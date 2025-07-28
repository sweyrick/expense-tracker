# ExpenseTracker API

A Kotlin-based REST API for personal expense and income tracking, built with Ktor framework and PostgreSQL database.

## 🚀 Features

- **User Management**: Registration and authentication with JWT tokens
- **Expense Tracking**: Create, read, update, and delete expenses with categories
- **Income Tracking**: Manage recurring and one-time income entries
- **Date Range Filtering**: Filter expenses and income by date ranges
- **Recurring Transactions**: Support for recurring expenses and income
- **RESTful API**: Clean REST endpoints for all operations
- **Database Migrations**: Automated schema management with Flyway
- **Docker Support**: Containerized deployment with Docker Compose

## 🛠️ Tech Stack

- **Backend**: Kotlin with Ktor framework
- **Database**: PostgreSQL with Exposed ORM
- **Migrations**: Flyway for database schema management
- **Authentication**: JWT tokens
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Gradle with Shadow JAR plugin

## 📋 Prerequisites

- Docker and Docker Compose
- Java 17+ (for local development)
- Gradle (for local development)

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd ExpenseTracker
   ```

2. **Start the application**
   ```bash
   docker-compose up --build
   ```

3. **Access the API**
   - The API will be available at `http://localhost:8081`
   - PostgreSQL database runs on port `5432`

### Local Development

1. **Set up PostgreSQL database**
   ```bash
   docker-compose up db
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew run
   ```

## 📚 API Documentation

### Authentication

#### Register User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123",
    "registrationCode": "WEYRICK2025"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Expenses

#### Create Expense
```bash
curl -X POST http://localhost:8081/expenses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 50.00,
    "category": "FOOD",
    "description": "Lunch",
    "isRecurring": false,
    "startDate": "2024-01-15"
  }'
```

#### Get User Expenses
```bash
curl -X GET http://localhost:8081/expenses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Update Expense
```bash
curl -X PUT http://localhost:8081/expenses/{expense_id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 60.00,
    "category": "FOOD",
    "description": "Updated lunch",
    "isRecurring": false,
    "startDate": "2024-01-15"
  }'
```

#### Delete Expense
```bash
curl -X DELETE http://localhost:8081/expenses/{expense_id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Income

#### Create Income
```bash
curl -X POST http://localhost:8081/incomes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 5000.00,
    "description": "Salary",
    "isRecurring": true,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  }'
```

#### Get User Income
```bash
curl -X GET http://localhost:8081/incomes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🗄️ Database Schema

### Users Table
- `id` (UUID, Primary Key)
- `username` (VARCHAR, Unique)
- `email` (VARCHAR, Unique)
- `password_hash` (VARCHAR)
- `created_at` (TIMESTAMP)

### Expenses Table
- `id` (UUID, Primary Key)
- `user_id` (UUID, Foreign Key to Users)
- `amount` (DECIMAL)
- `category` (VARCHAR)
- `description` (TEXT)
- `is_recurring` (BOOLEAN)
- `start_date` (DATE)
- `end_date` (DATE, Nullable)

### Incomes Table
- `id` (UUID, Primary Key)
- `user_id` (UUID, Foreign Key to Users)
- `amount` (DECIMAL)
- `description` (TEXT)
- `is_recurring` (BOOLEAN)
- `start_date` (DATE)
- `end_date` (DATE, Nullable)

## 🔧 Configuration

### Environment Variables

The application can be configured using environment variables or the `application.conf` file:

- `db.jdbcurl`: Database connection URL
- `db.user`: Database username
- `db.password`: Database password
- `jwt.secret`: JWT signing secret
- `jwt.issuer`: JWT issuer
- `jwt.audience`: JWT audience
- `jwt.expiresIn`: JWT expiration time

### Docker Configuration

The `docker-compose.yml` file includes:
- PostgreSQL database service
- Application service with proper networking
- Environment variable configuration
- Volume persistence for database data

## 🧪 Testing

Run the test suite:
```bash
./gradlew test
```

## 📦 Building

Create a fat JAR for deployment:
```bash
./gradlew shadowJar
```

The JAR file will be created at `build/libs/ExpenseTracker-all.jar`

## 🚀 Deployment

### Docker Deployment
```bash
docker-compose up -d
```

### Manual Deployment
1. Build the JAR: `./gradlew shadowJar`
2. Set up PostgreSQL database
3. Run: `java -jar build/libs/ExpenseTracker-all.jar`

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📞 Support

For issues and questions, please open an issue on GitHub. 