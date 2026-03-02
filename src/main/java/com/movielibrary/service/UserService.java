package com.movielibrary.service;

import com.movielibrary.dao.UserDAO;
import com.movielibrary.model.User;
import com.movielibrary.util.PasswordUtil;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Nghiệp vụ xác thực người dùng (Đăng nhập).
     * @param username Tên đăng nhập người dùng nhập vào
     * @param plainPassword Mật khẩu thô người dùng nhập vào
     * @return Đối tượng User nếu đăng nhập thành công, hoặc null nếu sai thông tin
     */
    public User authenticate(String username, String plainPassword) throws Exception {
        // 1. Tìm user trong cơ sở dữ liệu dựa theo username
        User user = userDAO.findByUsername(username);

        // 2. Nếu không tồn tại user này -> Thất bại
        if (user == null) {
            return null;
        }

        // 3. Nếu tồn tại, kiểm tra mật khẩu thô với mã băm (hash) lưu trong DB
        boolean isPasswordMatch = PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash());

        if (isPasswordMatch) {
            return user; // Mật khẩu khớp -> Trả về thông tin User
        }

        return null; // Mật khẩu sai
    }

    /**
     * Nghiệp vụ tạo tài khoản mới (Đăng ký).
     * @param username Tên đăng nhập
     * @param plainPassword Mật khẩu thô
     * @return true nếu đăng ký thành công, false nếu username đã tồn tại
     */
    public boolean registerUser(String username, String plainPassword) throws Exception {
        // 1. Tạo chuỗi Salt ngẫu nhiên
        String salt = PasswordUtil.generateSalt();

        // 2. Băm mật khẩu cùng với Salt vừa tạo
        String passwordHash = PasswordUtil.hashPassword(plainPassword, salt);

        // 3. Đóng gói thành đối tượng User mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(passwordHash);
        newUser.setSalt(salt); // Lưu lại cả Salt theo đúng chuẩn Database Schema

        // 4. Gửi xuống tầng DAO để insert vào Database
        return userDAO.createUser(newUser);
    }
}