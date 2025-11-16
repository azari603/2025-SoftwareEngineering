// src/main/java/com/cheack/softwareengineering/dto/CommentDto.java
package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;              // 댓글 ID
    private Long reviewId;        // 어떤 리뷰에 달린 댓글인지
    private String text;          // 댓글 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserMiniDto author;   // 작성자 요약 정보 (id, username, nickname 정도)
    private boolean mine;         // 내가 쓴 댓글인지 여부

    /**
     * Comment 엔티티 + 작성자 요약 정보 + mine 플래그로 DTO 생성
     */
    public static CommentDto from(Comment comment, UserMiniDto author, boolean mine) {
        return CommentDto.builder()
                .id(comment.getId())
                .reviewId(comment.getReviewId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())   // 엔티티에 updatedAt 있다고 가정
                .author(author)
                .mine(mine)
                .build();
    }

    /**
     * 작성자 정보를 나중에 채우거나, 일단 안 쓸 때용 단순 버전
     */
    public static CommentDto from(Comment comment, boolean mine) {
        return CommentDto.builder()
                .id(comment.getId())
                .reviewId(comment.getReviewId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(null)
                .mine(mine)
                .build();
    }
}