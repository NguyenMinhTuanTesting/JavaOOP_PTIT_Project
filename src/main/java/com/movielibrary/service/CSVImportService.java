package com.movielibrary.service;

import com.movielibrary.config.DatabaseConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class CSVImportService {

    /**
     * Hàm chính để đọc file CSV và import dữ liệu vào Database.
     * Hàm này sẽ được gọi 1 lần khi ứng dụng khởi động (trong Main.java).
     */
    public void importMediaData() {
        String csvFilePath = "/data/Media_Data.csv";

        // Dùng INSERT IGNORE: Nếu ID phim (media_001...) đã tồn tại, nó sẽ bỏ qua không báo lỗi
        String sql = "INSERT IGNORE INTO media (id, type, title, poster_url, release_year, genres, duration, episodes, country, director, casts, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (InputStream is = getClass().getResourceAsStream(csvFilePath)) {
            if (is == null) {
                System.out.println("CẢNH BÁO: Không tìm thấy file CSV tại " + csvFilePath + ". Bỏ qua bước import data.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String line;
                boolean isFirstLine = true;
                int count = 0;

                while ((line = br.readLine()) != null) {
                    // Bỏ qua dòng Header đầu tiên
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    // Bỏ qua các dòng trống
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Parse dòng CSV bằng thuật toán chuyên dụng
                    List<String> columns = parseCsvLine(line);

                    // Đảm bảo có đủ ít nhất 12 cột dữ liệu (cột 13 có thể là chuỗi rỗng do dấu ; cuối dòng)
                    if (columns.size() >= 12) {
                        pstmt.setString(1, columns.get(0).trim()); // id
                        pstmt.setString(2, columns.get(1).trim()); // type
                        pstmt.setString(3, columns.get(2).trim()); // title
                        pstmt.setString(4, columns.get(3).trim()); // poster_url

                        // Xử lý cẩn thận các cột số (Integer) để tránh lỗi NumberFormatException
                        pstmt.setInt(5, parseInteger(columns.get(4))); // release_year
                        pstmt.setString(6, columns.get(5).trim()); // genres
                        pstmt.setInt(7, parseInteger(columns.get(6))); // duration
                        pstmt.setInt(8, parseInteger(columns.get(7))); // episodes

                        pstmt.setString(9, columns.get(8).trim()); // country
                        pstmt.setString(10, columns.get(9).trim()); // director
                        pstmt.setString(11, columns.get(10).trim()); // casts
                        pstmt.setString(12, columns.get(11).trim()); // description

                        pstmt.addBatch();
                        count++;

                        // Thực thi theo từng lô 50 dòng để tối ưu hiệu suất bộ nhớ
                        if (count % 50 == 0) {
                            pstmt.executeBatch();
                        }
                    }
                }

                // Thực thi lô cuối cùng còn sót lại
                pstmt.executeBatch();
                System.out.println("CSV Import hoàn tất: Đã kiểm tra và nạp thành công dữ liệu Master Data.");

            }
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi import dữ liệu CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Thuật toán tách chuỗi CSV chuyên dụng.
     * Giải quyết bài toán: Dấu chấm phẩy (;) nằm bên trong cặp ngoặc kép ("") thì KHÔNG được tách.
     */
    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Lật trạng thái trong/ngoài ngoặc kép, không đưa dấu ngoặc vào kết quả
                inQuotes = !inQuotes;
            } else if (c == ';' && !inQuotes) {
                // Nếu gặp dấu ; và ĐANG KHÔNG ở trong ngoặc kép -> Kết thúc 1 cột
                result.add(currentToken.toString());
                currentToken.setLength(0); // Reset chuỗi
            } else {
                // Các ký tự bình thường (bao gồm cả ; nếu đang trong ngoặc kép)
                currentToken.append(c);
            }
        }

        // Thêm cột cuối cùng vào danh sách
        result.add(currentToken.toString());

        return result;
    }

    /**
     * Hàm helper để parse String sang Integer một cách an toàn.
     */
    private int parseInteger(String str) {
        try {
            String cleanStr = str.trim();
            if (cleanStr.isEmpty()) return 0;
            return Integer.parseInt(cleanStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}