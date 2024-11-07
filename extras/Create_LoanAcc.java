package com.obs.extras;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Create_LoanAcc {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement()) {
            // 6. Create Loan table
            String createLoanTable = "CREATE TABLE LoanAccount ( " +
                "loanAccountId INT PRIMARY KEY AUTO_INCREMENT, " +
                "customerACno BIGINT, " +
                "loanAmount DECIMAL(10, 2), " +
                "interestRate DECIMAL(5, 2), " +
                "loanTerm INT, " +
                "monthlyPayment DECIMAL(10, 2), " +
                "remainingLoan DECIMAL(10, 2), " +
                "loanStatus ENUM('Active', 'Paid', 'Defaulted'), " +
                "FOREIGN KEY (customerACno) REFERENCES Account(cACno) " +
                ");";
            stmt.executeUpdate(createLoanTable);
            System.out.println("Table Loan created.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}