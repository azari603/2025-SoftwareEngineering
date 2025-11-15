package com.cheack.softwareengineering.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "book_bti",
        indexes = {
                @Index(name = "idx_book_bti_user", columnList = "user_id")
        }
)
public class BookBTI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_bti_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 어떤 질문에 대한 응답인지
     */
    @Column(name = "question", nullable = false, length = 500)
    private String question;

    /**
     * 사용자가 선택/입력한 답변
     */
    @Column(name = "answer", nullable = false, length = 500)
    private String answer;

    /**
     * 결과 유형(예: "INTJ형 독서가" 같은 타입 코드)
     */
    @Column(name = "result_type", length = 100)
    private String resultType;

    /**
     * 총점이나 순위 등 숫자 결과가 필요할 때 사용
     */
    @Column(name = "quiz_result")
    private Long quizResult;
}
