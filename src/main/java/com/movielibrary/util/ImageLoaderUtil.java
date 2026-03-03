package com.movielibrary.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tiện ích tải ảnh an toàn, chống block và lưu cache cục bộ.
 */
public class ImageLoaderUtil {

    // Thư mục lưu cache ảnh trên máy
    private static final String CACHE_DIR = "poster_cache";

    // Hàng đợi: Chỉ cho phép tải tối đa 3 ảnh cùng lúc để qua mặt Rate Limit của Wikipedia
    private static final ExecutorService executor = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    // Tạo thư mục cache ngay khi class được gọi lần đầu
    static {
        File dir = new File(CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Hàm tải ảnh chính
     */
    public static void load(ImageView imageView, String imageUrl, int fallbackWidth, int fallbackHeight) {
        String fallbackUrl = "https://placehold.co/" + fallbackWidth + "x" + fallbackHeight + "/21262d/c9d1d9?text=No+Poster";

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImage(new Image(fallbackUrl, true));
            return;
        }

        // Tạo tên file từ URL gốc
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        File cachedFile = new File(CACHE_DIR, fileName);

        executor.submit(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Khai báo User-Agent
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Nếu server cho phép tải (HTTP 200)
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream in = conn.getInputStream()) {
                        Files.copy(in, cachedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    Platform.runLater(() -> imageView.setImage(new Image(cachedFile.toURI().toString(), true)));
                } else {
                    Platform.runLater(() -> imageView.setImage(new Image(fallbackUrl, true)));
                }
            } catch (Exception e) {
                Platform.runLater(() -> imageView.setImage(new Image(fallbackUrl, true)));
            }
        });
    }
}