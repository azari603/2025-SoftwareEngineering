package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.entity.ProviderType;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class OAuthAttributes {

    private final ProviderType provider;     // GOOGLE/KAKAO/NAVER
    private final String providerId;         // 플랫폼 고유 ID
    private final String email;
    private final String nameAttributeKey;   // user-name-attribute
    private final Map<String, Object> attributes;

    private OAuthAttributes(ProviderType provider, String providerId, String email,
                            String nameAttributeKey, Map<String, Object> attributes) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.nameAttributeKey = nameAttributeKey;
        this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        ProviderType provider = ProviderType.valueOf(registrationId.toUpperCase());

        String providerId;
        String email;
        String nameAttributeKey;

        switch (provider) {
            case GOOGLE -> {
                providerId = String.valueOf(attributes.get("sub"));
                email = (String) attributes.get("email");
                nameAttributeKey = "sub";
            }
            case KAKAO -> {
                providerId = String.valueOf(attributes.get("id"));
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
                nameAttributeKey = "id";
            }
            case NAVER -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                providerId = response != null ? String.valueOf(response.get("id")) : null;
                email = response != null ? (String) response.get("email") : null;
                nameAttributeKey = "id";
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        }

        return new OAuthAttributes(provider, providerId, email, nameAttributeKey, attributes);
    }

    public ProviderType getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    /** 임시 username 제안: email 앞부분 또는 provider+id */
    public String getSuggestedUsername() {
        if (email != null && email.contains("@")) {
            return "@" + email.substring(0, email.indexOf('@'));
        }
        return "@" + (provider.name().toLowerCase()) + "_" + providerId;
    }

    public String getEmail() {
        return email;
    }

    public String getNameAttributeKey() {
        return nameAttributeKey;
    }

    public List<String> getAuthorities() {
        // 필요 시 ROLE 매핑 확장
        return Collections.singletonList("ROLE_USER");
    }
}
