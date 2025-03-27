package com.example.Security.service;

import com.example.Security.model.UserPrincipal;
import com.example.Security.model.Users;
import com.example.Security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user by username: " + username);
        Users user = userRepo.findByLogin(username).orElse(null);
        if (user == null) {
            System.out.println("User Not Found for username: " + username);
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("User found: " + user.getLogin());
    return new UserPrincipal(user);
}
}
