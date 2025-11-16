// src/main/java/com/cheack/softwareengineering/repository/ReviewLikeRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.ReviewLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);

    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    Page<ReviewLike> findByUserId(Long userId, Pageable pageable);

    Page<ReviewLike> findByReviewId(Long reviewId, Pageable pageable);

    long countByReviewId(Long reviewId);

    /**
     * 사용자-리뷰 조합으로 좋아요 삭제 (멱등)
     */
    long deleteByUserIdAndReviewId(Long userId, Long reviewId);
}