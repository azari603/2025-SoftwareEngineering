// src/main/java/com/cheack/softwareengineering/config/SecurityConfig.java
package com.cheack.softwareengineering.config;

import com.cheack.softwareengineering.security.JwtAuthenticationFilter;
import com.cheack.softwareengineering.security.RestAuthenticationEntryPoint;
import com.cheack.softwareengineering.security.oauth2.CustomOAuth2UserService;
import com.cheack.softwareengineering.security.oauth2.OAuth2FailureHandler;
import com.cheack.softwareengineering.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 세션 안 쓰고 JWT만 사용
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> {})

                // ★ 인증 실패(미로그인/토큰 만료 등) 시 401 JSON 내려주기
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                )

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 1) 정적/기본 개방
                        .requestMatchers(
                                "/",
                                "/error",
                                "/favicon.ico",
                                "/index.html",
                                "/hi.html",
                                "/*.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/uploads/**"
                        ).permitAll()

                        // 2) 인증 불필요 엔드포인트 (Auth API – v1 경로)
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/auth/check/username",
                                "/api/v1/auth/check/email",
                                "/api/v1/auth/verify-email",
                                "/api/v1/auth/find-id",
                                "/api/v1/auth/email/verified"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/token/refresh",
                                "/api/v1/auth/password/forgot",
                                "/api/v1/auth/password/reset",
                                "/api/v1/auth/email/resend"
                        ).permitAll()

                        // 3) 인증 불필요 엔드포인트 (서평 상세 / 책 검색 / 도서 상세 + 공개 추천/피드/좋아요 수)
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/reviews/*",                // 서평 단건 상세
                                "/api/v1/reviews/books/*",          // ★ 이 책의 서평 목록 (reviews/books/{bookId})
                                "/api/v1/books/search",             // (쓰고 있으면 유지)
                                "/api/v1/books/*",                  // 도서 상세 조회
                                "/api/v1/books/*/similar",          // ★ 유사 도서 (books/{bookId}/similar)
                                "/api/v1/search/books",             // SearchController 매핑
                                "/api/v1/search/**",                // 검색 확장용

                                // ★ 여기부터 새로 추가한 공개 API들
                                "/api/v1/recommendations/popular",  // 인기 도서
                                "/api/v1/feed/latest",              // 최신 피드
                                "/api/v1/reviews/*/likes/count"     // 리뷰 좋아요 수 조회
                        ).permitAll()

                        // 소셜 로그인 진입/콜백은 누구나 접근 가능
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // 4) 그 외 보호
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}