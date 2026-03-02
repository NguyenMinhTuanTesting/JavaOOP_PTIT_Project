package com.movielibrary.controller;

import com.movielibrary.model.Media;
import com.movielibrary.service.MediaService;
import com.movielibrary.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MediaListController {

    // BIẾN STATE (Static) - Nhiệm vụ: Giữ nguyên trạng thái khi người dùng ấn Back từ trang Detail
    private static String currentSearch = "";
    private static String currentFilter = "All";
    private static String currentSort = "Release Year ↓";
    private static int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 10;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbFilterGenre;
    @FXML private ComboBox<String> cbSort;
    @FXML private FlowPane mediaContainer;
    @FXML private Label lblPageInfo;
    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;

    private final MediaService mediaService;
    private int totalPages = 1;

    public MediaListController() {
        this.mediaService = new MediaService();
    }

    /**
     * Hàm này được MediaDetailController gọi khi người dùng click vào một Filter Tag bất kỳ.
     * Thay vì chỉ set Genre như cũ, ta sẽ gán từ khóa vào ô Tìm kiếm (Search)
     * để DAO có thể tìm trên toàn bộ các cột (Title, Director, Casts, Year...).
     */
    public static void setFilterFromOutside(String keyword) {
        currentSearch = keyword; // Gắn tag được click vào thanh tìm kiếm
        currentFilter = "All";   // Reset combo box thể loại về All để mở rộng phạm vi
        currentPage = 1;         // Luôn đưa về trang 1
    }

    @FXML
    public void initialize() {
        setupComboBoxes();

        // Phục hồi lại State trên UI (cho trường hợp ấn Back từ trang chi tiết về, hoặc nhảy từ click Tag)
        txtSearch.setText(currentSearch);
        cbFilterGenre.setValue(currentFilter);
        cbSort.setValue(currentSort);

        // Lắng nghe sự thay đổi của ComboBox để tự động lọc mà không cần bấm nút Submit
        cbFilterGenre.setOnAction(e -> handleSearchAndFilter());
        cbSort.setOnAction(e -> handleSearchAndFilter());

        // Tải dữ liệu lần đầu
        loadMediaData();
    }

    private void setupComboBoxes() {
        try {
            // Tải danh sách Thể loại (Genres) động từ Database để đưa vào ComboBox
            List<String> genres = mediaService.getAllGenres();
            genres.add(0, "All");
            cbFilterGenre.setItems(FXCollections.observableArrayList(genres));

            // Khởi tạo các tùy chọn Sắp xếp theo đúng Requirement
            cbSort.setItems(FXCollections.observableArrayList(
                    "Release Year ↓",
                    "Title A-Z",
                    "Rating ↓"
            ));
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Không thể tải bộ lọc dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchAndFilter() {
        // Cập nhật State
        currentSearch = txtSearch.getText().trim();
        currentFilter = cbFilterGenre.getValue() != null ? cbFilterGenre.getValue() : "All";
        currentSort = cbSort.getValue() != null ? cbSort.getValue() : "Release Year ↓";
        currentPage = 1; // Luôn reset về trang 1 khi thay đổi điều kiện lọc

        loadMediaData();
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadMediaData();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadMediaData();
        }
    }

    private void loadMediaData() {
        try {
            // 1. Tính toán lại tổng số trang dựa trên điều kiện lọc hiện tại
            totalPages = mediaService.getTotalPages(currentSearch, currentFilter, ITEMS_PER_PAGE);
            if (totalPages == 0) totalPages = 1; // Đảm bảo UI luôn hiện ít nhất "Trang 1 / 1"

            // Ràng buộc lại currentPage đề phòng trường hợp Filter thu hẹp kết quả
            if (currentPage > totalPages) currentPage = totalPages;

            // 2. Lấy danh sách phim cho trang hiện tại
            List<Media> mediaList = mediaService.getPagedMedia(currentSearch, currentFilter, currentSort, currentPage, ITEMS_PER_PAGE);

            // 3. Cập nhật Giao diện
            updateUI(mediaList);
            updatePaginationControls();
        } catch (Exception e) {
            AlertUtil.showError("Lỗi tải dữ liệu", "Đã xảy ra lỗi khi truy xuất danh sách phim: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePaginationControls() {
        lblPageInfo.setText(String.format("Trang %d / %d", currentPage, totalPages));
        btnPrevPage.setDisable(currentPage <= 1);
        btnNextPage.setDisable(currentPage >= totalPages);
    }

    private void updateUI(List<Media> mediaList) {
        mediaContainer.getChildren().clear();

        if (mediaList == null || mediaList.isEmpty()) {
            Label emptyLabel = new Label("Không tìm thấy kết quả nào phù hợp.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-padding: 30px;");
            mediaContainer.getChildren().add(emptyLabel);
            return;
        }

        // Tạo giao diện Card cho từng bộ phim
        for (Media media : mediaList) {
            VBox card = createMediaCard(media);
            mediaContainer.getChildren().add(card);
        }
    }

    private VBox createMediaCard(Media media) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("media-card"); // Sẽ được style làm đẹp trong file CSS sau
        card.setPrefWidth(160);
        card.setPrefHeight(260);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(150);
        imgView.setFitHeight(210);
        imgView.setPreserveRatio(false);

        try {
            // Load hình ảnh (true = tải ngầm không làm đơ giao diện)
            if (media.getPosterUrl() != null && !media.getPosterUrl().isEmpty()) {
                Image img = new Image(media.getPosterUrl(), true);
                imgView.setImage(img);
            }
        } catch (Exception e) {
            // Nếu link ảnh lỗi, ImageView sẽ tự động để trống, không làm crash App
        }

        Label lblTitle = new Label(media.getTitle());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        lblTitle.setWrapText(true);
        lblTitle.setMaxWidth(150);
        lblTitle.setAlignment(Pos.CENTER);

        Label lblYear = new Label(String.valueOf(media.getReleaseYear()));
        lblYear.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        card.getChildren().addAll(imgView, lblTitle, lblYear);

        // Sự kiện quan trọng: Click vào Card sẽ mở trang Chi tiết phim
        card.setOnMouseClicked(event -> {
            MediaDetailController.selectedMediaId = media.getId();
            MainLayoutController.getInstance().loadCenterContent("/com/movielibrary/fxml/MediaDetail.fxml");
        });

        return card;
    }
}