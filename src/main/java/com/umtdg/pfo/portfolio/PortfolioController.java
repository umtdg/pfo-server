package com.umtdg.pfo.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.portfolio.dto.PortfolioCreate;
import com.umtdg.pfo.portfolio.dto.PortfolioUpdate;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/p")
public class PortfolioController {
    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @PostMapping
    public Portfolio create(@RequestBody PortfolioCreate createInfo) {
        return service.createPortfolio(createInfo);
    }

    @PutMapping("{id}")
    @Transactional
    public void update(
        @PathVariable UUID id,
        @RequestBody @Valid PortfolioUpdate update
    )
        throws NotFoundException {
        Portfolio portfolio = service.getPortfolio(id);

        service.removeFunds(portfolio, update.removeCodes());
        service.addFunds(portfolio, update.addCodes());
    }

    @GetMapping
    public List<Portfolio> getAll() {
        // TODO: Return User's portfolio set

        return service.getAllPortfolios();
    }

    @GetMapping("{id}")
    public Portfolio getOne(@PathVariable UUID id) throws NotFoundException {
        // TODO: Find one portfolio with owner id matching current user

        return service.getPortfolio(id);
    }
}
