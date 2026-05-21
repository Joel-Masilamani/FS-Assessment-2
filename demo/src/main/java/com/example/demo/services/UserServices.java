package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String phone, String email, String password, String role, double openingBalance) {
        if (isBlank(name) || isBlank(phone) || isBlank(email) || isBlank(password) || isBlank(role)) {
            throw new IllegalArgumentException("All user fields are required.");
        }
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        if (userRepository.findByPhno(phone).isPresent()) {
            throw new IllegalArgumentException("A user with this phone number already exists.");
        }

        User user = new User();
        user.setName(name);
        user.setPhno(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setCashBalance(openingBalance);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
