package com.movielibrary.util;

import com.movielibrary.model.User;

/**
 * Lớp quản lý phiên đăng nhập của người dùng.
 * Sử dụng Singleton pattern để duy trì trạng thái xuyên suốt quá trình chạy ứng dụng.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Private constructor để chặn việc khởi tạo bằng từ khóa new từ bên ngoài
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Lấy thông tin người dùng hiện đang đăng nhập.
     * @return Đối tượng User, hoặc null nếu đang ở chế độ Guest (Chưa đăng nhập).
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Cập nhật thông tin người dùng đăng nhập.
     * @param currentUser Đối tượng User (Truyền null khi muốn Đăng xuất).
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}