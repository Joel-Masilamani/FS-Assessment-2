package com.example.demo.services;

import com.example.demo.entities.Stock;
import com.example.demo.entities.StockOrder;
import com.example.demo.entities.Transaction;
import com.example.demo.entities.User;
import com.example.demo.repositories.StockOrderRepository;
import com.example.demo.repositories.StockRepository;
import com.example.demo.repositories.TransactionRepository;
import com.example.demo.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServices {

    private static final String BUY = "BUY";
    private static final String SELL = "SELL";
    private static final String COMPLETED = "COMPLETED";

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final StockOrderRepository stockOrderRepository;
    private final TransactionRepository transactionRepository;

    public OrderServices(
            UserRepository userRepository,
            StockRepository stockRepository,
            StockOrderRepository stockOrderRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.stockOrderRepository = stockOrderRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public StockOrder buyStock(long userId, long stockId, long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        User user = getUser(userId);
        Stock stock = getStock(stockId);
        double totalAmount = stock.getPrice() * quantity;

        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough market quantity is available.");
        }
        if (user.getCashBalance() < totalAmount) {
            throw new IllegalArgumentException("User does not have enough cash balance.");
        }

        user.setCashBalance(user.getCashBalance() - totalAmount);
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        return recordTrade(user, stock, BUY, quantity, totalAmount);
    }

    @Transactional
    public StockOrder sellStock(long userId, long stockId, long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        User user = getUser(userId);
        Stock stock = getStock(stockId);
        long ownedQuantity = calculateOwnedQuantity(user, stockId);

        if (ownedQuantity < quantity) {
            throw new IllegalArgumentException("User does not own enough shares to sell.");
        }

        double totalAmount = stock.getPrice() * quantity;
        user.setCashBalance(user.getCashBalance() + totalAmount);
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        return recordTrade(user, stock, SELL, quantity, totalAmount);
    }

    public List<StockOrder> getOrdersForUser(long userId) {
        return stockOrderRepository.findByUserOrderByOrderDateDesc(getUser(userId));
    }

    public List<PortfolioPosition> getPortfolio(long userId) {
        User user = getUser(userId);
        Map<Long, PortfolioAccumulator> positions = new LinkedHashMap<>();

        for (Transaction transaction : transactionRepository.findByUserOrderByTransactionDateDesc(user)) {
            Stock stock = transaction.getOrder().getStock();
            PortfolioAccumulator accumulator = positions.computeIfAbsent(
                    stock.getId(),
                    id -> new PortfolioAccumulator(stock.getId(), stock.getName(), stock.getPrice())
            );

            if (BUY.equals(transaction.getType())) {
                accumulator.quantity += transaction.getQuantity();
            } else if (SELL.equals(transaction.getType())) {
                accumulator.quantity -= transaction.getQuantity();
            }
        }

        return positions.values().stream()
                .filter(position -> position.quantity > 0)
                .map(position -> new PortfolioPosition(
                        position.stockId,
                        position.stockName,
                        position.quantity,
                        position.currentPrice,
                        position.quantity * position.currentPrice
                ))
                .toList();
    }

    private StockOrder recordTrade(User user, Stock stock, String action, long quantity, double totalAmount) {
        userRepository.save(user);
        stockRepository.save(stock);

        StockOrder order = new StockOrder();
        order.setOrderDate(LocalDateTime.now());
        order.setAction(action);
        order.setQuantity(quantity);
        order.setPricePerShare(stock.getPrice());
        order.setTotalAmount(totalAmount);
        order.setStatus(COMPLETED);
        order.setUser(user);
        order.setStock(stock);
        StockOrder savedOrder = stockOrderRepository.save(order);

        Transaction transaction = new Transaction();
        transaction.setType(action);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setQuantity(quantity);
        transaction.setPricePerShare(stock.getPrice());
        transaction.setTotalAmount(totalAmount);
        transaction.setUser(user);
        transaction.setOrder(savedOrder);
        transactionRepository.save(transaction);

        return savedOrder;
    }

    private long calculateOwnedQuantity(User user, long stockId) {
        long quantity = 0;
        for (Transaction transaction : transactionRepository.findByUserOrderByTransactionDateDesc(user)) {
            if (transaction.getOrder().getStock().getId() != stockId) {
                continue;
            }
            if (BUY.equals(transaction.getType())) {
                quantity += transaction.getQuantity();
            } else if (SELL.equals(transaction.getType())) {
                quantity -= transaction.getQuantity();
            }
        }
        return quantity;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Stock getStock(long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found."));
    }

    private static class PortfolioAccumulator {
        private final long stockId;
        private final String stockName;
        private final double currentPrice;
        private long quantity;

        private PortfolioAccumulator(long stockId, String stockName, double currentPrice) {
            this.stockId = stockId;
            this.stockName = stockName;
            this.currentPrice = currentPrice;
        }
    }
}
