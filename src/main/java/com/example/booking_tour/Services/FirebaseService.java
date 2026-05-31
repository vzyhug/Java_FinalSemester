package com.example.booking_tour.Services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseService {

    // Hàm upload ảnh và trả về đường link URL
    public String uploadImage(MultipartFile file) throws Exception {
        // 1. Lấy bucket từ cấu hình Firebase_config của bạn
        Bucket bucket = StorageClient.getInstance().bucket();

        // 2. Tạo tên file ngẫu nhiên (dùng UUID để không bao giờ bị trùng)
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Mình lưu ảnh vào thư mục "tours/..." trên Firebase cho gọn gàng
        String fileName = "tours/" + UUID.randomUUID().toString() + extension;

        // 3. Đẩy file lên Firebase Storage
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        // 4. Lấy cái tên bucket từ file cấu hình của bạn
        String bucketName = "bookingtourvietnam-63a59.firebasestorage.app";

        // 5. Tạo đường link Public URL để web hiển thị được ảnh
        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";

        // Mã hóa tên file (vì URL không được chứa dấu cách hoặc dấu gạch chéo /)
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return String.format(DOWNLOAD_URL, encodedFileName);
    }
}