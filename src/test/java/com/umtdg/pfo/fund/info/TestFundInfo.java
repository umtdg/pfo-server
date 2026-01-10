package com.umtdg.pfo.fund.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestFundInfo {
    String getProvider(FundInfo info) {
        return (String) ReflectionTestUtils.getField(info, "provider");
    }

    @Test
    void shouldDefaultConstructFundInfo() {
        FundInfo info = new FundInfo();

        assertNull(info.getCode());
        assertNull(info.getTitle());
        assertNull(getProvider(info));
        assertNull(info.getDate());
        assertEquals(0.0f, info.getPrice());
        assertEquals(0.0f, info.getTotalValue());
    }
}
