package com.example.rental.property;

import com.example.rental.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PropertyRepo extends JpaRepository<Property, UUID> {
}