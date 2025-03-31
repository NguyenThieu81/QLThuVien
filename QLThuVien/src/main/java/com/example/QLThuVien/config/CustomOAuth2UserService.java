package com.example.QLThuVien.config;

import com.example.QLThuVien.services.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String username;
        String email;

        if (oAuth2User instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) oAuth2User;
            username = oidcUser.getAttribute("name");
            email = oidcUser.getEmail();
        } else {
            username = oAuth2User.getAttribute("name");
            email = oAuth2User.getAttribute("email");
        }

        userService.saveOrUpdateUserFromGoogle(username, email);

        return oAuth2User;
    }
}


