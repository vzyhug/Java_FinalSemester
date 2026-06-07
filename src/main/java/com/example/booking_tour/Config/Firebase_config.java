

package com.example.booking_tour.Config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class Firebase_config {

    @PostConstruct
    public void init() {
        try {
            // Load file từ classpath
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("firebase/bookingtourvietnam-63a59-firebase-adminsdk-fbsvc-9766a5565d.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("Không tìm thấy file service account trong resources/firebase/");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("bookingtourvietnam-63a59.firebasestorage.app")
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println(" Kết nối Firebase thành công!");

        } catch (Exception e) {
            System.out.println("Kết nối Firebase thất bại!");
            e.printStackTrace();
        }
    }
}
