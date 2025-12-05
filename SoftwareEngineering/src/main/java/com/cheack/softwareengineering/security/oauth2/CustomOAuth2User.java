package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;
    private final boolean newUser;   // ★ 추가

    public CustomOAuth2User(User user, Map<String, Object> attributes, boolean newUser) {
        this.user = user;
        this.attributes = attributes;
        this.newUser = newUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

    public ProviderType getProvider() {
        return user.getProvider();
    }

    public boolean isNewUser() {
        return newUser;
    }
}