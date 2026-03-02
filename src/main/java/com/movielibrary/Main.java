package com.movielibrary;

import com.movielibrary.service.CSVImportService;
import com.movielibrary.util.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Thiết lập các thông số cơ bản cho Cửa sổ chính (Stage)
            primaryStage.setTitle("Thư Viện Điện Ảnh - JavaFX");
            primaryStage.setResizable(false); // Cố định kích thước để giao diện không bị vỡ

            // 2. Giao Stage này cho SceneManager quản lý (Đảm bảo chuẩn kiến trúc Single Stage)
            SceneManager.getInstance().setPrimaryStage(primaryStage);

            // 3. Khởi tạo và nạp dữ liệu Master Data từ file CSV vào Database
            // (Sử dụng INSERT IGNORE bên trong hàm nên bạn cứ yên tâm chạy bao nhiêu lần cũng được)
            System.out.println("Đang kiểm tra và nạp dữ liệu CSV...");
            CSVImportService csvService = new CSVImportService();
            csvService.importMediaData();

            // 4. Mở màn hình đầu tiên: Trang Đăng Nhập
            SceneManager.getInstance().switchScene(null, "/com/movielibrary/fxml/Login.fxml");

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi khởi động ứng dụng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Lệnh khởi chạy vòng đời của một ứng dụng JavaFX
        launch(args);
    }
}