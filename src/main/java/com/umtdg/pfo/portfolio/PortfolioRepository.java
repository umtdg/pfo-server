package com.umtdg.pfo.portfolio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
}
