package com.movielibrary.controller;

import com.movielibrary.model.Media;
import com.movielibrary.service.MediaService;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.ImageLoaderUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MediaListController {

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

    public static void setFilterFromOutside(String keyword) {
        currentSearch = keyword;
        currentFilter = "All";
        currentPage = 1;
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        txtSearch.setText(currentSearch);
        cbFilterGenre.setValue(currentFilter);
        cbSort.setValue(currentSort);

        cbFilterGenre.setOnAction(e -> handleSearchAndFilter());
        cbSort.setOnAction(e -> handleSearchAndFilter());

        loadMediaData();
    }

    private void setupComboBoxes() {
        try {
            List<String> genres = mediaService.getAllGenres();
            genres.add(0, "All");
            cbFilterGenre.setItems(FXCollections.observableArrayList(genres));

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
        currentSearch = txtSearch.getText().trim();
        currentFilter = cbFilterGenre.getValue() != null ? cbFilterGenre.getValue() : "All";
        currentSort = cbSort.getValue() != null ? cbSort.getValue() : "Release Year ↓";
        currentPage = 1;
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
            totalPages = mediaService.getTotalPages(currentSearch, currentFilter, ITEMS_PER_PAGE);
            if (totalPages == 0) totalPages = 1;

            if (currentPage > totalPages) currentPage = totalPages;

            List<Media> mediaList = mediaService.getPagedMedia(currentSearch, currentFilter, currentSort, currentPage, ITEMS_PER_PAGE);

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

        for (Media media : mediaList) {
            VBox card = createMediaCard(media);
            mediaContainer.getChildren().add(card);
        }
    }

    private VBox createMediaCard(Media media) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("media-card");
        card.setPrefWidth(160);
        card.setPrefHeight(260);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(150);
        imgView.setFitHeight(210);
        imgView.setPreserveRatio(false);

        // GỌI TIỆN ÍCH TẢI ẢNH MỚI (CHỈ 1 DÒNG)
        ImageLoaderUtil.load(imgView, media.getPosterUrl(), 150, 210);

        Label lblTitle = new Label(media.getTitle());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        lblTitle.setWrapText(true);
        lblTitle.setMaxWidth(150);
        lblTitle.setAlignment(Pos.CENTER);

        Label lblYear = new Label(String.valueOf(media.getReleaseYear()));
        lblYear.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        card.getChildren().addAll(imgView, lblTitle, lblYear);

        card.setOnMouseClicked(event -> {
            MediaDetailController.selectedMediaId = media.getId();
            MainLayoutController.getInstance().loadCenterContent("/com/movielibrary/fxml/MediaDetail.fxml");
        });

        return card;
    }
}