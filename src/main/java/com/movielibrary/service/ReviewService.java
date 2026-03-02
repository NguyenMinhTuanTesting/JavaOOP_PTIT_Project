package com.movielibrary.service;

import com.movielibrary.dao.ReviewDAO;
import com.movielibrary.model.Review;

import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
    }

    /**
     * Lấy danh sách toàn bộ bình luận của một bộ phim.
     */
    public List<Review> getReviewsForMedia(String mediaId) throws Exception {
        return reviewDAO.getReviewsByMediaId(mediaId);
    }

    /**
     * Thêm một bình luận mới vào hệ thống.
     */
    public boolean addReview(Review review) throws Exception {
        return reviewDAO.addReview(review);
    }

    /**
     * Kiểm tra xem người dùng đã review bộ phim này chưa.
     */
    public boolean hasUserReviewed(int userId, String mediaId) throws Exception {
        return reviewDAO.hasUserReviewed(userId, mediaId);
    }

    /**
     * Lấy điểm đánh giá trung bình của một bộ phim.
     */
    public double getAverageRating(String mediaId) throws Exception {
        return reviewDAO.getAverageRating(mediaId);
    }
}