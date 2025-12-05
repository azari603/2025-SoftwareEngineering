package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.feed.FeedReviewCardDto;
import com.cheack.softwareengineering.entity.User;
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
    private final UserRepository userRepository;

    /**
     * [최신 피드]
     * GET /api/v1/feed/latest?page,size,sort
     */
    @GetMapping("/latest")
    @ResponseStatus(HttpStatus.OK)
    public Page<FeedReviewCardDto> getLatest(Pageable pageable) {
        return feedService.getLatest(pageable);
    }

    /**
     * [팔로잉 피드] (Auth)
     * GET /api/v1/feed/following?page,size,sort
     */
    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public Page<FeedReviewCardDto> getFollowing(Pageable pageable,
                                                Authentication authentication) {
        Long viewerId = resolveCurrentUserId(authentication);
        return feedService.getFollowing(viewerId, pageable);
    }

    /**
     * [피드용 서평 카드 조회(단일)]
     * GET /api/v1/feed/items/{reviewId}
     */
    @GetMapping("/items/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public FeedReviewCardDto getFeedItem(@PathVariable Long reviewId,
                                         Authentication authentication) {

        Long viewerId = null;
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            viewerId = resolveCurrentUserId(authentication);
        }

        return feedService.getFeedItem(reviewId, viewerId);
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
