package com.movielibrary.dao;

import com.movielibrary.config.DatabaseConfig;
import com.movielibrary.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Tìm kiếm người dùng dựa trên username.
     * Dùng để kiểm tra đăng nhập hoặc kiểm tra trùng lặp khi đăng ký.
     */
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setSalt(rs.getString("salt"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Thêm mới một người dùng vào cơ sở dữ liệu.
     * @return true nếu thêm thành công, false nếu username đã tồn tại.
     */
    public boolean createUser(User user) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getSalt());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Lỗi 1062 của MySQL là lỗi vi phạm ràng buộc UNIQUE (trùng username)
            if (e.getErrorCode() == 1062) {
                return false;
            }
            throw e; // Ném ra các lỗi SQL khác để Service xử lý
        }
    }
}