package com.umtdg.pfo.fund;

import java.util.HashSet;
import java.util.Set;

public class GetFundsResult {
    private Set<Fund> found = new HashSet<>();

    private Set<String> missing = new HashSet<>();

    public void addFound(Fund fund) {
        found.add(fund);
    }

    public void addMissing(String fundId) {
        missing.add(fundId);
    }

    public Set<Fund> getFound() {
        return this.found;
    }

    public Set<String> getMissing() {
        return this.missing;
    }
}
