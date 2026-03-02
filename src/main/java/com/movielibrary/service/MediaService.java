package com.movielibrary.service;

import com.movielibrary.dao.MediaDAO;
import com.movielibrary.model.Media;

import java.util.List;

public class MediaService {

    private final MediaDAO mediaDAO;

    public MediaService() {
        this.mediaDAO = new MediaDAO();
    }

    /**
     * Lấy danh sách tất cả các thể loại phim đang có trong hệ thống.
     */
    public List<String> getAllGenres() throws Exception {
        return mediaDAO.getAllGenres();
    }

    /**
     * Lấy thông tin chi tiết của một bộ phim/series theo ID.
     */
    public Media getMediaById(String id) throws Exception {
        return mediaDAO.getMediaById(id);
    }

    /**
     * Nghiệp vụ tính toán tổng số trang (Total Pages) dựa trên điều kiện tìm kiếm và lọc.
     * @param search Từ khóa tìm kiếm
     * @param filter Thể loại cần lọc
     * @param itemsPerPage Số lượng phim trên mỗi trang
     * @return Tổng số trang
     */
    public int getTotalPages(String search, String filter, int itemsPerPage) throws Exception {
        int totalItems = mediaDAO.getTotalCount(search, filter);
        // Sử dụng Math.ceil để làm tròn lên. Ví dụ: 21 items / 10 = 2.1 -> làm tròn thành 3 trang
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    /**
     * Lấy danh sách phim cho một trang cụ thể (đã áp dụng tìm kiếm, lọc, sắp xếp).
     */
    public List<Media> getPagedMedia(String search, String filter, String sort, int page, int itemsPerPage) throws Exception {
        return mediaDAO.getPagedMedia(search, filter, sort, page, itemsPerPage);
    }
}