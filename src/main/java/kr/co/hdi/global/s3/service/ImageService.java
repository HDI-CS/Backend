package kr.co.hdi.global.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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
}
