package com.example.Security.repo;

import com.example.Security.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<Users, UUID> {

    Optional<Users> findByLogin(String login);

    Optional<Users> findByEmail(String email);


    Optional<Users> findByLoginOrEmail(String username, String username1);
}