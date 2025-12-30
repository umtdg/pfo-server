package com.umtdg.pfo.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.umtdg.pfo.portfolio.dto.PortfolioCreate;
import com.umtdg.pfo.exception.NotFoundException;

@Service
public class PortfolioService {
    private static final String NOT_FOUND_CONTEXT = "Portfolio";

    private final PortfolioRepository repository;

    public PortfolioService(PortfolioRepository repository) {
        this.repository = repository;
    }

    public Portfolio createPortfolio(PortfolioCreate createInfo) {
        Portfolio portfolio = new Portfolio(createInfo.name());

        return repository.save(portfolio);
    }

    public List<Portfolio> getAllPortfolios() {
        return repository.findAll();
    }

    public Portfolio getPortfolio(UUID id) throws NotFoundException {
        return repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString()));
    }

    public void deletePortfolio(UUID id) {
        repository.deleteById(id);
    }
}
