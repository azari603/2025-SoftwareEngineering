package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Notification;
import com.cheack.softwareengineering.entity.NotificationType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NotificationDto {
    Long id;
    Long receiverId;
    Long actorId;
    String actorUsername;
    String actorNickname;
    Long reviewId;
    String reviewTitle;
    NotificationType type;
    String targetUrl;
    String content;
    boolean read;
    LocalDateTime createdAt;

    public static NotificationDto from(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .receiverId(n.getReceiverId())
                .actorId(n.getActorId())
                .actorUsername(n.getActorUsername())
                .actorNickname(n.getActorNickname())
                .reviewId(n.getReviewId())
                .reviewTitle(n.getReviewTitle())
                .type(n.getType())
                .targetUrl(n.getTargetUrl())
                .content(n.getContent())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}