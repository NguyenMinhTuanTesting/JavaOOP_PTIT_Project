package com.movielibrary.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích hỗ trợ mã hóa và kiểm tra mật khẩu sử dụng thuật toán BCrypt.
 */
public class PasswordUtil {

    /**
     * Tạo một chuỗi Salt ngẫu nhiên để tăng cường bảo mật cho mật khẩu.
     * @return Chuỗi Salt sinh ngẫu nhiên
     */
    public static String generateSalt() {
        return BCrypt.gensalt();
    }

    /**
     * Mã hóa (Băm) mật khẩu thô kết hợp với chuỗi Salt.
     * @param plainPassword Mật khẩu gốc do người dùng nhập
     * @param salt Chuỗi Salt ngẫu nhiên
     * @return Mật khẩu đã được băm (Hash)
     */
    public static String hashPassword(String plainPassword, String salt) {
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Kiểm tra xem mật khẩu người dùng nhập vào có khớp với mật khẩu đã băm trong cơ sở dữ liệu hay không.
     * @param plainPassword Mật khẩu gốc do người dùng nhập lúc đăng nhập
     * @param hashedPassword Mật khẩu đã băm lấy từ Database
     * @return true nếu khớp, false nếu sai mật khẩu
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}