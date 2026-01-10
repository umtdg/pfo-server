package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.umtdg.pfo.exception.SortByValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class TestSortParameters {
    class SortByClass {
        String arg1;
        int arg2;
        float arg3;
        double arg4;
    }

    static final Set<String> ALLOWED_SORT_BY_CLASS_PROPERTIES = Set
        .of(
            "arg1",
            "arg2",
            "arg3",
            "arg4"
        );

    @Test
    void givenSortParameters_thenShouldGetAndSetSortByAndDirection() {
        SortParameters sortParameters = new SortParameters();
        assertInstanceOf(ArrayList.class, sortParameters.sortBy);
        assertEquals(0, sortParameters.sortBy.size());
        assertEquals(Sort.Direction.ASC, sortParameters.sortDirection);

        List<String> sortBy = List.of("arg1", "arg2", "arg3");
        sortParameters.setSortBy(sortBy);
        assertEquals(3, sortParameters.getSortBy().size());
        assertIterableEquals(sortBy, sortParameters.getSortBy());

        sortParameters.setSortDirection(Sort.Direction.DESC);
        assertEquals(Sort.Direction.DESC, sortParameters.getSortDirection());
    }

    @Test
    void givenSortParameters_whenItHasNotAllowedProperties_thenThrow() {
        SortParameters sortParameters = new SortParameters();
        sortParameters.setSortBy(List.of("arg1", "arg2", "arg5"));

        assertThrowsExactly(SortByValidationException.class, () -> {
            sortParameters.validate(ALLOWED_SORT_BY_CLASS_PROPERTIES, null);
        });
    }

    @Test
    void givenSortParameters_whenAllPropertiesAreAllowed_thenShouldReturnSortedSort() {
        SortParameters sortParameters = new SortParameters();
        sortParameters.setSortBy(List.of("arg1", "arg2", "arg3"));

        assertDoesNotThrow(() -> {
            sortParameters.validate(ALLOWED_SORT_BY_CLASS_PROPERTIES, null);
        });

        try {
            Sort sort = sortParameters
                .validate(ALLOWED_SORT_BY_CLASS_PROPERTIES, null);
            assertTrue(sort.isSorted());
            assertTrue(sort.getOrderFor("arg1").isAscending());
            assertTrue(sort.getOrderFor("arg2").isAscending());
            assertTrue(sort.getOrderFor("arg3").isAscending());
            assertNull(sort.getOrderFor("arg4"));
        } catch (SortByValidationException exc) {
            // validate is guaranteed to not throw
        }
    }

    @Test
    void givenSortParameters_whenAllPropertiesAreAllowed_thenShouldReturnUnSortedSort() {
        SortParameters sortParameters = new SortParameters();

        assertDoesNotThrow(() -> {
            sortParameters.validate(ALLOWED_SORT_BY_CLASS_PROPERTIES, null);
        });

        try {
            Sort sort = sortParameters
                .validate(ALLOWED_SORT_BY_CLASS_PROPERTIES, null);
            assertTrue(sort.isUnsorted());
            assertNull(sort.getOrderFor("arg1"));
            assertNull(sort.getOrderFor("arg2"));
            assertNull(sort.getOrderFor("arg3"));
            assertNull(sort.getOrderFor("arg4"));
        } catch (SortByValidationException exc) {
            // validate is guaranteed to not throw
        }
    }
}
