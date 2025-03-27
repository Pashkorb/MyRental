package com.example.Security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class MyOAuth2User implements OAuth2User {
    private Users user;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public MyOAuth2User(Users user, Object o, Map<String, Object> attributes) {
    }
    public MyOAuth2User(Users user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.user = user;
        this.authorities = authorities;
        this.attributes = attributes;
    }
    public void OAuth2User(Users user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public <A> A getAttribute(String name) {
        return (A) attributes.get(name);
    }

    @Override
    public String getName() {
        if (user == null) {
            throw new IllegalStateException("User is null");
        }
        return user.getLogin();
    }
}

