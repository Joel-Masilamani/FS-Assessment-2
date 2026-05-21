package com.example.demo.services;

import com.example.demo.entities.Report;
import com.example.demo.entities.User;
import com.example.demo.repositories.ReportRepository;
import com.example.demo.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReportServices {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final OrderServices orderServices;

    public ReportServices(ReportRepository reportRepository, UserRepository userRepository, OrderServices orderServices) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.orderServices = orderServices;
    }

    public Report generatePortfolioReport(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        double portfolioValue = orderServices.getPortfolio(userId).stream()
                .mapToDouble(PortfolioPosition::marketValue)
                .sum();

        Report report = new Report();
        report.setUser(user);
        report.setReportDate(LocalDateTime.now());
        report.setDescription("Cash balance: " + user.getCashBalance() + ", portfolio value: " + portfolioValue);
        return reportRepository.save(report);
    }

    public List<Report> getReportsForUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return reportRepository.findByUserOrderByReportDateDesc(user);
    }
}
