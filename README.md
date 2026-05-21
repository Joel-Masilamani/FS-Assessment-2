# Realtime Stock Trading Platform 

## Requirement Analysis :

### 1. Problem Statement

Traditional trading systems require fast and accurate handling of stock transactions. The system must process large numbers of buy and sell orders in real time while maintaining correct order matching, portfolio updates, and profit/loss tracking.

The challenge is to design a trading platform capable of:

* real-time order execution,
* price-time priority matching,
* portfolio management,
* and high-performance processing with minimal latency.

### 2. Purpose of the System

The purpose of the system is to provide a platform where traders can:

* register,
* place buy/sell orders,
* trade stocks efficiently,
* monitor holdings,
* and analyze profits/losses.

The system should simulate a real-world stock exchange order book using in-memory data structures.

## Functional Requirements :
1. Trader Management :
The system shall allow trader registration.
The system shall store trader details.
The system shall authenticate traders before trading operations.
2. Order Management :
The system shall allow traders to place orders.
The system shall generate a unique order ID for every order.
3. Trade Execution :
The system shall execute matched trades instantly.
The system shall generate trade records after successful execution.
4. Portfolio Management :
The system shall maintain trader portfolio holdings.
The system shall update holdings after each trade.
The system shall prevent negative stock holdings.

## Non-Functional Requirements

1. Performance :
The system shall process orders within milliseconds.
2. Scalability :
The system shall support thousands of concurrent traders.
3. Reliability :
The system shall ensure accurate trade execution.
4. Availability :
The system shall provide high availability during trading hours.
5. Security :
The system shall authenticate and authorize traders.
The system shall protect trading data from unauthorized access.
6. Consistency :
The system shall maintain data consistency.
No invalid or negative holdings shall exist.
7. Maintainability :
The system shall follow modular architecture.
8. Usability :
The CLI/UI shall provide clear trading options.
9. Accuracy :
The system shall execute trades using correct FIFO matching rules.

## SQL query : List top traders by profit and most traded stocks.
```sql
SELECT 
    u.user_id,
    u.name,
    SUM(
        CASE 
            WHEN t.mode = 'Sell' THEN o.total_price
            WHEN t.mode = 'Purchase' THEN -o.total_price
            ELSE 0
        END
    ) AS profit
FROM users u
JOIN transactions t 
    ON u.user_id = t.user_id
JOIN orders o 
    ON t.order_id = o.order_id
GROUP BY u.user_id, u.name
ORDER BY profit DESC;
```

## Output :

<img width="845" height="797" alt="image" src="https://github.com/user-attachments/assets/8ebafddf-c09e-408d-a2b9-b229c8fc01a5" />
