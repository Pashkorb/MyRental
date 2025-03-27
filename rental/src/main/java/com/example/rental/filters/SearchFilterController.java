package com.example.rental.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/rental/searchFilter")
public class SearchFilterController {

    @Autowired
    private SearchFilterService searchFilterService;

    // Создание нового фильтра
//    @PostMapping("/create")
//    public ResponseEntity<SearchFilter> createSearchFilter(@RequestBody SearchFilter searchFilter) {
//        SearchFilter createdSearchFilter = searchFilterService.createSearchFilter(searchFilter);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdSearchFilter);
//    }

    // Получение фильтра по UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<SearchFilter> getSearchFilter(@PathVariable UUID uuid) {
        SearchFilter searchFilter = searchFilterService.getSearchFilter(uuid);
        if (searchFilter != null) {
            return ResponseEntity.ok(searchFilter);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Получение всех фильтров
    @GetMapping("/all")
    public ResponseEntity<List<SearchFilter>> getAllSearchFilters() {
        List<SearchFilter> searchFilters = searchFilterService.getAllSearchFilters();
        return ResponseEntity.ok(searchFilters);
    }

    // Получение всех фильтров для конкретного арендатора
    @GetMapping("/tenant/{tenantUuid}")
    public ResponseEntity<List<SearchFilter>> getSearchFiltersByTenant(@PathVariable UUID tenantUuid) {
        List<SearchFilter> searchFilters = searchFilterService.getSearchFiltersByTenant(tenantUuid);
        return ResponseEntity.ok(searchFilters);
    }

    // Обновление фильтра
    @PutMapping("/update/{uuid}")
    public ResponseEntity<SearchFilter> updateSearchFilter(@PathVariable UUID uuid, @RequestBody SearchFilter searchFilter) {
        if (searchFilterService.getSearchFilter(uuid) != null) {
            searchFilter.setUuid(uuid);
            SearchFilter updatedSearchFilter = searchFilterService.updateSearchFilter(searchFilter);
            return ResponseEntity.ok(updatedSearchFilter);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Удаление фильтра
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteSearchFilter(@PathVariable UUID uuid) {
        if (searchFilterService.getSearchFilter(uuid) != null) {
            searchFilterService.deleteSearchFilter(uuid);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}