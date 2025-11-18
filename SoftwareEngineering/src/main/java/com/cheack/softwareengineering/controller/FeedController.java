package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.feed.ReviewCardDto;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.ReviewRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import com.cheack.softwareengineering.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Feed API v1
 *
 * Base: /api/v1/feed
 * - GET /feed/latest
 * - GET /feed/following   (Auth)
 * - GET /feed/items/{reviewId}
 */
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * [최신 피드]
     * GET /api/v1/feed/latest?page,size,sort
     *
     * 정렬 기본은 서비스/리포지토리에서 createdAt desc 로 처리하고 있으므로
     * 여기서는 Pageable 그대로 넘긴다.
     */
    @GetMapping("/latest")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReviewCardDto> getLatest(Pageable pageable) {
        return feedService.getLatest(pageable);
    }

    /**
     * [팔로잉 피드] (Auth)
     * GET /api/v1/feed/following?page,size,sort
     *
     * - JWT 인증된 사용자의 username 을 Authentication 에서 꺼내고
     * - UserRepository 로 userId 를 조회한 뒤 FeedService 에 넘긴다.
     */
    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReviewCardDto> getFollowing(Pageable pageable,
                                            Authentication authentication) {
        Long viewerId = resolveCurrentUserId(authentication);
        return feedService.getFollowing(viewerId, pageable);
    }

    /**
     * [피드용 서평 카드 조회(단일)]
     * GET /api/v1/feed/items/{reviewId}
     *
     * - 리뷰 하나를 가져와서 FeedService.enrich(..)로 FeedItemDto 생성
     * - ReviewCardDto.from(...) 으로 카드 DTO 변환
     * - 로그인 안 되어 있으면 viewerId = null 로 넘겨서 myLike=false 처리
     */
    @GetMapping("/items/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewCardDto getFeedItem(@PathVariable Long reviewId,
                                     Authentication authentication) {

        Long viewerId = null;
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            viewerId = resolveCurrentUserId(authentication);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        return ReviewCardDto.from(feedService.enrich(review, viewerId));
    }

    /**
     * Authentication -> 현재 로그인 유저의 id 로 변환
     * (username 을 기반으로 UserRepository 조회)
     */
    private Long resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("UNAUTHORIZED");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getId();
    }
}