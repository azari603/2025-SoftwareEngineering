package com.cheack.softwareengineering.repository;

import com.cheack.softwareengineering.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 특정 쌍이 이미 팔로우 관계인지 확인할 때 사용
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    // 한 사용자가 팔로우하고 있는 사람 목록(활성 팔로우만)
    List<Follow> findByFollowerIdAndStatusTrue(Long followerId);

    // 나를 팔로우하는 사람 수
    long countByFolloweeIdAndStatusTrue(Long userId);

    // 내가 팔로우하는 사람 수
    long countByFollowerIdAndStatusTrue(Long userId);

    // 나를 팔로우하는 사람들 (팔로워 목록)
    Page<Follow> findByFolloweeIdAndStatusTrue(Long userId, Pageable pageable);

    // 내가 팔로우하는 사람들 (팔로잉 목록)
    Page<Follow> findByFollowerIdAndStatusTrue(Long userId, Pageable pageable);
}