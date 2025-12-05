package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.ExternalBookMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 네이버 도서 API 같은 외부 도서 메타 제공자에 대한 클라이언트 인터페이스
 * 실제 HTTP 호출 구현은 이 인터페이스를 구현하는 클래스에서 하면 됨.
 */
public interface ExternalBookApiClient {

    /**
     * 단일 ISBN 으로 도서 메타 조회
     * 못 찾으면 null 을 리턴하게 구현해도 되고, 예외를 던지게 구현해도 됨.
     */
    ExternalBookMeta fetchByIsbn(String isbn);

    /**
     * 키워드(제목/저자 등)에 대한 검색 결과를 페이지 단위로 조회
     */
    Page<ExternalBookMeta> search(String keyword, Pageable pageable);
}
