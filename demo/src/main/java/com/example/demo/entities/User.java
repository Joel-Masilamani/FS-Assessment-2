package com.example.demo.entities;

import org.springframework.data.annotation.Id;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String name;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    long phno;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String role;

}
