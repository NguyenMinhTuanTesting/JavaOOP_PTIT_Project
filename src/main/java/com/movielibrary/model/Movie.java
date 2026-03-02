package com.movielibrary.model;

/**
 * Lớp đại diện cho Phim lẻ (Movie).
 * Kế thừa toàn bộ thuộc tính của Media và bổ sung thêm Thời lượng (Duration).
 */
public class Movie extends Media {

    private int duration; // Thời lượng tính bằng Phút

    public Movie() {
        // Thiết lập sẵn Type để tránh nhầm lẫn khi tạo mới object
        this.setType("MOVIE");
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }
}