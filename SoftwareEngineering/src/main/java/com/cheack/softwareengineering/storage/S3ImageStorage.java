package com.cheack.softwareengineering.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * S3ImageStorage <<impl>>
 */
@Slf4j
//@Component
public class S3ImageStorage implements ImageStorage {

    private final S3Client s3;
    private final String bucket;
    private final String publicBaseUrl;

    public S3ImageStorage(
            S3Client s3,
            @Value("${app.image.s3.bucket}") String bucket,
            @Value("${app.image.s3.public-base-url}") String publicBaseUrl
    ) {
        this.s3 = s3;
        this.bucket = bucket;
        this.publicBaseUrl = removeTrailingSlash(publicBaseUrl);
    }

    @Override
    public String storeAvatar(Long userId, MultipartFile file) {
        return putObject("avatars", userId, file);
    }

    @Override
    public String storeBackground(Long userId, MultipartFile file) {
        return putObject("backgrounds", userId, file);
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        s3.deleteObject(req);
    }

    @Override
    public String toPublicUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        return publicBaseUrl + "/" + objectKey;
    }

    public void validateContentType(MultipartFile file, Set<String> allow) {
        String contentType = file.getContentType();
        if (contentType == null || !allow.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않은 이미지 형식입니다. contentType=" + contentType);
        }
    }

    public void validateMaxSize(MultipartFile file, long maxBytes) {
        long size = file.getSize();
        if (size > maxBytes) {
            throw new IllegalArgumentException("이미지 파일 크기가 너무 큽니다. size=" + size);
        }
    }

    public void validateDimensions(BufferedImage img, int maxW, int maxH) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (w > maxW || h > maxH) {
            throw new IllegalArgumentException(
                    String.format("이미지 해상도가 너무 큽니다. width=%d, height=%d", w, h)
            );
        }
    }

    // ================== private helpers ==================

    private String putObject(String typeDir, Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("저장할 이미지 파일이 비어 있습니다.");
        }

        // 예시: 간단한 검증 (원하면 외부에서 validate* 호출해서 써도 됨)
        validateContentType(file, Set.of("image/jpeg", "image/png"));
        validateMaxSize(file, 5 * 1024 * 1024L);
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img != null) {
                validateDimensions(img, 2000, 2000);
            }
        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
        }

        String ext = getExtension(file.getOriginalFilename());
        String key = typeDir + "/" + userId + "/" + UUID.randomUUID() + ext;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new IllegalStateException("S3 업로드에 실패했습니다.", e);
        }

        return key;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot == -1) return "";
        return originalFilename.substring(dot);
    }

    private String removeTrailingSlash(String url) {
        if (url == null) return null;
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
