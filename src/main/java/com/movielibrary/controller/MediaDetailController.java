package com.movielibrary.controller;

import com.movielibrary.model.Media;
import com.movielibrary.model.Review;
import com.movielibrary.model.User;
import com.movielibrary.service.MediaService;
import com.movielibrary.service.ReviewService;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MediaDetailController {

    // Biến lưu trữ ID phim được chọn từ màn hình MediaList truyền sang
    public static String selectedMediaId = null;

    @FXML private ImageView imgPoster;
    @FXML private Label lblTitle, lblType, lblYear, lblDuration, lblCountry, lblDirector, lblCast, lblDescription, lblRating;
    @FXML private FlowPane genreTagsPane;

    // Vùng chứa danh sách Review và Form thêm Review
    @FXML private VBox reviewListContainer;
    @FXML private VBox reviewFormContainer;

    // Các UI Control cho Form Review
    @FXML private Slider sliderRating;
    @FXML private Label lblRatingValue;
    @FXML private TextArea txtComment;
    @FXML private Button btnSubmitReview;

    private final MediaService mediaService;
    private final ReviewService reviewService;
    private Media currentMedia;

    public MediaDetailController() {
        this.mediaService = new MediaService();
        this.reviewService = new ReviewService();
    }

    @FXML
    public void initialize() {
        if (selectedMediaId == null) {
            AlertUtil.showError("Lỗi dữ liệu", "Không tìm thấy mã định danh của phim!");
            handleBack();
            return;
        }

        loadMediaDetails();
        loadReviews();
        checkAndSetupReviewForm();

        // Listener để cập nhật giá trị rating realtime từ Slider, ép bước nhảy 0.5
        sliderRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rounded = Math.round(newValue.doubleValue() * 2) / 2.0;
            sliderRating.setValue(rounded);
            lblRatingValue.setText(String.format("%.1f / 10.0", rounded));
        });
    }

    private void loadMediaDetails() {
        try {
            currentMedia = mediaService.getMediaById(selectedMediaId);
            if (currentMedia == null) return;

            lblTitle.setText(currentMedia.getTitle());
            lblType.setText(currentMedia.getType());
            lblYear.setText(String.valueOf(currentMedia.getReleaseYear()));

            // Xử lý Duration (Phút) hoặc Episodes (Số tập)
            String durationText = currentMedia.getType().equalsIgnoreCase("MOVIE")
                    ? currentMedia.getDuration() + " Phút"
                    : currentMedia.getEpisodes() + " Tập";
            lblDuration.setText(durationText);

            lblCountry.setText(currentMedia.getCountry());
            lblDirector.setText(currentMedia.getDirector());
            lblCast.setText(currentMedia.getCasts());
            lblDescription.setText(currentMedia.getDescription());

            // Tải hình ảnh an toàn, không làm crash app nếu link URL lỗi
            if (currentMedia.getPosterUrl() != null && !currentMedia.getPosterUrl().isEmpty()) {
                Image image = new Image(currentMedia.getPosterUrl(), true); // true = load background
                imgPoster.setImage(image);
            }

            // Xử lý chuỗi Tag Thể loại (Genre) thành các Nút bấm
            genreTagsPane.getChildren().clear();
            if (currentMedia.getGenres() != null && !currentMedia.getGenres().isEmpty()) {
                String[] genres = currentMedia.getGenres().split(";");
                for (String genre : genres) {
                    String cleanGenre = genre.trim();
                    Button btnTag = new Button(cleanGenre);
                    btnTag.getStyleClass().add("tag-button");
                    btnTag.setOnAction(e -> handleGenreClick(cleanGenre));
                    genreTagsPane.getChildren().add(btnTag);
                }
            }

            // Tính toán và làm tròn Average Rating hiển thị lên UI
            double avgRating = reviewService.getAverageRating(selectedMediaId);
            if (avgRating > 0) {
                double rounded = Math.round(avgRating * 2) / 2.0;
                lblRating.setText(String.format("%.1f / 10", rounded));
            } else {
                lblRating.setText("Chưa có đánh giá");
            }
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Lỗi khi tải dữ liệu phim: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReviews() {
        reviewListContainer.getChildren().clear();
        try {
            List<Review> reviews = reviewService.getReviewsForMedia(selectedMediaId);

            if (reviews.isEmpty()) {
                Label emptyLabel = new Label("Chưa có bình luận nào. Hãy là người đầu tiên đánh giá!");
                emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
                reviewListContainer.getChildren().add(emptyLabel);
                return;
            }

            // Khởi tạo từng Review Item bọc trong FXML con
            for (Review r : reviews) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/movielibrary/fxml/ReviewItem.fxml"));
                Node reviewNode = loader.load();
                ReviewItemController controller = loader.getController();
                controller.setReviewData(r);
                reviewListContainer.getChildren().add(reviewNode);
            }
        } catch (IOException e) {
            AlertUtil.showError("Lỗi giao diện", "Không thể tải danh sách bình luận.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndSetupReviewForm() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Theo Requirement: Không hiện Form Review cho Guest
        if (currentUser == null) {
            hideReviewForm();
            return;
        }

        try {
            // Theo Requirement: Không hiện Form Review nếu User đã review phim này rồi
            boolean hasReviewed = reviewService.hasUserReviewed(currentUser.getId(), selectedMediaId);
            if (hasReviewed) {
                hideReviewForm();
            } else {
                showReviewForm();
            }
        } catch (Exception e) {
            hideReviewForm();
            e.printStackTrace();
        }
    }

    private void hideReviewForm() {
        reviewFormContainer.setVisible(false);
        reviewFormContainer.setManaged(false); // Xóa khoảng trống của VBox khỏi Layout
    }

    private void showReviewForm() {
        reviewFormContainer.setVisible(true);
        reviewFormContainer.setManaged(true);
        sliderRating.setValue(10.0);
        lblRatingValue.setText("10.0 / 10.0");
        txtComment.setText("");
    }

    @FXML
    private void handleSubmitReview() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        double rating = sliderRating.getValue();
        String comment = txtComment.getText().trim();

        if (comment.length() > 1000) {
            AlertUtil.showWarning("Lỗi định dạng", "Bình luận không được vượt quá 1000 ký tự!");
            return;
        }

        Review review = new Review();
        review.setUserId(currentUser.getId());
        review.setMediaId(selectedMediaId);
        review.setRating(rating);
        review.setComment(comment);

        try {
            reviewService.addReview(review);
            AlertUtil.showInfo("Thành công", "Đánh giá của bạn đã được ghi nhận.");
            // Theo requirement: Cập nhật lại Rating Avg và Ẩn form vĩnh viễn
            loadMediaDetails();
            loadReviews();
            checkAndSetupReviewForm();
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Không thể gửi đánh giá: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        // Trở về trang danh sách thông qua MainLayoutController
        // Các state như Search, Sort, Pagination đang được giữ tĩnh ở MediaListController
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().loadCenterContent("/com/movielibrary/fxml/MediaList.fxml");
        }
    }

    private void handleGenreClick(String genre) {
        // Requirement: Click Tag -> Chuyển về List Page, Áp dụng filter, Reset Page 1
        MediaListController.setFilterFromOutside(genre);
        handleBack();
    }
}