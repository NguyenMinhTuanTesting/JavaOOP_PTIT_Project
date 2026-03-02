package com.movielibrary.controller;

import com.movielibrary.service.UserService;
import com.movielibrary.util.AlertUtil;
import com.movielibrary.util.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegister;
    @FXML private Button btnBack;

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        // Reset các trường nhập liệu khi form được bật lên
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtil.showWarning("Thiếu thông tin", "Vui lòng điền đầy đủ tất cả các trường!");
            return;
        }

        if (username.length() < 4) {
            AlertUtil.showWarning("Lỗi định dạng", "Tên đăng nhập phải có ít nhất 4 ký tự!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.showWarning("Lỗi mật khẩu", "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            // Đẩy xuống tầng Service để xử lý Hash Password và Insert DB
            boolean success = userService.registerUser(username, password);
            if (success) {
                AlertUtil.showInfo("Thành công", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
                handleBackToLogin(event);
            } else {
                AlertUtil.showError("Thất bại", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên đăng nhập khác!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Lỗi hệ thống", "Đã xảy ra lỗi khi đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // Chuyển lại Scene về trang Đăng nhập
            Node source = (Node) event.getSource();
            SceneManager.getInstance().switchScene(source, "/com/movielibrary/fxml/Login.fxml");
        } catch (Exception e) {
            AlertUtil.showError("Lỗi giao diện", "Không thể quay lại trang Đăng nhập!");
            e.printStackTrace();
        }
    }
}