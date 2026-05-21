# Realtime Stock Trading Platform 

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
