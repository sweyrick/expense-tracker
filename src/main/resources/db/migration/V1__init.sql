-- Initial schema creation
CREATE TABLE IF NOT EXISTS Users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS Incomes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES Users(id),
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    start_date DATE NOT NULL,
    end_date DATE NULL
);

CREATE TABLE IF NOT EXISTS Expenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES Users(id),
    amount DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    start_date DATE NOT NULL,
    end_date DATE NULL
); 