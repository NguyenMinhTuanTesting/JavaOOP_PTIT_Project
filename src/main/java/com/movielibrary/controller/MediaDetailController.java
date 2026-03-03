package com.movielibrary.controller;

import com.movielibrary.model.Media;
import com.movielibrary.model.Review;
import com.movielibrary.model.User;
import com.movielibrary.service.MediaService;
import com.movielibrary.service.ReviewService;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.ImageLoaderUtil;
import com.movielibrary.util.SessionManager;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class MediaDetailController {

    public static String selectedMediaId = null;

    @FXML private ImageView imgPoster;
    @FXML private Label lblTitle, lblDuration, lblDescription, lblRating;

    @FXML private FlowPane paneType, paneYear, genreTagsPane, paneCountry, paneDirector, paneCast;

    @FXML private VBox reviewListContainer;
    @FXML private VBox reviewFormContainer;

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

            // GỌI TIỆN ÍCH TẢI ẢNH MỚI (CHỈ 1 DÒNG)
            ImageLoaderUtil.load(imgPoster, currentMedia.getPosterUrl(), 280, 400);

            String durationText = currentMedia.getType().equalsIgnoreCase("MOVIE")
                    ? currentMedia.getDuration() + " Phút"
                    : currentMedia.getEpisodes() + " Tập";
            lblDuration.setText(durationText);

            lblDescription.setText(currentMedia.getDescription());

            populateTagLabels(paneType, currentMedia.getType(),
                    "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-weight: bold;", null);
            populateTagLabels(paneYear, String.valueOf(currentMedia.getReleaseYear()), null, "label-info");
            populateTagLabels(paneCountry, currentMedia.getCountry(), "-fx-text-fill: #c9d1d9;", null);
            populateTagLabels(paneDirector, currentMedia.getDirector(), "-fx-text-fill: #c9d1d9;", null);
            populateTagLabels(paneCast, currentMedia.getCasts(), "-fx-text-fill: #c9d1d9;", null);

            genreTagsPane.getChildren().clear();
            if (currentMedia.getGenres() != null && !currentMedia.getGenres().isEmpty()) {
                String[] genres = currentMedia.getGenres().split(";");
                for (String genre : genres) {
                    String cleanGenre = genre.trim();
                    Button btnTag = new Button(cleanGenre);
                    btnTag.getStyleClass().add("tag-button");
                    setupHoverAnimation(btnTag);
                    btnTag.setOnAction(e -> handleTagClick(cleanGenre));
                    genreTagsPane.getChildren().add(btnTag);
                }
            }

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

    private void populateTagLabels(FlowPane pane, String data, String inlineStyle, String styleClass) {
        pane.getChildren().clear();
        if (data == null || data.trim().isEmpty()) {
            Label empty = new Label("N/A");
            empty.setStyle("-fx-text-fill: #c9d1d9;");
            pane.getChildren().add(empty);
            return;
        }

        String[] items = data.split(";");
        for (String item : items) {
            String cleanItem = item.trim();
            if (!cleanItem.isEmpty()) {
                Label lbl = new Label(cleanItem);

                String finalStyle = "-fx-cursor: hand;";
                if (inlineStyle != null) finalStyle = inlineStyle + " " + finalStyle;
                lbl.setStyle(finalStyle);

                if (styleClass != null) lbl.getStyleClass().add(styleClass);

                setupHoverAnimation(lbl);
                lbl.setOnMouseClicked(e -> handleTagClick(cleanItem));

                pane.getChildren().add(lbl);
            }
        }
    }

    private void setupHoverAnimation(Node node) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), node);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), node);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        node.setOnMouseEntered(e -> {
            scaleIn.playFromStart();
            node.setOpacity(0.8);
        });
        node.setOnMouseExited(e -> {
            scaleOut.playFromStart();
            node.setOpacity(1.0);
        });
    }

    private void handleTagClick(String keyword) {
        MediaListController.setFilterFromOutside(keyword);
        handleBack();
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

        if (currentUser == null) {
            hideReviewForm();
            return;
        }

        try {
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
        reviewFormContainer.setManaged(false);
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
            loadMediaDetails();
            loadReviews();
            checkAndSetupReviewForm();
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Không thể gửi đánh giá: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().loadCenterContent("/com/movielibrary/fxml/MediaList.fxml");
        }
    }
}