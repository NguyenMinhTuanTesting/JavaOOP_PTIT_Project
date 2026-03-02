package com.movielibrary.dao;

import com.movielibrary.config.DatabaseConfig;
import com.movielibrary.model.Media;
import com.movielibrary.model.Movie;
import com.movielibrary.model.Series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MediaDAO {

    /**
     * Hàm helper để map 1 dòng dữ liệu (ResultSet) từ MySQL thành đối tượng Media (Movie hoặc Series).
     */
    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        Media media;

        if ("MOVIE".equalsIgnoreCase(type)) {
            Movie movie = new Movie();
            movie.setDuration(rs.getInt("duration"));
            media = movie;
        } else {
            Series series = new Series();
            series.setEpisodes(rs.getInt("episodes"));
            media = series;
        }

        media.setId(rs.getString("id"));
        media.setType(type);
        media.setTitle(rs.getString("title"));
        media.setPosterUrl(rs.getString("poster_url"));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setGenres(rs.getString("genres"));
        media.setCountry(rs.getString("country"));
        media.setDirector(rs.getString("director"));
        media.setCasts(rs.getString("casts"));
        media.setDescription(rs.getString("description"));

        return media;
    }

    /**
     * Lấy danh sách TẤT CẢ các Thể loại (Genre) hiện có trong hệ thống bằng cách
     * bóc tách chuỗi (ví dụ: "Action;Sci-Fi") từ tất cả các phim.
     */
    public List<String> getAllGenres() throws Exception {
        Set<String> genreSet = new HashSet<>();
        String sql = "SELECT genres FROM media";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String genresStr = rs.getString("genres");
                if (genresStr != null && !genresStr.trim().isEmpty()) {
                    String[] arr = genresStr.split(";");
                    for (String g : arr) {
                        genreSet.add(g.trim());
                    }
                }
            }
        }

        List<String> sortedGenres = new ArrayList<>(genreSet);
        Collections.sort(sortedGenres); // Sắp xếp A-Z cho đẹp trên ComboBox
        return sortedGenres;
    }

    /**
     * Lấy thông tin 1 bộ phim/series dựa vào ID
     */
    public Media getMediaById(String id) throws Exception {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedia(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lấy TỔNG SỐ bộ phim thỏa mãn điều kiện tìm kiếm và bộ lọc (để tính tổng số trang).
     */
    public int getTotalCount(String search, String filter) throws Exception {
        String sql = "SELECT COUNT(*) FROM media WHERE LOWER(title) LIKE LOWER(?) ";
        if (filter != null && !filter.equalsIgnoreCase("All")) {
            sql += " AND genres LIKE ? ";
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + (search == null ? "" : search) + "%");
            if (filter != null && !filter.equalsIgnoreCase("All")) {
                pstmt.setString(2, "%" + filter + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Truy vấn danh sách Phim với đầy đủ tính năng: TÌM KIẾM, LỌC THỂ LOẠI, SẮP XẾP, PHÂN TRANG.
     */
    public List<Media> getPagedMedia(String search, String filter, String sort, int page, int itemsPerPage) throws Exception {
        List<Media> list = new ArrayList<>();
        int offset = (page - 1) * itemsPerPage;

        // Câu lệnh SQL LEFT JOIN để tính Rating trung bình cho từng phim
        String sql = "SELECT m.*, COALESCE(AVG(r.rating), 0) AS avg_rating " +
                "FROM media m LEFT JOIN reviews r ON m.id = r.media_id " +
                "WHERE LOWER(m.title) LIKE LOWER(?) ";

        if (filter != null && !filter.equalsIgnoreCase("All")) {
            sql += " AND m.genres LIKE ? ";
        }

        sql += " GROUP BY m.id ";

        // Xử lý Sort theo Requirement
        if ("Title A-Z".equals(sort)) {
            sql += " ORDER BY m.title ASC ";
        } else if ("Rating ↓".equals(sort)) {
            sql += " ORDER BY avg_rating DESC ";
        } else {
            // Default: Release Year ↓
            sql += " ORDER BY m.release_year DESC ";
        }

        sql += " LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            // Tham số Tìm kiếm
            pstmt.setString(paramIndex++, "%" + (search == null ? "" : search) + "%");

            // Tham số Bộ lọc
            if (filter != null && !filter.equalsIgnoreCase("All")) {
                pstmt.setString(paramIndex++, "%" + filter + "%");
            }

            // Tham số Phân trang
            pstmt.setInt(paramIndex++, itemsPerPage);
            pstmt.setInt(paramIndex++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedia(rs));
                }
            }
        }
        return list;
    }
}