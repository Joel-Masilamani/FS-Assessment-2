package com.example.demo.services;

import com.example.demo.entities.Transaction;
import com.example.demo.entities.User;
import com.example.demo.repositories.TransactionRepository;
import com.example.demo.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionServices {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServices(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactionsForUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return transactionRepository.findByUserOrderByTransactionDateDesc(user);
    }
}
