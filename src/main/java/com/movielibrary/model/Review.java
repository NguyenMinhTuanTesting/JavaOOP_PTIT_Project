package com.movielibrary.model;

import java.sql.Timestamp;

/**
 * Lớp đại diện cho một Đánh giá/Bình luận của người dùng về một bộ phim.
 */
public class Review {
    private int id;
    private int userId;
    private String mediaId;
    private double rating;
    private String comment;
    private Timestamp createdAt;

    // Thuộc tính phụ trợ (Transient): Dùng để hiển thị lên UI sau khi JOIN với bảng Users
    private String username;

    public Review() {
    }

    // --- GETTERS & SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}