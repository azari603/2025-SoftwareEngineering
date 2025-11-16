package com.cheack.softwareengineering.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LocalImageStorage <<impl>>
 * - baseDir : Path
 * - publicBaseUrl : String
 */
@Slf4j
@Component
public class LocalImageStorage implements ImageStorage {

    private final Path baseDir;
    private final String publicBaseUrl;

    public LocalImageStorage(
            @Value("${app.image.local.base-dir:./uploads}") String baseDir,
            @Value("${app.image.local.public-base-url:/uploads}") String publicBaseUrl
    ) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        this.publicBaseUrl = removeTrailingSlash(publicBaseUrl);

        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 디렉터리를 생성할 수 없습니다: " + this.baseDir, e);
        }
    }

    @Override
    public String storeAvatar(Long userId, MultipartFile file) {
        return store("avatars", userId, file);
    }

    @Override
    public String storeBackground(Long userId, MultipartFile file) {
        return store("backgrounds", userId, file);
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            Path target = baseDir.resolve(objectKey).normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("이미지 삭제 실패. key={}", objectKey, e);
        }
    }

    @Override
    public String toPublicUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        return publicBaseUrl + "/" + objectKey.replace("\\", "/");
    }

    // ================== private helpers ==================

    private String store(String typeDir, Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("저장할 이미지 파일이 비어 있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = getExtension(originalFilename);
        String filename = typeDir + "_" + userId + "_" + System.currentTimeMillis() + ext;

        Path userDir = baseDir.resolve(typeDir).resolve(String.valueOf(userId));
        try {
            Files.createDirectories(userDir);
            Path target = userDir.resolve(filename);
            file.transferTo(target.toFile());

            // DB에 저장할 objectKey = baseDir 이하 상대 경로
            return baseDir.relativize(target).toString().replace("\\", "/");
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
        }
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
