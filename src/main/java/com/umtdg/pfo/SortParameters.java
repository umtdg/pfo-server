package com.umtdg.pfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.umtdg.pfo.exception.SortByValidationException;

import jakarta.validation.constraints.NotNull;

public class SortParameters {
    @NotNull
    List<String> sortBy;

    @NotNull
    Sort.Direction sortDirection;

    public SortParameters() {
        this.sortBy = new ArrayList<>();
        this.sortDirection = Sort.Direction.ASC;
    }

    public List<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Sort validate(Set<String> allowedProperties, String defaultProperty)
        throws SortByValidationException {
        if (sortBy.isEmpty() && defaultProperty != null) {
            sortBy.add(defaultProperty);
        }

        List<String> invalidSortBy = sortBy
            .stream()
            .filter(by -> !allowedProperties.contains(by))
            .toList();
        if (!invalidSortBy.isEmpty()) {
            throw new SortByValidationException(invalidSortBy);
        }

        return Sort
            .by(sortBy.stream().map(by -> new Order(sortDirection, by)).toList());
    }
}
