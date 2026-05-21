package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.StockOrder;
import com.example.demo.entities.User;
import java.util.List;

@Repository
public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {
    List<StockOrder> findByUserOrderByOrderDateDesc(User user);
}
