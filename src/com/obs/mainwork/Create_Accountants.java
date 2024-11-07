package com.obs.mainwork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Create_Accountants {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            String[] insertStatements = {
                "INSERT INTO InfoAccountant (ename, email, epass) VALUES ('Thiru', 'thiru@ssn.edu.in', 'root')",
                "INSERT INTO InfoAccountant (ename, email, epass) VALUES ('Varun', 'varun@ssn.edu.in', 'root')",
                "INSERT INTO InfoAccountant (ename, email, epass) VALUES ('Vishal', 'vishal@ssn.edu.in', 'root')",
                "INSERT INTO InfoAccountant (ename, email, epass) VALUES ('root', 'r', 'r')",
            };

            for (String sql : insertStatements) {
                stmt.executeUpdate(sql);
                System.out.println("Accountant inserted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
