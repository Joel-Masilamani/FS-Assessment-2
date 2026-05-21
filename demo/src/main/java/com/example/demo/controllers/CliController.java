package com.example.demo.controllers;

import com.example.demo.entities.Report;
import com.example.demo.entities.Stock;
import com.example.demo.entities.StockOrder;
import com.example.demo.entities.Transaction;
import com.example.demo.entities.User;
import com.example.demo.services.OrderServices;
import com.example.demo.services.PortfolioPosition;
import com.example.demo.services.ReportServices;
import com.example.demo.services.StockService;
import com.example.demo.services.TransactionServices;
import com.example.demo.services.UserServices;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class CliController {

    private final UserServices userServices;
    private final StockService stockService;
    private final OrderServices orderServices;
    private final TransactionServices transactionServices;
    private final ReportServices reportServices;
    private final Scanner sc = new Scanner(System.in);

    public CliController(
            UserServices userServices,
            StockService stockService,
            OrderServices orderServices,
            TransactionServices transactionServices,
            ReportServices reportServices
    ) {
        this.userServices = userServices;
        this.stockService = stockService;
        this.orderServices = orderServices;
        this.transactionServices = transactionServices;
        this.reportServices = reportServices;
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = readInt("Enter Choice: ");

            try {
                switch (choice) {
                    case 1 -> addUser();
                    case 2 -> viewUsers();
                    case 3 -> addStock();
                    case 4 -> viewStocks();
                    case 5 -> updateStockPrice();
                    case 6 -> buyStock();
                    case 7 -> sellStock();
                    case 8 -> viewPortfolio();
                    case 9 -> viewOrders();
                    case 10 -> viewTransactions();
                    case 11 -> generateReport();
                    case 12 -> viewReports();
                    case 0 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n====== STOCK TRADING SYSTEM ======");
        System.out.println("1. Register User");
        System.out.println("2. View Users");
        System.out.println("3. Add Market Stock");
        System.out.println("4. View Market Stocks");
        System.out.println("5. Update Stock Price");
        System.out.println("6. Buy Stock");
        System.out.println("7. Sell Stock");
        System.out.println("8. View Portfolio");
        System.out.println("9. View Orders");
        System.out.println("10. View Transactions");
        System.out.println("11. Generate Portfolio Report");
        System.out.println("12. View Reports");
        System.out.println("0. Exit");
    }

    private void addUser() {
        String name = readText("Enter Name: ");
        String phone = readText("Enter Phone: ");
        String email = readText("Enter Email: ");
        String password = readText("Enter Password: ");
        String role = readText("Enter Role: ");
        double openingBalance = readDouble("Enter Opening Cash Balance: ");

        User user = userServices.registerUser(name, phone, email, password, role, openingBalance);
        System.out.println("User added with ID " + user.getId() + ".");
    }

    private void viewUsers() {
        List<User> users = userServices.getAllUsers();
        System.out.println("\n===== USERS =====");
        for (User user : users) {
            System.out.println(user.getId() + " | " + user.getName() + " | " + user.getEmail()
                    + " | " + user.getRole() + " | Cash: " + user.getCashBalance());
        }
    }

    private void addStock() {
        String stockName = readText("Enter Stock Name: ");
        double price = readDouble("Enter Price: ");
        long quantity = readLong("Enter Available Quantity: ");

        Stock stock = stockService.addStock(stockName, price, quantity);
        System.out.println("Stock added with ID " + stock.getId() + ".");
    }

    private void viewStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        System.out.println("\n===== MARKET STOCKS =====");
        for (Stock stock : stocks) {
            System.out.println(stock.getId() + " | " + stock.getName() + " | Price: "
                    + stock.getPrice() + " | Available: " + stock.getAvailableQuantity());
        }
    }

    private void updateStockPrice() {
        long stockId = readLong("Enter Stock ID: ");
        double price = readDouble("Enter New Price: ");

        Stock stock = stockService.updateStockPrice(stockId, price);
        System.out.println("Updated " + stock.getName() + " to " + stock.getPrice() + ".");
    }

    private void buyStock() {
        long userId = readLong("Enter User ID: ");
        long stockId = readLong("Enter Stock ID: ");
        long quantity = readLong("Enter Quantity: ");

        StockOrder order = orderServices.buyStock(userId, stockId, quantity);
        System.out.println("Buy order completed. Order ID: " + order.getId()
                + ", Total: " + order.getTotalAmount());
    }

    private void sellStock() {
        long userId = readLong("Enter User ID: ");
        long stockId = readLong("Enter Stock ID: ");
        long quantity = readLong("Enter Quantity: ");

        StockOrder order = orderServices.sellStock(userId, stockId, quantity);
        System.out.println("Sell order completed. Order ID: " + order.getId()
                + ", Total: " + order.getTotalAmount());
    }

    private void viewPortfolio() {
        long userId = readLong("Enter User ID: ");
        User user = userServices.getUserById(userId);
        List<PortfolioPosition> portfolio = orderServices.getPortfolio(userId);

        System.out.println("\n===== PORTFOLIO: " + user.getName() + " =====");
        System.out.println("Cash Balance: " + user.getCashBalance());
        for (PortfolioPosition position : portfolio) {
            System.out.println(position.stockId() + " | " + position.stockName()
                    + " | Qty: " + position.quantity()
                    + " | Price: " + position.currentPrice()
                    + " | Value: " + position.marketValue());
        }
    }

    private void viewOrders() {
        long userId = readLong("Enter User ID: ");
        System.out.println("\n===== ORDERS =====");
        for (StockOrder order : orderServices.getOrdersForUser(userId)) {
            System.out.println(order.getId() + " | " + order.getAction() + " | "
                    + order.getStock().getName() + " | Qty: " + order.getQuantity()
                    + " | Total: " + order.getTotalAmount() + " | " + order.getStatus());
        }
    }

    private void viewTransactions() {
        long userId = readLong("Enter User ID: ");
        System.out.println("\n===== TRANSACTIONS =====");
        for (Transaction transaction : transactionServices.getTransactionsForUser(userId)) {
            System.out.println(transaction.getId() + " | " + transaction.getType() + " | "
                    + transaction.getOrder().getStock().getName() + " | Qty: "
                    + transaction.getQuantity() + " | Total: " + transaction.getTotalAmount());
        }
    }

    private void generateReport() {
        long userId = readLong("Enter User ID: ");
        Report report = reportServices.generatePortfolioReport(userId);
        System.out.println("Report generated with ID " + report.getId() + ": " + report.getDescription());
    }

    private void viewReports() {
        long userId = readLong("Enter User ID: ");
        System.out.println("\n===== REPORTS =====");
        for (Report report : reportServices.getReportsForUser(userId)) {
            System.out.println(report.getId() + " | " + report.getReportDate() + " | " + report.getDescription());
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}
