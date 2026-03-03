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

Trước khi chạy dự án, vui lòng đảm bảo máy đã cài đặt:

1. JDK 25  
2. Maven  
3. MySQL Server  
4. MySQL Workbench (khuyến khích)

---

# 📥 HƯỚNG DẪN TẢI PHẦN MỀM (NẾU CHƯA CÀI)

## 1️⃣ MySQL Server

Nếu máy chưa có MySQL, tải tại:

👉 https://dev.mysql.com/downloads/installer/

Cài đặt theo mặc định và ghi nhớ:
- Username (thường là root)
- Password bạn tự đặt
- Port (mặc định 3306)

---

## 2️⃣ MySQL Workbench

Tải tại:

👉 https://dev.mysql.com/downloads/workbench/

Dùng để import schema và quản lý database dễ dàng hơn.

---

## 3️⃣ Maven

Nếu máy chưa có Maven, tải tại:

👉 https://maven.apache.org/download.cgi

Chọn:

```
Binary zip archive (bin.zip)
```

Sau khi tải:
- Giải nén
- Thêm thư mục `bin` vào biến môi trường PATH

Kiểm tra bằng lệnh:

```bash
mvn -v
```

Nếu hiển thị phiên bản → cài đặt thành công.

---

# 🗄️ CÀI ĐẶT DATABASE

## Bước 1: Khởi động MySQL

Đảm bảo MySQL Server đang chạy.

---

## Bước 2: Tạo Database

Mở MySQL Workbench và chạy:

```sql
CREATE DATABASE movie_library;
USE movie_library;
```

---

## Bước 3: Import Schema

1. Mở file SQL schema trong repository.
2. Chạy toàn bộ script để tạo các bảng:
   - users
   - media
   - reviews

Đảm bảo tất cả bảng được tạo thành công.

---

# ⚠️ CẤU HÌNH KẾT NỐI DATABASE

File cấu hình:

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

Thay:

```java
private static final String PASSWORD = "****";
```

Bằng mật khẩu MySQL bạn đã đặt:

```java
private static final String PASSWORD = "mat_khau_mysql_cua_ban";
```

---

## 🔌 2️⃣ Về cổng MySQL (Port 3306)

Mặc định MySQL sử dụng:

```
3306
```

Nếu không có xung đột → giữ nguyên 3306.

Nếu MySQL chạy trên cổng khác (ví dụ 3307), sửa:

```java
jdbc:mysql://localhost:3306/movie_library
```

Thành:

```java
jdbc:mysql://localhost:3307/movie_library
```

---

# ▶️ CHẠY ỨNG DỤNG

Sau khi hoàn tất các bước trên, mở Terminal tại thư mục project và chạy:

```bash
mvn clean install
mvn javafx:run
```

Ứng dụng sẽ khởi động.

---

# 📥 HÀNH VI KHI CHẠY LẦN ĐẦU

- Ứng dụng tự động import dữ liệu từ:

```
src/main/resources/data/Media_Data.csv
```

- Sử dụng `INSERT IGNORE` để tránh trùng dữ liệu.

---

# 🔐 LƯU Ý VỀ TRẢI NGHIỆM ỨNG DỤNG

Do hệ thống lưu trữ bình luận và đánh giá phim hoàn toàn trên database local, nên khi chạy trên máy mới:

- Database sẽ không có sẵn tài khoản hoặc đánh giá mẫu.
- Người dùng cần tự tạo tài khoản mới.

👉 Để trải nghiệm đầy đủ tính năng:

1. Tạo ít nhất 2 tài khoản người dùng.
2. Đăng nhập từng tài khoản và thực hiện đánh giá phim.
3. Kiểm tra chức năng:
   - Hiển thị danh sách đánh giá
   - Phân quyền người dùng
   - Hiển thị đánh giá theo từng tài khoản

Việc này giúp kiểm tra đầy đủ các chức năng liên quan đến hệ thống review và quản lý người dùng.

---

# ❓ KHẮC PHỤC LỖI THƯỜNG GẶP

### 1️⃣ Access denied for user 'root'

- Kiểm tra đúng mật khẩu
- MySQL đang chạy

---

### 2️⃣ Communications link failure

- Kiểm tra đúng port
- MySQL đang hoạt động

---

### 3️⃣ Unknown database 'movie_library'

Chạy lại:

```sql
CREATE DATABASE movie_library;
```

---

### 4️⃣ Maven không tải dependency

Chạy lại:

```bash
mvn clean install
```

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

Chỉ cần làm đúng các bước trên và chạy:

```bash
mvn javafx:run
```

Ứng dụng sẽ hoạt động bình thường trên môi trường local.
