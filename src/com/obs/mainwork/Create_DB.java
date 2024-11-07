package com.obs.mainwork;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Create_DB {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement()) {
            // 1. Create InfoAccountant table
            String createInfoAccountantTable = "CREATE TABLE InfoAccountant (" +
                    "ename VARCHAR(20), " +
                    "email VARCHAR(25), " +
                    "epass VARCHAR(20)" +
                    ");";
            stmt.executeUpdate(createInfoAccountantTable);
            System.out.println("Table InfoAccountant created.");

            // 2. Create InfoCustomer table
            String createInfoCustomerTable = "CREATE TABLE InfoCustomer (" +
                    "cname VARCHAR(20), " +
                    "cmail VARCHAR(25), " +
                    "cpass VARCHAR(20), " +
                    "cmob VARCHAR(10), " +
                    "cadd VARCHAR(20), " +
                    "cid INT PRIMARY KEY AUTO_INCREMENT" +
                    "schoolName VARCHAR(255)" +
                    ");";
            stmt.executeUpdate(createInfoCustomerTable);
            System.out.println("Table InfoCustomer created.");

            // 3. Create Account table
            String createAccountTable = "CREATE TABLE Account (" +
                    "cACno BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "cbal INT, " +
                    "cid INT, " +
                    "accountType VARCHAR(10), " +
                    "overdraftLimit DOUBLE, " +
                    "overdraftFee DOUBLE, " +
                    "FOREIGN KEY(cid) REFERENCES InfoCustomer(cid) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(createAccountTable);
            System.out.println("Table Account created.");

            // Set auto_increment starting value for Account table
            String alterAccountTable = "ALTER TABLE Account AUTO_INCREMENT = 2023001;";
            stmt.executeUpdate(alterAccountTable);
            System.out.println("Account table auto_increment set.");

            // 4. Create Transaction table
            String createTransactionTable = "CREATE TABLE Transaction (" +
                    "cACno BIGINT NOT NULL, " +
                    "deposit INT NOT NULL, " +
                    "withdraw INT NOT NULL, " +
                    "transaction_time TIMESTAMP, " +
                    "accountType VARCHAR(20), " +
                    "FOREIGN KEY(cACno) REFERENCES Account(cACno) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(createTransactionTable);
            System.out.println("Table Transaction created.");

            // 5. Create Fixed Deposit table
            String createFixedDepositTable = "CREATE TABLE FixedDepositAccount (" + 
                    "fdAccountNo INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customerACno BIGINT, " + 
                    "amount DECIMAL(10, 2), " +
                    "interestRate DECIMAL(10, 2), " +
                    "startDate DATE, " +
                    "maturityDate DATE, " +
                    "status ENUM('ACTIVE', 'MATURED', 'CLOSED'), " +
                    "FOREIGN KEY (customerACno) REFERENCES Account(cACno) " +
                    ");";
            stmt.executeUpdate(createFixedDepositTable);
            System.out.println("Table Fixed Deposit created.");

            // 6. Create Loan table
            String createLoanTable = "CREATE TABLE LoanAccount ( " +
                    "loanAccountId INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customerACno BIGINT, " +
                    "loanAmount DECIMAL(10, 2), " +
                    "interestRate DECIMAL(10, 2), " +
                    "loanTerm INT, " +
                    "monthlyPayment DECIMAL(10, 2), " +
                    "remainingLoan DECIMAL(10, 2), " +
                    "loanStatus ENUM('ACTIVE','CLOSED') " +
                    "FOREIGN KEY (customerACno) REFERENCES Account(cACno) " +
                    ");";
            stmt.executeUpdate(createLoanTable);
            System.out.println("Table Loan created.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
