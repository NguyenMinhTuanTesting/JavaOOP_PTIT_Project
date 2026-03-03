# 🎬 Ứng Dụng Movie Library

Ứng dụng quản lý và đánh giá phim/series được xây dựng bằng JavaFX và Maven.

---

## 🛠️ Công Nghệ Sử Dụng

- Java JDK 25
- Maven
- JavaFX
- MySQL
- JDBC
- BCrypt (Mã hóa mật khẩu)

---

# ⚙️ YÊU CẦU HỆ THỐNG

Trước khi chạy dự án, vui lòng cài đặt:

1. JDK 25  
2. Maven (phiên bản mới nhất)  
3. MySQL Server (phiên bản mới nhất)  
4. MySQL Workbench (khuyến khích)

---

# 🗄️ CÀI ĐẶT DATABASE

## Bước 1: Khởi động MySQL

Đảm bảo MySQL Server đang chạy trên máy.

---

## Bước 2: Tạo Database

Mở MySQL Workbench và chạy:

```sql
CREATE DATABASE movie_library;
USE movie_library;
```

---

## Bước 3: Import Schema

1. Mở file SQL schema được cung cấp trong repository.
2. Chạy toàn bộ script để tạo các bảng:
   - users
   - media
   - reviews

Đảm bảo tất cả bảng được tạo thành công trước khi chạy ứng dụng.

---

# ⚠️ CẤU HÌNH KẾT NỐI DATABASE

File cấu hình nằm tại:

```
src/main/java/com/movielibrary/config/DatabaseConfig.java
```

Cấu hình mặc định:

```java
private static final String URL = "jdbc:mysql://localhost:3306/movie_library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "****";
```

---

## 🔑 1️⃣ Đổi mật khẩu MySQL

Thay dòng:

```java
private static final String PASSWORD = "****";
```

Bằng mật khẩu MySQL bạn đã đặt khi cài:

```java
private static final String PASSWORD = "mat_khau_mysql_cua_ban";
```

⚠️ Mật khẩu này phải trùng với mật khẩu MySQL trên máy bạn.

---

## 🔌 2️⃣ Về cổng MySQL (Port 3306)

Mặc định MySQL sử dụng:

```
3306
```

Nếu máy bạn không bị xung đột cổng → giữ nguyên 3306.

---

### ❗ Nếu 3306 bị xung đột

Nếu MySQL của bạn chạy trên cổng khác (ví dụ 3307), sửa dòng:

```java
jdbc:mysql://localhost:3306/movie_library
```

Thành:

```java
jdbc:mysql://localhost:3307/movie_library
```

✔ Nếu không có xung đột → nên giữ 3306.

---

# ▶️ CHẠY ỨNG DỤNG

Sau khi:

- Tạo database
- Import schema
- Cập nhật mật khẩu (nếu cần)

Mở Terminal tại thư mục gốc của project và chạy:

```bash
mvn clean install
mvn javafx:run
```

Ứng dụng sẽ tự động khởi động.

---

# 📥 HÀNH VI KHI CHẠY LẦN ĐẦU

- Ứng dụng tự động import dữ liệu từ:

```
src/main/resources/data/Media_Data.csv
```

- Sử dụng `INSERT IGNORE` → không bị trùng dữ liệu nếu chạy nhiều lần.

---

# 🔐 ĐĂNG NHẬP

Người dùng có thể:

- Đăng ký tài khoản mới
- Đăng nhập
- Sử dụng chế độ Guest

Mật khẩu được mã hóa bằng BCrypt trước khi lưu vào database.

---

# ❓ KHẮC PHỤC LỖI THƯỜNG GẶP

---

### 1️⃣ Lỗi: Access denied for user 'root'

✔ Kiểm tra:
- Đúng mật khẩu trong DatabaseConfig.java
- MySQL đang chạy

---

### 2️⃣ Lỗi: Communications link failure

✔ Kiểm tra:
- Đúng port
- MySQL đang hoạt động
- Không bị firewall chặn

---

### 3️⃣ Lỗi: Unknown database 'movie_library'

Chạy lại:

```sql
CREATE DATABASE movie_library;
```

---

### 4️⃣ Lỗi: Thiếu dependency

Chạy:

```bash
mvn clean install
```

Maven sẽ tự động tải thư viện từ pom.xml.

---

# 📁 CẤU TRÚC DỰ ÁN

- controller → Xử lý giao diện  
- service → Xử lý nghiệp vụ  
- dao → Truy vấn database  
- model → Lớp dữ liệu  
- config → Cấu hình kết nối  
- util → Tiện ích  
- resources → FXML, CSS, CSV  

---

# 🎯 KẾT LUẬN

Sau khi hoàn tất các bước trên, chỉ cần chạy:

```bash
mvn javafx:run
```

Ứng dụng sẽ hoạt động bình thường.
