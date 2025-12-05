package com.cheack.softwareengineering.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * ImageValidator <<component>>
 */
@Slf4j
@Component
public class ImageValidator {

    private final Set<String> allowedContentTypes = new HashSet<>();
    private final long maxBytes;
    private final int maxWidth;
    private final int maxHeight;

    public ImageValidator(
            @Value("${app.image.allowed-content-types:image/jpeg,image/png}") String allowedContentTypes,
            @Value("${app.image.max-bytes:5242880}") long maxBytes,
            @Value("${app.image.max-width:2000}") int maxWidth,
            @Value("${app.image.max-height:2000}") int maxHeight
    ) {
        this.maxBytes = maxBytes;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(this.allowedContentTypes::add);
    }

    @PostConstruct
    void logConfig() {
        log.info("ImageValidator init - allowed={}, maxBytes={}, maxWidth={}, maxHeight={}",
                allowedContentTypes, maxBytes, maxWidth, maxHeight);
    }

    public void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않은 이미지 형식입니다. contentType=" + contentType);
        }
    }

    public void validateSize(MultipartFile file) {
        long size = file.getSize();
        if (size > maxBytes) {
            throw new IllegalArgumentException("이미지 파일 크기가 너무 큽니다. size=" + size);
        }
    }

    public void validateDimensions(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (w > maxWidth || h > maxHeight) {
            throw new IllegalArgumentException(
                    String.format("이미지 해상도가 너무 큽니다. width=%d, height=%d", w, h)
            );
        }
    }

    public void validateAll(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        validateContentType(file);
        validateSize(file);

        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다.");
            }
            validateDimensions(img);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }
}
