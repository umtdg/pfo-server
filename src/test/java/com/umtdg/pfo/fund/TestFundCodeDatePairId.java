package com.umtdg.pfo.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestFundCodeDatePairId {
    String getCode(FundCodeDatePairId id) {
        return (String) ReflectionTestUtils.getField(id, "code");
    }

    LocalDate getDate(FundCodeDatePairId id) {
        return (LocalDate) ReflectionTestUtils.getField(id, "date");
    }

    @Test
    void shouldDefaultConstructPairId() {
        FundCodeDatePairId id = new FundCodeDatePairId();
        assertNull(getCode(id));
        assertNull(getDate(id));
    }

    @Test
    void givenCodeAndDate_shouldConstructPairId() {
        String code = "FUN";
        LocalDate date = LocalDate.of(2025, 03, 14);

        FundCodeDatePairId id = new FundCodeDatePairId(code, date);

        assertEquals(code, getCode(id));
        assertEquals(date, getDate(id));
    }

    @Test
    void givenEqualPairIds_shouldBeEqualsAndHaveSameHashCode() {
        FundCodeDatePairId id1 = new FundCodeDatePairId(
            "FUN", LocalDate.of(2025, 03, 14)
        );
        FundCodeDatePairId id2 = new FundCodeDatePairId(
            "FUN", LocalDate.of(2025, 03, 14)
        );

        assertEquals(id1, id1);
        assertEquals(id1.hashCode(), id1.hashCode());

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void givenEqualPairIds_shouldBeNotEqualsAndHaveDifferentHashCode() {
        FundCodeDatePairId id1 = new FundCodeDatePairId(
            "FUN", LocalDate.of(2025, 03, 14)
        );
        FundCodeDatePairId id2 = new FundCodeDatePairId(
            "DUN", LocalDate.of(2025, 03, 14)
        );
        FundCodeDatePairId id3 = new FundCodeDatePairId(
            "FUN", LocalDate.of(2025, 03, 15)
        );
        String nonId = "abc";

        assertNotEquals(null, id1);
        assertNotEquals(id1, nonId);

        assertNotEquals(id1, id2);
        assertNotEquals(id1.hashCode(), id2.hashCode());

        assertNotEquals(id1, id3);
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}
