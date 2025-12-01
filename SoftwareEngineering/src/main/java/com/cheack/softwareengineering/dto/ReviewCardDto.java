package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.dto.feed.FeedItemDto;
import com.cheack.softwareengineering.entity.Review;
import com.cheack.softwareengineering.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReviewCardDto {

    private final Long id;
    private final Long bookId;
    private final Long userId;

    private final String title;
    private final String excerpt;
    private final Double starRating;
    private final Visibility visibility;

    private final LocalDateTime createdAt;

    private final String nickname;
    private final String username;
    private final String profileImage;

    private final BookInfo book;

    private final long likeCount;
    private final long commentCount;
    private final boolean likedByViewer;

    public static ReviewCardDto from(Review review) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())
                .nickname(null)
                .username(null)
                .profileImage(null)
                .book(null)
                .likeCount(0L)
                .commentCount(0L)
                .likedByViewer(false)
                .build();
    }

    public static ReviewCardDto from(Review review, String nickname) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())
                .nickname(nickname)   // ✅ 여기만 다름
                .username(null)
                .profileImage(null)
                .book(null)
                .likeCount(0L)
                .commentCount(0L)
                .likedByViewer(false)
                .build();
    }

    public static ReviewCardDto from(
            Review review,
            FeedItemDto item,
            String nickname,
            String username,
            String profileImage,
            String bookName,
            String bookImage,
            String bookAuthor,
            Double avgStar
    ) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        BookInfo bookInfo = BookInfo.builder()
                .name(bookName)
                .image(bookImage)
                .author(bookAuthor)
                .avgStar(avgStar)
                .build();

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())
                .nickname(nickname)
                .username(username)
                .profileImage(profileImage)
                .book(bookInfo)
                .likeCount(item != null ? item.getLikeCount() : 0L)
                .commentCount(item != null ? item.getCommentCount() : 0L)
                .likedByViewer(item != null && item.isLikedByViewer())
                .build();
    }

    public Long getReviewId() {
        return this.id;
    }

    public static ReviewCardDto forProfile(
            Review review,
            String username,
            String nickname,
            String profileImage,
            String bookName,
            String bookAuthor,
            String bookImage
    ) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        BookInfo bookInfo = BookInfo.builder()
                .name(bookName)
                .author(bookAuthor)
                .image(bookImage)
                .avgStar(null)   // 프로필용 요구사항에는 없음
                .build();

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())
                .nickname(nickname)
                .username(username)
                .profileImage(profileImage)
                .book(bookInfo)
                .likeCount(0L)       // 프로필용 요구사항엔 없으니 0
                .commentCount(0L)
                .likedByViewer(false)
                .build();
    }

    public static ReviewCardDto forMyReviews(
            Review review,
            String profileImage,
            String bookImage
    ) {
        String text = review.getText();
        String excerpt = text == null
                ? ""
                : (text.length() <= 120 ? text : text.substring(0, 117) + "...");

        BookInfo bookInfo = BookInfo.builder()
                .name(null)          // 요구사항엔 필요 없으니까 null
                .image(bookImage)    // ✅ 여기만 채워주면 됨
                .author(null)
                .avgStar(null)
                .build();

        return ReviewCardDto.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .title(review.getTitle())
                .excerpt(excerpt)
                .starRating(review.getStarRating())
                .visibility(review.getVisibility())
                .createdAt(review.getCreatedAt())

                // 닉네임/username 은 요구사항에 없으니 null
                .nickname(null)
                .username(null)
                .profileImage(profileImage)  // ✅ 내 프로필 이미지
                .book(bookInfo)              // ✅ book.image 포함된 객체

                .likeCount(0L)       // 이 API 요구사항엔 없으니 0으로
                .commentCount(0L)
                .likedByViewer(false)
                .build();
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class BookInfo {
        private final String name;
        private final String image;
        private final String author;
        private final Double avgStar;
    }

}