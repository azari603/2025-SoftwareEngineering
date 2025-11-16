package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Notification;
import com.cheack.softwareengineering.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class NotificationDto {

    private final Long id;
    private final NotificationType type;

    private final Long actorId;
    private final String actorUsername; // 나중에 필요 없으면 null 로 두고 사용

    private final String content;
    private final String targetUrl;

    private final boolean read;
    private final LocalDateTime createdAt;

    public static NotificationDto from(Notification n, String actorUsername) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .actorId(n.getActorId())
                .actorUsername(actorUsername)
                .content(n.getContent())
                .targetUrl(n.getTargetUrl())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static NotificationDto from(Notification n) {
        // actorUsername 아직 안 쓰면 이 메서드 사용
        return from(n, null);
    }
}