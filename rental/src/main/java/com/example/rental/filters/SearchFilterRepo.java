package com.example.rental.filters;

import com.example.rental.filters.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchFilterRepo extends JpaRepository<SearchFilter, UUID> {

    // Метод для поиска фильтров по UUID арендатора
    List<SearchFilter> findByTenantUuid(UUID tenantUuid);
}