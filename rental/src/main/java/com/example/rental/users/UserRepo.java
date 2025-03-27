package com.example.rental.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    User findByUuid(UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.surname = :surname, u.email = :email, u.phone = :phone WHERE u.id = :id")
    int updateUserFields(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("email") String email,
            @Param("phone") String phone
    );
}