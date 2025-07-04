# Expense Tracker API Documentation

## Overview
The Expense Tracker API is a RESTful service for managing family expenses and incomes. It provides authentication via JWT tokens and supports CRUD operations for expenses and incomes.

## Base URL
- **Development**: `http://localhost:8080`
- **Production**: `https://your-domain.com`

## Authentication
All endpoints (except registration and login) require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### Authentication

#### Register User
- **POST** `/auth/register`
- **Description**: Creates a new user account
- **Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "registrationCode": "string"
}
```
- **Response** (201 Created):
```json
{
  "token": "jwt-token",
  "user": {
    "id": "string",
    "username": "string",
    "email": "string"
  }
}
```

#### Login User
- **POST** `/auth/login`
- **Description**: Authenticates a user and returns JWT token
- **Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response** (200 OK):
```json
{
  "token": "jwt-token",
  "user": {
    "id": "string",
    "username": "string",
    "email": "string"
  }
}
```

### Expenses

#### Get Expense Categories
- **GET** `/expenses/categories`
- **Description**: Returns all available expense categories
- **Response** (200 OK):
```json
[
  "groceries",
  "utilities",
  "entertainment",
  "transportation",
  "healthcare",
  "education",
  "shopping",
  "dining",
  "travel",
  "other"
]
```

#### Get All Expenses
- **GET** `/expenses`
- **Description**: Returns all expenses for the family
- **Response** (200 OK):
```json
[
  {
    "id": "string",
    "userId": "string",
    "amount": 100.50,
    "category": "groceries",
    "description": "Weekly groceries",
    "isRecurring": false,
    "startDate": "2024-01-01",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

#### Create Expense
- **POST** `/expenses`
- **Description**: Creates a new expense record
- **Request Body**:
```json
{
  "amount": 100.50,
  "category": "groceries",
  "description": "Weekly groceries",
  "isRecurring": false,
  "startDate": "2024-01-01"
}
```
- **Response** (201 Created):
```json
{
  "id": "expense-id"
}
```

#### Update Expense
- **PUT** `/expenses/{id}`
- **Description**: Updates an existing expense record
- **Request Body**: Same as Create Expense
- **Response** (200 OK): Empty response

#### Delete Expense
- **DELETE** `/expenses/{id}`
- **Description**: Deletes an expense record
- **Response** (200 OK): Empty response

### Incomes

#### Get All Incomes
- **GET** `/incomes`
- **Description**: Returns all incomes for the family
- **Response** (200 OK):
```json
[
  {
    "id": "string",
    "userId": "string",
    "amount": 5000.00,
    "description": "Monthly salary",
    "isRecurring": true,
    "startDate": "2024-01-01",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

#### Create Income
- **POST** `/incomes`
- **Description**: Creates a new income record
- **Request Body**:
```json
{
  "amount": 5000.00,
  "description": "Monthly salary",
  "isRecurring": true,
  "startDate": "2024-01-01"
}
```
- **Response** (201 Created):
```json
{
  "id": "income-id"
}
```

#### Update Income
- **PUT** `/incomes/{id}`
- **Description**: Updates an existing income record
- **Request Body**: Same as Create Income
- **Response** (200 OK): Empty response

#### Delete Income
- **DELETE** `/incomes/{id}`
- **Description**: Deletes an income record
- **Response** (200 OK): Empty response

## Data Models

### ExpenseCategory Enum
```kotlin
enum class ExpenseCategory {
    GROCERIES,
    UTILITIES,
    ENTERTAINMENT,
    TRANSPORTATION,
    HEALTHCARE,
    EDUCATION,
    SHOPPING,
    DINING,
    TRAVEL,
    OTHER
}
```

### Error Responses
All endpoints may return the following error responses:

#### 400 Bad Request
```json
{
  "error": "Invalid request data"
}
```

#### 401 Unauthorized
```json
{
  "error": "Invalid token"
}
```

#### 403 Forbidden
```json
{
  "error": "Invalid registration code"
}
```

#### 404 Not Found
```json
{
  "error": "Resource not found"
}
```

#### 409 Conflict
```json
{
  "error": "Username already exists"
}
```

#### 500 Internal Server Error
```json
{
  "error": "Internal server error"
}
```

## Testing
You can import the `ExpenseTracker_API.postman_collection.json` file into Postman for interactive testing of all endpoints.

## Rate Limiting
Currently, there are no rate limits implemented. Consider implementing rate limiting for production use.

## Security Notes
- Passwords are hashed using SHA-256 (consider using bcrypt for production)
- JWT tokens expire after 24 hours
- Registration requires a valid registration code
- All sensitive endpoints require authentication 