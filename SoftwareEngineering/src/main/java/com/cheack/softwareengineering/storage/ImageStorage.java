package com.cheack.softwareengineering.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 다이어그램 ImageStorage <<interface>>
 */
public interface ImageStorage {

    String storeAvatar(Long userId, MultipartFile file);

    String storeBackground(Long userId, MultipartFile file);

    void delete(String objectKey);

    String toPublicUrl(String objectKey);
}
