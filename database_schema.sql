
CREATE DATABASE IF NOT EXISTS movie_library;
USE movie_library;

-- ==============================
-- TABLE: media
-- ==============================

CREATE TABLE IF NOT EXISTS media (
                                     id VARCHAR(50) PRIMARY KEY,      -- ID từ CSV (media_001)
    type VARCHAR(20) NOT NULL,       -- MOVIE / SERIES
    title VARCHAR(255) NOT NULL,
    poster_url TEXT,
    release_year INT,
    genres TEXT,                     -- Ví dụ: "Action;Sci-Fi"
    duration INT,                    -- Phút (Movie)
    episodes INT,                    -- Số tập (Series)
    country VARCHAR(100),
    director TEXT,
    casts TEXT,
    description TEXT
    );

-- ==============================
-- TABLE: users
-- ==============================

CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(64) NOT NULL,
    salt VARCHAR(32) NOT NULL
    );

-- ==============================
-- TABLE: reviews
-- ==============================

CREATE TABLE IF NOT EXISTS reviews (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       user_id INT NOT NULL,
                                       media_id VARCHAR(50) NOT NULL,
    rating DOUBLE NOT NULL CHECK (rating >= 1.0 AND rating <= 10.0),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (user_id, media_id),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
    );