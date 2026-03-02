package com.movielibrary.controller;

import com.movielibrary.model.User;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.SceneManager;
import com.movielibrary.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label lblWelcome;

    @FXML
    private Button btnAuth;

    // Singleton pattern cho phép các View con gọi MainLayout để thay đổi màn hình giữa
    private static MainLayoutController instance;

    public MainLayoutController() {
        instance = this;
    }

    public static MainLayoutController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        refreshAuthUI();
        // Mặc định khi load MainLayout, vùng trung tâm sẽ hiển thị Danh sách phim
        loadCenterContent("/com/movielibrary/fxml/MediaList.fxml");
    }

    /**
     * Cập nhật giao diện Header dựa trên trạng thái đăng nhập
     */
    public void refreshAuthUI() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            lblWelcome.setText("Xin chào, " + currentUser.getUsername() + "!");
            btnAuth.setText("Đăng xuất");
            btnAuth.getStyleClass().add("btn-logout");
        } else {
            lblWelcome.setText("Xin chào, Khách!");
            btnAuth.setText("Đăng nhập");
            btnAuth.getStyleClass().remove("btn-logout");
        }
    }

    /**
     * Xử lý sự kiện bấm nút Đăng nhập / Đăng xuất
     */
    @FXML
    private void handleAuthAction(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            // Đang đăng nhập -> Xử lý Đăng xuất
            SessionManager.getInstance().setCurrentUser(null);
            AlertUtil.showInfo("Thành công", "Bạn đã đăng xuất khỏi hệ thống.");
            refreshAuthUI();

            // Tải lại màn hình danh sách phim để reset các quyền (ẩn nút review, v.v.)
            loadCenterContent("/com/movielibrary/fxml/MediaList.fxml");
        } else {
            // Chưa đăng nhập -> Chuyển hướng hoàn toàn về màn hình Login
            try {
                Node source = (Node) event.getSource();
                SceneManager.getInstance().switchScene(source, "/com/movielibrary/fxml/Login.fxml");
            } catch (Exception e) {
                AlertUtil.showError("Lỗi hệ thống", "Không thể chuyển trang đăng nhập!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Hàm lõi để thực hiện Scene Switching lồng bên trong (Single Stage).
     * @param fxmlPath Đường dẫn tới file FXML cần load vào vùng trung tâm.
     */
    public void loadCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node centerNode = loader.load();
            borderPane.setCenter(centerNode);
        } catch (IOException e) {
            AlertUtil.showError("Lỗi giao diện", "Không thể tải thành phần giao diện: " + fxmlPath);
            e.printStackTrace();
        }
    }
}