package kr.co.hdi.global.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private final AmazonS3 amazonS3;

    /*
    이미지 업로드 용 presigned url 발급 (1분 유효)
     */
    public String generateUploadPresignedUrl(String key) {

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 1);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    /*
    이미지 조회 url
     */
    public String getImageUrl(String key) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucket,
                region,
                key
        );
    }

    /*
    이미지 삭제
     */
    public void deleteImage(String key) {
        if (key == null || key.isBlank()) return;
        amazonS3.deleteObject(bucket, key);
    }

    /*
    이미지 다운로드 (ZIP 파일)
     */
    public void downloadAsZip(Map<String, String> keyNameMap, OutputStream os) throws IOException {

        try (ZipOutputStream zipOut = new ZipOutputStream(os)) {
            for (Map.Entry<String, String> entry : keyNameMap.entrySet()) {

                String key = entry.getKey();
                String fileName = entry.getValue();

                S3Object s3Object = amazonS3.getObject(bucket, key);

                try (S3ObjectInputStream in = s3Object.getObjectContent()) {

                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, len);
                    }

                    zipOut.closeEntry();
                }
            }
        }
    }
}
