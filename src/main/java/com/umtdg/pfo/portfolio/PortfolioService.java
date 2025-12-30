package com.umtdg.pfo.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.umtdg.pfo.portfolio.dto.PortfolioCreate;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.portfolio.fund.PortfolioFund;
import com.umtdg.pfo.portfolio.fund.PortfolioFundRepository;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundAdd;

@Service
public class PortfolioService {
    private static final String NOT_FOUND_CONTEXT = "Portfolio";

    private final PortfolioRepository repository;
    private final PortfolioFundRepository portfolioFundRepository;

    public PortfolioService(
        PortfolioRepository repository,
        PortfolioFundRepository portfolioFundRepository
    ) {
        this.repository = repository;
        this.portfolioFundRepository = portfolioFundRepository;
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

    public void addFunds(Portfolio portfolio, List<PortfolioFundAdd> addInfos) {
        if (addInfos == null || addInfos.isEmpty()) {
            return;
        }

        UUID portfolioId = portfolio.getId();

        List<PortfolioFund> addList = addInfos
            .stream()
            .map(addInfo -> addInfo.toPortfolioFund(portfolioId))
            .toList();

        List<PortfolioFund> funds = portfolioFundRepository
            .findAllByPortfolioId(portfolioId, null);
        funds.addAll(addList);

        float totalWeights = 0.0f;
        for (PortfolioFund fund : funds) {
            totalWeights += fund.getWeight();
        }

        for (PortfolioFund fund : funds) {
            fund.setNormWeight(fund.getWeight() / totalWeights);
        }

        portfolioFundRepository.saveAll(funds);
    }

    public void removeFunds(Portfolio portfolio, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return;
        }

        portfolioFundRepository
            .deleteAllByPortfolioIdAndFundCodeIn(portfolio.getId(), codes);
    }
}
