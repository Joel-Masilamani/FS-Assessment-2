package com.example.demo.entities;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String name;

    @Column(unique = true)
    BigDecimal price;

    @Column(unique = true)
    long amt;

}
