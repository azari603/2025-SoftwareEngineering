// src/main/java/com/cheack/softwareengineering/repository/CommentRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 리뷰의 댓글 목록 (최신순)
    Page<Comment> findByReviewIdOrderByCreatedAtDesc(Long reviewId, Pageable pageable);

    // 특정 리뷰의 댓글 개수
    long countByReviewId(Long reviewId);
}