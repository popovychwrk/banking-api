# Banking API

This project provides a simple RESTful API for a banking application using Spring Boot, Postgres, H2 database, and JPA. The application allows performing basic banking operations such as creating accounts, depositing funds, withdrawing funds, and transferring funds between accounts.

## Features

- **Account Management**: Create new accounts, retrieve account details, and list all accounts.
- **Account Transactions**: Deposit funds into an account, withdraw funds from an account, and transfer funds between accounts.

## Tech Stack

- **Java**: The primary programming language used.
- **Spring Boot**: Framework to ease the bootstrapping and development of new Spring Applications.
- **Postgres**: In-memory database for integration tests.
- **H2 Database**: In-memory database for integration tests.
- **JPA (Java Persistence API)**: Java API specification that describes the management of relational data in applications.

## Project Structure

- `src/main/java`: Java source files.
    - `domain`: Contains the entity classes.
    - `repositories`: Contains the Spring Data JPA repositories.
    - `services`: Contains business logic.
    - `controllers`: Contains REST API endpoints.
    - `mappers`: Contains classes to map entities to DTOs and vice versa.
    - `config`: Contains MapperConfig class.
- `src/test/java`: Contains the integration tests.
- `src/main/resources/application.properties`: Configuration properties to connect Postgres DB.
- `src/test/resources/application.properties`: Configuration properties to connect H2 DB for integration tests.

## Installation and Setup

1. **Clone the repository**

```bash
git clone https://github.com/popovychwrk/banking-api.git
cd banking-api
```
2. **Set up the database**
```bash
docker-compose up
```
3. **Build the project**

```bash
./mvnw clean install
```

4. **Run the application**

```bash
./mvnw spring-boot:run
```

The application will start running at <http://localhost:8080>.

## API Endpoints

1. `POST /api/accounts`
  - Description: Creates a new account.
  - Request Body: AccountDto
  - Response: AccountDto

2. `GET /api/accounts`
  - Description: Retrieves a paginated list of all accounts.
  - Request Parameters: `page`, `size`, `sort`
  - Response: Page<AccountDto>

3. `GET /api/accounts/{id}`
  - Description: Retrieves a specific account by its ID.
  - Path Variables: `id`
  - Response: AccountDto or 404 Not Found

4. `PUT /api/accounts/{id}`
  - Description: Updates an existing account by its ID.
  - Path Variables: `id`
  - Request Body: AccountDto
  - Response: AccountDto or 404 Not Found

5. `PATCH /api/accounts/{id}`
  - Description: Partially updates an existing account by its ID.
  - Path Variables: `id`
  - Request Body: AccountDto
  - Response: AccountDto or 404 Not Found

6. `DELETE /api/accounts/{id}`
  - Description: Deletes an account by its ID.
  - Path Variables: `id`
  - Response: 204 No Content or 404 Not Found

7. `POST /api/accounts/deposit/{id}`
  - Description: Deposits an amount to a specific account.
  - Path Variables: `id`
  - Request Parameters: `amount`
  - Response: AccountDto

8. `POST /api/accounts/withdraw/{id}`
  - Description: Withdraws an amount from a specific account.
  - Path Variables: `id`
  - Request Parameters: `amount`
  - Response: AccountDto

9. `POST /api/accounts/transfer/`
  - Description: Transfers an amount from one account to another.
  - Request Parameters: `fromAccountId`, `toAccountId`, `amount`
  - Response: AccountDto

## Testing

Run the following command to execute the integration tests and unit tests:
```bash
./mvnw test
```
