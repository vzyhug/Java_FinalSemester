<<<<<<< HEAD
package com.example.booking_tour.Config;
=======
//package com.example.booking_tour.Config;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//
//
//@Configuration //Khai báo class này cần được quét khi ứng dụng bắt đầu chạy
//public class Firebase_config {
//    @PostConstruct //Đánh dấu phương thức sẽ tự động chạy khi spring khởi tạo xong bean firebase_config
//
//    public void init() {
//        try {
//            // Đọc file cấu hình bảo mật
//            FileInputStream serviceAccount =
//                    new FileInputStream(
//                            "src/main/resources/firebase/bookingtourvietnam-63a59-firebase-adminsdk-fbsvc-9766a5565d.json"
//                    );
//            // Thiết lập cấu hình firebase
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)) //Nạp file json đã đọc để chứng thực
//                    .setStorageBucket("bookingtourvietnam-63a59.firebasestorage.app") //Chỉ định bucket mà ứng dụng sẽ thao tác
//                    .build();
//
//            FirebaseApp.initializeApp(options); //Kích hoạt kết nối firebase với các cấu hình ở trên
//
//            System.out.println("Kết nối firebase thành công!");
//
//        } catch (Exception e) {
//
//            System.out.println("Kết nồi firebase thất bại!");
//
//            e.printStackTrace();
//        }
//    }
//}

package com.example.booking_tour.Config;

>>>>>>> origin/minhthu
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

<<<<<<< HEAD
import java.io.FileInputStream;


@Configuration //Khai báo class này cần được quét khi ứng dụng bắt đầu chạy
public class Firebase_config {
    @PostConstruct //Đánh dấu phương thức sẽ tự động chạy khi spring khởi tạo xong bean firebase_config

    public void init() {
        try {
            // Đọc file cấu hình bảo mật
            FileInputStream serviceAccount =
                    new FileInputStream(
                            "src/main/resources/firebase/bookingtourvietnam-63a59-firebase-adminsdk-fbsvc-9766a5565d.json"
                    );
            // Thiết lập cấu hình firebase
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)) //Nạp file json đã đọc để chứng thực
                    .setStorageBucket("bookingtourvietnam-63a59.firebasestorage.app") //Chỉ định bucket mà ứng dụng sẽ thao tác
                    .build();

            FirebaseApp.initializeApp(options); //Kích hoạt kết nối firebase với các cấu hình ở trên

            System.out.println("Kết nối firebase thành công!");

        } catch (Exception e) {

            System.out.println("Kết nồi firebase thất bại!");

            e.printStackTrace();
        }
    }
}
=======
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

            System.out.println("✅ Kết nối Firebase thành công!");

        } catch (Exception e) {
            System.out.println("❌ Kết nối Firebase thất bại!");
            e.printStackTrace();
        }
    }
}
>>>>>>> origin/minhthu
