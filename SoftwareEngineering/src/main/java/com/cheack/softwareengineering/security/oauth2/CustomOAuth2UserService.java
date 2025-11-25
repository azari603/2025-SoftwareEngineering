package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.entity.UserStatus;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google/kakao/naver
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes attr = OAuthAttributes.of(registrationId, attributes);

        boolean isNew = false;

        User user = userRepository.findByProviderAndProviderId(
                        attr.getProvider(),
                        attr.getProviderId()
                )
                .orElse(null);

        if (user == null) {
            // ★ 최초 소셜 유입
            isNew = true;

            String tmpUsername = attr.getSuggestedUsername(); // 예: "@abc123"

            user = User.builder()
                    .username(tmpUsername)
                    .email(attr.getEmail())
                    .password(null)
                    .nickname(tmpUsername)
                    .emailVerified(true)
                    .provider(attr.getProvider())   // ProviderType
                    .providerId(attr.getProviderId())
                    .status(UserStatus.ACTIVE)
                    .build();

            user = userRepository.save(user);
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes(), isNew);
    }
}
