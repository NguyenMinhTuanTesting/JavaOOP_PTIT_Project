package com.movielibrary.model;

/**
 * Lớp trừu tượng (Abstract Class) đại diện cho một sản phẩm truyền thông chung.
 * Chứa tất cả các thuộc tính dùng chung cho cả Phim lẻ (Movie) và Phim bộ (Series).
 */
public abstract class Media {
    private String id;
    private String type;
    private String title;
    private String posterUrl;
    private int releaseYear;
    private String genres;
    private String country;
    private String director;
    private String casts;
    private String description;

    // Các hàm Get/Set mặc định để hỗ trợ Đa hình cho Controller
    // Lớp con sẽ ghi đè (Override) các hàm này
    public int getDuration() { return 0; }
    public void setDuration(int duration) {}

    public int getEpisodes() { return 0; }
    public void setEpisodes(int episodes) {}

    // --- GETTERS & SETTERS CHUẨN ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCasts() {
        return casts;
    }

    public void setCasts(String casts) {
        this.casts = casts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}