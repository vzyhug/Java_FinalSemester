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
        Bucket bucket = StorageClient.getInstance().bucket();

        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String fileName = "tours/" + UUID.randomUUID().toString() + extension;

        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        String bucketName = "bookingtourvietnam-63a59.firebasestorage.app";

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return String.format(DOWNLOAD_URL, encodedFileName);
    }
}