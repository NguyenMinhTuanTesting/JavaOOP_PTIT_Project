package com.movielibrary.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // CẤU HÌNH KẾT NỐI DATABASE
    private static final String URL = "jdbc:mysql://localhost:3306/movie_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "**********";

    /**
     * Khởi tạo và trả về một đối tượng Connection kết nối tới MySQL.
     * * @return Connection đối tượng kết nối
     * @throws SQLException Ném ra lỗi nếu kết nối thất bại (sai user, pass, hoặc DB chưa bật)
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Đảm bảo MySQL JDBC Driver đã được load vào bộ nhớ
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL JDBC Driver. Hãy kiểm tra lại file pom.xml!", e);
        }
    }
}