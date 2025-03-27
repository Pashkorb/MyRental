package com.example.rental.rentalListing;

import com.example.rental.filters.SearchFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RentalListingSpecification {

    public static Specification<RentalListing> filterBySearchFilter(SearchFilter searchFilter) {
        return new Specification<RentalListing>() {
            @Override
            public Predicate toPredicate(Root<RentalListing> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // Фильтр по минимальной цене
                if (searchFilter.getMinimumPrice() > 0) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rentalCost"), searchFilter.getMinimumPrice()));
                }

                // Фильтр по максимальной цене
                if (searchFilter.getMaximumPrice() > 0) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rentalCost"), searchFilter.getMaximumPrice()));
                }

                // Фильтр по минимальной площади
                if (searchFilter.getMinimumArea() > 0) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("property").get("totalArea"), searchFilter.getMinimumArea()));
                }

                // Фильтр по максимальной площади
                if (searchFilter.getMaximumArea() > 0) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("property").get("totalArea"), searchFilter.getMaximumArea()));
                }

                // Фильтр по количеству комнат
                if (searchFilter.getNumberOfRooms() > 0) {
                    predicates.add(criteriaBuilder.equal(root.get("property").get("numberOfRooms"), searchFilter.getNumberOfRooms()));
                }

                // Фильтр по удобствам (amenities)
                if (searchFilter.getAmenities() != null && !searchFilter.getAmenities().isEmpty()) {
                    String[] amenities = searchFilter.getAmenities().split(",");
                    for (String amenity : amenities) {
                        predicates.add(criteriaBuilder.like(root.get("property").get("amenities"), "%" + amenity.trim() + "%"));
                    }
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}