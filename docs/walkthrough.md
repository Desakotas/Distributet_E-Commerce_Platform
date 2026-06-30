# System Walkthrough

## 1. Architecture

Start by showing the project structure:

```text
api-gateway -> auth/catalog/order/inventory/payment/notification -> Kafka -> PostgreSQL
```

Emphasize that each business service owns its own database. Services do not share tables directly.

## 2. Spring Security

Demo registration:

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","password":"password123"}'
```

Demo login:

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123"}'
```

Explain that users are stored in the auth service database, passwords are hashed, the JWT contains `roles`, and both the gateway and business services validate the token signature.

## 3. Non-CRUD Feature

Dynamic pricing quote:

```bash
curl -X POST http://localhost:8080/api/products/1/quote \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"quantity":5,"customerTier":"VIP"}'
```

Explain the pricing rules: quantity discount, customer tier discount, and a maximum discount cap.

## 4. Local Transactions

Code locations:

| Service | Class | Method |
| --- | --- | --- |
| order-service | `OrderService` | `checkout` |
| inventory-service | `InventoryService` | `reserveForOrder` |
| payment-service | `PaymentService` | `authorize` |

Also mention that order, inventory, and payment services write `OutboxEvent` rows in the same transaction as the business data.

## 5. Distributed Transaction With Saga

Successful flow:

```text
order.created -> inventory.reserved -> payment.authorized -> order PAID
```

Failure flow:

```text
order.created -> inventory.reserved -> payment.failed -> order CANCELLED + inventory release
```

Use `MOCK_FAIL` during checkout to trigger the compensation flow.

## 6. Kafka

Discuss these topics:

```text
order.created
inventory.reserved
inventory.rejected
payment.authorized
payment.failed
```

## 7. Notifications

After a successful or failed checkout, query notifications:

```bash
curl http://localhost:8080/api/notifications/my \
  -H "Authorization: Bearer $TOKEN"
```

Explain that notification creation is asynchronous and decoupled from checkout.

## 8. Scaling

```bash
docker compose up --build --scale order-service=2 --scale inventory-service=2 --scale payment-service=2 --scale notification-service=2
```

Key points:

- Service replicas are stateless.
- PostgreSQL stores external state.
- Kafka consumer groups let replicas of the same service compete for events.
- Events use `orderId` as the key, which helps keep events for the same order in partition order.
- Outbox publishers use database locking so multiple replicas do not intentionally publish the same outbox row at the same time.
