// src/main/java/com/cheack/softwareengineering/repository/ReviewRepository.java
package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 최신 피드용 (전체 공개/비공개는 일단 신경 안 쓰고, createdAt 기준 정렬만)
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 팔로잉 피드용 – 여러 유저의 리뷰를 모아서 최신순으로
    Page<Review> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);

    // 이 밑의 메서드들은 나중에 ReviewService에서 써먹을 수 있도록 미리 정의
    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByBookId(Long bookId, Pageable pageable);

    Optional<Review> findById(Long id);

    Page<Review> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    Page<Review> findByBookIdAndVisibilityAndDeletedFalse(Long bookId,
                                                          Visibility visibility,
                                                          Pageable pageable);

    Page<Review> findByUserIdAndVisibilityAndDeletedFalse(Long userId,
                                                          Visibility visibility,
                                                          Pageable pageable);
    /**
     * 전체 공개 리뷰만, 최신순
     */
    Page<Review> findByVisibilityAndDeletedFalseOrderByCreatedAtDesc(Visibility visibility,
                                                                     Pageable pageable);

    /**
     * 팔로잉 피드에서 쓸: 특정 유저들(followee) 중 공개 리뷰만, 최신순
     */
    Page<Review> findByUserIdInAndVisibilityAndDeletedFalseOrderByCreatedAtDesc(List<Long> userIds,
                                                                                Visibility visibility,
                                                                                Pageable pageable);

    @Query("select avg(r.starRating) " +
            "from Review r " +
            "where r.bookId = :bookId " +
            "  and r.visibility = :visibility " +
            "  and r.deleted = false")
    Double findAvgStarByBookIdAndVisibility(@Param("bookId") Long bookId,
                                            @Param("visibility") Visibility visibility);


    long countByBookIdAndVisibilityAndDeletedFalse(Long bookId, Visibility visibility);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.deleted = true where r.id = :id")
    void softDelete(@Param("id") Long id);

    void deleteById(Long id);
}