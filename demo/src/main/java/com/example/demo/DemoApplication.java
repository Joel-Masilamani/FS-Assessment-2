package com.example.demo;

import com.example.demo.controllers.CliController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private CliController cliController;

    @Value("${app.cli.enabled:true}")
    private boolean cliEnabled;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (cliEnabled) {
            cliController.start();
        }
    }
}
