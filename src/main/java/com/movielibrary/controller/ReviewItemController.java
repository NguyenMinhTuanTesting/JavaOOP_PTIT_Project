package com.movielibrary.controller;

import com.movielibrary.model.Review;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;

public class ReviewItemController {

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblRating;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblComment;

    /**
     * Đổ dữ liệu từ Object Review vào các Label giao diện
     * @param review Đối tượng Review chứa dữ liệu bình luận
     */
    public void setReviewData(Review review) {
        // Tên người dùng đánh giá
        if (review.getUsername() != null && !review.getUsername().isEmpty()) {
            lblUsername.setText(review.getUsername());
        } else {
            lblUsername.setText("Người dùng ẩn danh");
        }

        // Định dạng điểm đánh giá (VD: 8.5 / 10)
        lblRating.setText(String.format("%.1f / 10", review.getRating()));

        // Định dạng Ngày giờ đăng
        if (review.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            lblDate.setText(sdf.format(review.getCreatedAt()));
        } else {
            lblDate.setText("");
        }

        // Nội dung bình luận
        if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
            lblComment.setText(review.getComment());
        } else {
            // Nếu không có nội dung, hiển thị chữ nghiêng mờ
            lblComment.setText("(Không có nội dung bình luận)");
            lblComment.setStyle("-fx-font-style: italic; -fx-text-fill: #888888;");
        }
    }
}