package com.umtdg.pfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class SortParameters {
    List<String> sortBy;

    String sortDirection;

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public class ValidationException extends RuntimeException {
        public ValidationException(List<String> invalidSortBy) {
            super(String.format("Invalid sort by properties %s", invalidSortBy));
        }

        public ValidationException(String direction) {
            super(String.format("Invalid sort direction %s", direction));
        }
    }

    public SortParameters() {
        this.sortBy = new ArrayList<>();
        this.sortDirection = "desc";
    }

    public List<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<String> sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Sort validate(Set<String> allowedProperties, Sort defaultSort)
        throws ValidationException {
        List<String> invalidSortBy = sortBy
            .stream()
            .filter(by -> !allowedProperties.contains(by))
            .toList();
        if (!invalidSortBy.isEmpty()) {
            throw new ValidationException(invalidSortBy);
        }

        if (sortBy.isEmpty()) return defaultSort;

        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            return Sort
                .by(sortBy.stream().map(by -> new Order(direction, by)).toList());
        } catch (IllegalArgumentException exc) {
            throw new ValidationException(sortDirection);
        }
    }

    @Override
    public String toString() {
        return "SortParameters [sortBy=" + sortBy + ", sortDirection=" + sortDirection
            + "]";
    }
}
