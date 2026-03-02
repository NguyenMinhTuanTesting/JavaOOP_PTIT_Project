package com.movielibrary.controller;

import com.movielibrary.model.User;
import com.movielibrary.service.UserService;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.SceneManager;
import com.movielibrary.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        // Hàm chạy ngay khi FXML được load.
        // Có thể reset các trường input nếu cần thiết.
        txtUsername.setText("");
        txtPassword.setText("");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showWarning("Thiếu thông tin", "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        try {
            // Thực hiện xác thực người dùng qua Service Layer
            User loggedInUser = userService.authenticate(username, password);

            if (loggedInUser != null) {
                // Đăng nhập thành công -> Lưu thông tin User vào Session
                SessionManager.getInstance().setCurrentUser(loggedInUser);

                // Chuyển sang màn hình Main Layout
                Node source = (Node) event.getSource();
                SceneManager.getInstance().switchScene(source, "/com/movielibrary/fxml/MainLayout.fxml");
            } else {
                // Đăng nhập thất bại -> Hiển thị thông báo lỗi
                AlertUtil.showError("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Đã xảy ra lỗi trong quá trình xác thực: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterClick(ActionEvent event) {
        try {
            // Chuyển sang màn hình Đăng ký
            Node source = (Node) event.getSource();
            SceneManager.getInstance().switchScene(source, "/com/movielibrary/fxml/Register.fxml");
        } catch (Exception e) {
            AlertUtil.showError("Lỗi giao diện", "Không thể tải trang Đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGuestClick(ActionEvent event) {
        try {
            // Chế độ Guest -> Gán User trong Session thành null
            SessionManager.getInstance().setCurrentUser(null);

            // Chuyển sang màn hình Main Layout
            Node source = (Node) event.getSource();
            SceneManager.getInstance().switchScene(source, "/com/movielibrary/fxml/MainLayout.fxml");
        } catch (Exception e) {
            AlertUtil.showError("Lỗi giao diện", "Không thể tải trang Chính: " + e.getMessage());
            e.printStackTrace();
        }
    }
}