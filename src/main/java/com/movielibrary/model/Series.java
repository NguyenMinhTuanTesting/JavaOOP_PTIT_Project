package com.movielibrary.model;

/**
 * Lớp đại diện cho Phim bộ (Series).
 * Kế thừa toàn bộ thuộc tính của Media và bổ sung thêm Số tập (Episodes).
 */
public class Series extends Media {

    private int episodes; // Số tập phim

    public Series() {
        // Thiết lập sẵn Type để tránh nhầm lẫn khi tạo mới object
        this.setType("SERIES");
    }

    @Override
    public int getEpisodes() {
        return episodes;
    }

    @Override
    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }
}