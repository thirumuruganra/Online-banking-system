# Online Banking System

The Online Banking System is a web-based application that simulates essential banking services, enabling users to securely manage their bank accounts, check balances, transfer funds, and perform other transactions. This project can be a great learning resource for understanding how to build and structure a banking application with a secure and user-friendly interface.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)

## Features

### User Authentication
- Register new users
- Log in securely using user-set password
- Reset password
- Create new bank accounts for existing user
- Edit existing accounts
- Choose between savings and current account
- Create accounts in multiple branches
- Set overdraft limit and fee

### Account Management
- Check account details
- View balances
- Transaction history with type of transaction
- Manage loan and fixed deposit accounts
- Close accounts

### Fund Transfer
- Transfer money between accounts
- Withdraw money
- Deposit money
- Make loan payments
- Withdraw from fixed deposit accounts
- Withdraw money until overdraft limit

### Admin Dashboard
- Admin interface for managing user accounts
- Monitor transactions
- Check customer account status
- Check customer transactions
- Delete accounts
- Creation of loan accounts

## Technologies Used
- Frontend: Java Swing
- Language: Java
- Database: MySQL
- Database Connection: JDBC

## Installation

1. Clone the Repository
```bash
git clone https://github.com/thirumuruganra/Online-banking-system.git
```

2. Run the Application
```bash
cd Online-banking-system
java -jar BankingSystemProject.jar
```