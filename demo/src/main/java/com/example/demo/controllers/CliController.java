package com.example.demo.controllers;

import com.example.demo.entities.Stock;
import com.example.demo.entities.User;
import com.example.demo.services.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class CliController {

    @Autowired
    private StockService stockService;

    private final Scanner sc = new Scanner(System.in);

    public void start() {

        while (true) {

            System.out.println("\n====== STOCK TRADING SYSTEM ======");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Add Stock");
            System.out.println("4. View Stocks");
            System.out.println("5. Exit");

            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    addUser();
                    break;

                case 2:
                    viewUsers();
                    break;

                case 3:
                    addStock();
                    break;

                case 4:
                    viewStocks();
                    break;

                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    

    private void addUser() {

        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Role: ");
        String role = sc.nextLine();

        User user = new User();

        user.setName(name);
        user.setPhno(phone);
        user.setEmail(email);
        user.setRole(role);

        stockService.addUser(user);

        System.out.println("User Added Successfully!");
    }

    

    private void viewUsers() {

        List<User> users = stockService.getAllUsers();

        System.out.println("\n===== USERS =====");

        for (User user : users) {

            System.out.println(
                    user.getId() + " | " +
                    user.getName() + " | " +
                    user.getEmail() + " | " +
                    user.getRole()
            );
        }
    }

    

    private void addStock() {

        sc.nextLine();

        System.out.print("Enter Stock Name: ");
        String stockName = sc.nextLine();

        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        System.out.print("Enter Quantity: ");
        int qty = sc.nextInt();

        System.out.print("Enter User ID: ");
        Long userId = sc.nextLong();

        User user = stockService.getUserById(userId);

        if (user == null) {

            System.out.println("User Not Found!");
            return;
        }

        Stock stock = new Stock();

        stock.setName(stockName);
        stock.setPrice(price);
        stock.setAmt(qty);
        stock.setUser(user);

        stockService.addStock(stock);

        System.out.println("Stock Added Successfully!");
    }

    

    private void viewStocks() {

        List<Stock> stocks = stockService.getAllStocks();

        System.out.println("\n===== STOCKS =====");

        for (Stock stock : stocks) {

            System.out.println(
                    stock.getId() + " | " +
                    stock.getName() + " | " +
                    stock.getPrice() + " | Qty: " +
                    stock.getAmt()
            );
        }
    }
}