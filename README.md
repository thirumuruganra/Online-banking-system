Online Banking System
=====================

Table of Contents
-----------------

* [Features](#features)
* [Technologies Used](#technologies-used)
* [Installation](#installation)
* [Usage](#usage)
* [System Architecture](#system-architecture)
* [Database Schema](#database-schema)

Features
--------

### User Authentication

* Register new users
* Log in securely using user-set password
* Reset password using Security Question
* Create new bank accounts for existing user
* Edit existing accounts
* Choose between savings and current account
* Set overdraft limit and fee

### Account Management

* Check account details
* View balances
* Transaction history with type of transaction
* Manage loan and fixed deposit accounts
* Close accounts

### Fund Transfer

* Transfer money between accounts
* Withdraw money
* Deposit money
* Make loan payments
* Withdraw from fixed deposit accounts
* Withdraw money until overdraft limit (for current accounts)

Technologies Used
-----------------

* Frontend: Java Swing
* Graphs and Charts: JFreeChart
* Language: Java
* Database: MySQL
* Database Connection: JDBC

Installation
------------

1. Clone the Repository
```bash
git clone https://github.com/thirumuruganra/Online-banking-system.git
```
2. Create the databases and the accountant's User ID and Password
```bash
cd Online-banking-system/src/com/obs/mainwork
javac Create_DB.java
java Create_DB
```

```bash
cd Online-banking-system/src/com/obs/mainwork
javac Create_Accountants.java
java Create_Accountants
```

3. Run the Application
```bash
cd Online-banking-system
java -jar BankingSystemProject.jar
```

Usage
-----

1. Launch the application and log in as an accountant or customer.
2. Perform various banking operations such as account management, fund transfer, and loan management.

System Architecture
-------------------

The system consists of the following components:

* Frontend: Java Swing-based GUI for user interaction
* Backend: Java-based business logic for banking operations
* Database: MySQL database for storing customer and account information

Database Schema
----------------

The database schema consists of the following tables:

* InfoAccountant
* InfoCustomer
* Account
* Transaction
* FixedDepositAccount
* LoanAccount
