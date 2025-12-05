package com.cheack.softwareengineering.entity;

public enum NotificationType {
    FOLLOW,          // 누가 나를 팔로우함
    REVIEW_LIKE,     // 내 서평에 좋아요
    REVIEW_COMMENT,  // 내 서평에 댓글
    SYSTEM            // 기타 시스템 알림
}