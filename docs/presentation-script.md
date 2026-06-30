# Presentation Script

I would like to introduce my e-commerce backend project.

This project is built with Java 17 and Spring Boot 3. It simulates the core backend workflow of an online shopping platform, including user authentication, product management, shopping cart management, checkout, inventory reservation, order management, payment processing, and user notifications.

The project is designed as a distributed backend with multiple Spring Boot services. All client requests go through an API Gateway. Behind the gateway, each service owns a clear business responsibility, such as authentication, catalog, order, inventory, payment, and notification. The shopping cart is implemented inside the order service because it is closely related to checkout and order creation. In Docker mode, each business service owns its own PostgreSQL database, so services do not share tables directly.

Each service follows a layered architecture. A request first enters the Controller, then goes to the Service layer, then the Repository layer, and finally the database. This separation keeps business logic independent from the API layer and makes the code easier to maintain.

For authentication and authorization, I use Spring Security with JWT. The auth service validates the user's username and password, hashes passwords with BCrypt, and generates a JWT after login. The API Gateway validates the token before forwarding protected requests, and the business services also validate JWTs. I also implemented role-based access control. Normal users can access their own orders, payments, and notifications, while administrators can manage products and inventory.

The RESTful APIs cover the main shopping workflow. Users can log in, browse products, request a dynamic price quote, add products to the shopping cart, update item quantities, remove cart items, check out from the cart, view their orders, check payment status, view inventory, and read notifications. Administrators can create products and restock inventory.

One important non-CRUD feature is the dynamic pricing quote API. It calculates the final price based on product price, quantity discount, customer tier discount, and a maximum discount cap.

The most important part of the project is checkout. Before checkout, the user can add multiple products to the shopping cart. When the user checks out from the cart, the order service loads the current user's cart items, creates an order, creates order lines, clears the cart, and writes an outbox event in the same local transaction. Then Kafka is used to publish the `order.created` event. The inventory service listens to this event, locks the related inventory rows, validates stock, reserves inventory, and publishes either `inventory.reserved` or `inventory.rejected`.

After inventory is reserved, the payment service starts payment processing. In this demo, payment is simulated with two modes: `MOCK_OK` for success and `MOCK_FAIL` for failure. If payment succeeds, the payment service publishes `payment.authorized`; if it fails, it publishes `payment.failed`.

The project uses local transactions inside each service. For example, cart checkout, order creation, inventory reservation, and payment authorization are wrapped with Spring `@Transactional`. During cart checkout, reading the cart, creating the order, saving the outbox event, and clearing the cart are handled as one local transaction. If any step inside one service fails, the local database transaction is rolled back automatically.

For distributed transaction consistency, the project uses the Saga pattern instead of two-phase commit. Each service commits its own local transaction and communicates through Kafka events. If payment fails, the system performs compensation: the order is cancelled, reserved inventory is released, and a failure notification is created.

Kafka is integrated as the asynchronous messaging layer. The main topics include `order.created`, `inventory.reserved`, `inventory.rejected`, `payment.authorized`, and `payment.failed`. This event-driven design reduces coupling between services and makes it easier to add new consumers, such as notification or analytics modules, without changing the checkout logic.

The project also uses the transactional outbox pattern. Instead of publishing Kafka messages directly inside business logic, services first save outbox records in the same local transaction as the business data. A scheduled publisher then sends pending outbox events to Kafka. This reduces the risk of losing events after the database transaction commits.

Finally, the project supports horizontal scaling. The services are stateless, Kafka consumers use consumer groups, and the README includes Docker Compose scaling commands and a Kubernetes HPA example. This means multiple instances of the same service can run at the same time and share event processing.

In summary, this project demonstrates RESTful API design, Spring Security with JWT, local transactions, Kafka-based asynchronous communication, Saga-based distributed transactions, and basic horizontal scalability.
