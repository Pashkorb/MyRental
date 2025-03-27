package com.example.rental.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SearchFilterService {

    @Autowired
    private SearchFilterRepo searchFilterRepo;

    // Создание нового фильтра
    public SearchFilter createSearchFilter(SearchFilter searchFilter) {
        return searchFilterRepo.save(searchFilter);
    }

    // Получение фильтра по UUID
    public SearchFilter getSearchFilter(UUID uuid) {
        return searchFilterRepo.findById(uuid).orElse(null);
    }

    // Получение всех фильтров
    public List<SearchFilter> getAllSearchFilters() {
        return searchFilterRepo.findAll();
    }

    // Получение всех фильтров для конкретного арендатора
    public List<SearchFilter> getSearchFiltersByTenant(UUID tenantUuid) {
        return searchFilterRepo.findByTenantUuid(tenantUuid);
    }

    // Обновление фильтра
    public SearchFilter updateSearchFilter(SearchFilter searchFilter) {
        if (searchFilterRepo.existsById(searchFilter.getUuid())) {
            return searchFilterRepo.save(searchFilter);
        } else {
            return null;
        }
    }

    // Удаление фильтра
    public void deleteSearchFilter(UUID uuid) {
        searchFilterRepo.deleteById(uuid);
    }
}