package com.obs.extras;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Create_FDAcc {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement()) {
            // 5. Create Fixed Deposit table
            String createFixedDepositTable = "CREATE TABLE FixedDepositAccount (" + 
                    "fdAccountNo INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customerACno BIGINT, " + 
                    "amount DECIMAL(10, 2), " +
                    "interestRate DECIMAL(5, 2), " +
                    "startDate DATE, " +
                    "maturityDate DATE, " +
                    "status ENUM('ACTIVE', 'MATURED', 'CLOSED'), " +
                    "FOREIGN KEY (customerACno) REFERENCES Account(cACno) " +
                    ");";
            stmt.executeUpdate(createFixedDepositTable);
            System.out.println("Table Fixed Deposit created.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}