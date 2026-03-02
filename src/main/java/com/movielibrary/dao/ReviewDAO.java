package com.movielibrary.dao;

import com.movielibrary.config.DatabaseConfig;
import com.movielibrary.model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    /**
     * Lấy danh sách bình luận của một bộ phim.
     * JOIN với bảng users để lấy username và Sắp xếp mới nhất lên đầu.
     */
    public List<Review> getReviewsByMediaId(String mediaId) throws Exception {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.media_id = ? " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mediaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setId(rs.getInt("id"));
                    review.setUserId(rs.getInt("user_id"));
                    review.setMediaId(rs.getString("media_id"));
                    review.setRating(rs.getDouble("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));

                    // Gán thêm thông tin Username để hiển thị lên UI
                    review.setUsername(rs.getString("username"));

                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    /**
     * Thêm một đánh giá mới vào Database.
     */
    public boolean addReview(Review review) throws Exception {
        String sql = "INSERT INTO reviews (user_id, media_id, rating, comment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, review.getUserId());
            pstmt.setString(2, review.getMediaId());
            pstmt.setDouble(3, review.getRating());
            pstmt.setString(4, review.getComment());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Kiểm tra xem một người dùng đã từng đánh giá bộ phim này chưa.
     * (Để ẩn form Review theo Requirement)
     */
    public boolean hasUserReviewed(int userId, String mediaId) throws Exception {
        String sql = "SELECT 1 FROM reviews WHERE user_id = ? AND media_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, mediaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Nếu có dữ liệu trả về tức là đã review
            }
        }
    }

    /**
     * Lấy điểm đánh giá trung bình của một bộ phim.
     */
    public double getAverageRating(String mediaId) throws Exception {
        String sql = "SELECT AVG(rating) FROM reviews WHERE media_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mediaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
}