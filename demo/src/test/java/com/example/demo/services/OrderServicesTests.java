package com.example.demo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.entities.Stock;
import com.example.demo.entities.StockOrder;
import com.example.demo.entities.User;
import com.example.demo.repositories.StockRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.cli.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:order-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderServicesTests {

    @Autowired
    private UserServices userServices;

    @Autowired
    private StockService stockService;

    @Autowired
    private OrderServices orderServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void buyStockCreatesOrderAndUpdatesBalances() {
        User user = userServices.registerUser("Asha", "111", "asha@example.com", "pass", "TRADER", 1_000);
        Stock stock = stockService.addStock("ABC", 50, 20);

        StockOrder order = orderServices.buyStock(user.getId(), stock.getId(), 4);

        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        Stock savedStock = stockRepository.findById(stock.getId()).orElseThrow();

        assertThat(order.getAction()).isEqualTo("BUY");
        assertThat(order.getTotalAmount()).isEqualTo(200);
        assertThat(savedUser.getCashBalance()).isEqualTo(800);
        assertThat(savedStock.getAvailableQuantity()).isEqualTo(16);
        assertThat(orderServices.getPortfolio(user.getId()))
                .singleElement()
                .satisfies(position -> {
                    assertThat(position.stockName()).isEqualTo("ABC");
                    assertThat(position.quantity()).isEqualTo(4);
                });
    }

    @Test
    void sellStockRequiresOwnedQuantity() {
        User user = userServices.registerUser("Ravi", "222", "ravi@example.com", "pass", "TRADER", 1_000);
        Stock stock = stockService.addStock("XYZ", 25, 10);

        assertThatThrownBy(() -> orderServices.sellStock(user.getId(), stock.getId(), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not own enough");
    }

    @Test
    void buyStockRequiresEnoughCash() {
        User user = userServices.registerUser("Mira", "333", "mira@example.com", "pass", "TRADER", 10);
        Stock stock = stockService.addStock("CASH", 50, 10);

        assertThatThrownBy(() -> orderServices.buyStock(user.getId(), stock.getId(), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("enough cash");
    }
}
