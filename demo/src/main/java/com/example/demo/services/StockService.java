package com.example.demo.services;

import com.example.demo.entities.Stock;
import com.example.demo.repositories.StockRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock addStock(String name, double price, long availableQuantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative.");
        }
        stockRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            throw new IllegalArgumentException("A stock with this name already exists.");
        });

        Stock stock = new Stock();
        stock.setName(name);
        stock.setPrice(price);
        stock.setAvailableQuantity(availableQuantity);
        return stockRepository.save(stock);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found."));
    }

    public Stock updateStockPrice(long stockId, double newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Stock price must be greater than zero.");
        }
        Stock stock = getStockById(stockId);
        stock.setPrice(newPrice);
        return stockRepository.save(stock);
    }
}
