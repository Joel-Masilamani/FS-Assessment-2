package com.example.demo.services;

public record PortfolioPosition(
        long stockId,
        String stockName,
        long quantity,
        double currentPrice,
        double marketValue
) {
}
