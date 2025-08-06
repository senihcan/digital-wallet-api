# 💼 Wallet API

This is a RESTful Wallet Management API built with **Spring Boot**.  
It enables users to register, authenticate, manage wallets, perform deposit/withdraw transactions, and approve large transactions (by employees).

---

## 🚀 Features

- **JWT-based Authentication & Authorization**
- **Role-based Access Control** (`CUSTOMER` and `EMPLOYEE`)
- **Wallet operations:** create, filter, deposit, withdraw
- **Transaction approval process**
- **Swagger UI** with JWT bearer support
- **Comprehensive exception handling**
- **Unit & Integration Tests** for all core use-cases

---

## 📦 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- H2 (in-memory DB)
- JWT (auth)
- Swagger / OpenAPI
- JUnit 5

---

## 🔐 Roles

| Role     | Description                                 |
|----------|---------------------------------------------|
| CUSTOMER | Can register, login, create wallets, deposit, withdraw, and see their own transactions |
| EMPLOYEE | Can login, and **approve or deny** pending transactions |

---

## 🔧 Setup

1. **Clone the repository**

```bash
git clone https://github.com/senihcan/digital-wallet-api.git
cd wallet-api
```

2. **Run the application**

```bash
./mvnw spring-boot:run
```

3. **Access the API**

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🔑 Authentication

- **Register a user** at `/api/auth/register`
- **Login** via `/api/auth/login` and get JWT token
- **Authorize Swagger**:  
  Click "Authorize" in Swagger and enter:

```
Bearer <your_token>
```

---

## 🧪 Testing

To run all unit/integration tests:

```bash
./mvnw test
```

### Covered Test Cases

| Test Area           | Description                                                    |
|---------------------|----------------------------------------------------------------|
| Wallet Creation     | Valid wallet creation and filtering                            |
| Transactions        | Deposit & withdraw logic with balance and limits               |
| Negative Scenarios  | Insufficient funds, inactive wallet, unauthorized access       |
| Transaction Approval| Employee role approval/denial workflows                        |
| Auth Service        | Registration, login, duplicate username, wrong password checks |

---

## 🔁 API Endpoints Overview

### Auth

| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| POST   | `/api/auth/register` | Register as CUSTOMER or EMPLOYEE |
| POST   | `/api/auth/login`  | Login and receive JWT    |

### Wallet

| Method | Endpoint             | Role      | Description                       |
|--------|----------------------|-----------|-----------------------------------|
| POST   | `/api/wallet`        | CUSTOMER  | Create new wallet                 |
| GET    | `/api/wallet`        | CUSTOMER  | List wallets with filters         |
| POST   | `/api/wallet/deposit`| CUSTOMER  | Deposit money                     |
| POST   | `/api/wallet/withdraw` | CUSTOMER | Withdraw money (approval if > 1000) |
| GET    | `/api/wallet/{id}/transactions` | CUSTOMER | Get transaction history         |

### Transactions (EMPLOYEE only)

| Method | Endpoint                  | Role     | Description                |
|--------|---------------------------|----------|----------------------------|
| POST   | `/api/transaction/approve`| EMPLOYEE | Approve or deny a transaction |
| GET    | `/api/transaction/pending`| EMPLOYEE | List all pending transactions |

---

## 🔄 Transaction Status Values

- `APPROVED` – Automatically or manually approved
- `PENDING` – Awaiting employee approval (for large transactions)
- `DENIED` – Denied by employee

---

## 🛡️ Security

- JWT is used for authentication.
- Role-based authorization handled via `SecurityFilterChain`
- `/api/auth/**` is publicly accessible
- All other endpoints require authentication

---

## 🔍 Swagger Customization

- Descriptions, request/response samples, and role explanations are added for better API documentation.
- JWT Bearer support is integrated into Swagger for testing secure endpoints easily.

---

## ❗ Error Handling

- Global exception handler (`@RestControllerAdvice`) manages business logic exceptions (`WalletOperationException`) and returns HTTP 400 with message.

---

## 🗃️ Lazy vs Eager Loading

All entity relationships are reviewed:
- **Lazy loading** is used where recursive data (e.g. `User → Customer → User`) would cause infinite loops or bloated responses.
- DTOs are used for requests to avoid such issues.

---

## 📄 Sample Swagger Request Payloads

### WalletRequest

```json
{
  "walletName": "Main Wallet",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true
}
```

### TransactionRequest

```json
{
  "walletId": 1,
  "amount": 1500.00,
  "oppositePartyType": "IBAN",
  "oppositeParty": "TR0000000000000000"
}
```

### TransactionApprovalRequest

```json
{
  "transactionId": 5,
  "status": "APPROVED"
}
```

---

## 📬 Contact

For any questions or issues, feel free to reach out or raise an issue in the repository.