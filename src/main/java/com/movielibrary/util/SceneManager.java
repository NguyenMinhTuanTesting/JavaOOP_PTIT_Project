package com.movielibrary.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lớp quản lý việc chuyển đổi các màn hình (Scene) trong ứng dụng.
 * Đảm bảo ứng dụng tuân thủ kiến trúc Single Stage (Chỉ dùng 1 cửa sổ duy nhất).
 */
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {
        // Private constructor để tuân thủ Singleton pattern
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Lưu trữ Stage chính của ứng dụng.
     * Hàm này sẽ được gọi 1 lần duy nhất ở file Main.java khi ứng dụng khởi động.
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Chuyển đổi toàn bộ màn hình (Scene) hiện tại sang một giao diện FXML mới.
     * * @param source Node nguồn (ví dụ: nút bấm) đã gọi sự kiện. Dùng để lấy Stage hiện tại.
     * @param fxmlPath Đường dẫn tới file giao diện FXML cần tải.
     * @throws Exception Bắn ra lỗi nếu file FXML không tồn tại hoặc lỗi khởi tạo.
     */
    public void switchScene(Node source, String fxmlPath) throws Exception {
        Stage stage = null;

        // Ưu tiên lấy Stage từ Node nguồn vừa tương tác (chính xác nhất)
        if (source != null && source.getScene() != null && source.getScene().getWindow() != null) {
            stage = (Stage) source.getScene().getWindow();
        }
        // Nếu không có Node nguồn, sử dụng Stage chính đã lưu
        else if (primaryStage != null) {
            stage = primaryStage;
        }

        if (stage == null) {
            throw new IllegalStateException("Không tìm thấy Stage hiện tại để thực hiện chuyển Scene!");
        }

        // Tải giao diện FXML mới
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Khởi tạo Scene mới với Root vừa tải
        Scene newScene = new Scene(root);

        // Tự động nhúng file CSS mặc định (nếu tồn tại) vào Scene mới
        java.net.URL cssUrl = getClass().getResource("/com/movielibrary/css/style.css");
        if (cssUrl != null) {
            newScene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Thay thế Scene cũ bằng Scene mới trên cùng 1 Stage
        stage.setScene(newScene);

        // Căn giữa cửa sổ lại cho đẹp mắt nếu kích thước thay đổi
        stage.centerOnScreen();
        stage.show();
    }
}