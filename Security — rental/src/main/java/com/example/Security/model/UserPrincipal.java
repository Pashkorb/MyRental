package com.example.Security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


public class UserPrincipal implements UserDetails {
    private Users user;
    public UserPrincipal(Users user) {
        this.user=user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Возвращаем роль пользователя как GrantedAuthority
        return Collections.singleton(new SimpleGrantedAuthority(user.getEmail()));
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }

    @Override
    public String getUsername(){
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }


    public UUID getId() {
        return user.getId();
    }
}
