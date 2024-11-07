package com.obs.mainwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Scanner;

import com.obs.dao.*;
import com.obs.bean.*;
import com.obs.exception.*;

public class MainGUI_Desktop extends JFrame {
    private JPanel mainPanel, contentPanel;
    private CardLayout cardLayout;
    private AccountantDAO accountantDAO;
    private CustomerDAO customerDAO;

    private JTextField customerUsernameField, customerAccountField, accountantUsernameField;
    private JPasswordField customerPasswordField, accountantPasswordField;

    private static final Font STANDARD_FONT = new Font("Bookman Old Style", Font.PLAIN, 14);
    private static final Color LOGIN_BUTTON_COLOR = new Color(40, 167, 69); // Bootstrap green

    public MainGUI_Desktop() {
        setTitle("Online Banking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        accountantDAO = new AccountantDAOimpl();
        customerDAO = new CustomerDAOimpl();

        mainPanel = new JPanel(new BorderLayout());
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel();
        JPanel accountantPanel = createAccountantPanel();
        JPanel customerPanel = createCustomerPanel();

        contentPanel.add(menuPanel, "menu");
        contentPanel.add(accountantPanel, "accountant");
        contentPanel.add(customerPanel, "customer");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        UIManager.put("OptionPane.messageFont", STANDARD_FONT);
        UIManager.put("OptionPane.buttonFont", STANDARD_FONT);
        UIManager.put("TextField.font", STANDARD_FONT);

        // Create a custom button panel factory for JOptionPane
        UIManager.put("OptionPane.buttonOrientation", 0);
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.okButtonPanelBackground", new Color(40, 167, 69));
        UIManager.put("OptionPane.buttonPanelBackground", Color.WHITE);

        // Create custom button UI only for JOptionPane buttons
        UIManager.put("OptionPane.buttonUIResource", new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                JButton button = (JButton) c;
                button.setBackground(new Color(40, 167, 69));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
            }
        });

        Timer timer = new Timer(24 * 60 * 60 * 1000, e -> {
            updateAllAccountTypes();
        });
        timer.start();
    }

    private void updateAllAccountTypes() {
        updateAllFixedDepositAccounts();
        updateAllSavingsAccounts();
        updateAllLoanAccounts();
    }

    private void updateAllFixedDepositAccounts() {
        try {
            List<CustomerBean> allCustomers = accountantDAO.viewAllCustomers();
            for (CustomerBean customer : allCustomers) {
                List<FixedDepositAccountBean> fdAccounts = accountantDAO.getAllFixedDepositAccounts(customer.getcACno());
                for (FixedDepositAccountBean fd : fdAccounts) {
                    accountantDAO.updateFixedDepositAmount(fd.getFdAccountNo());
                }
            }
        } catch (CustomerException e) {
            System.err.println("Error updating fixed deposit accounts: " + e.getMessage());
        }
    }
    
    private void updateAllSavingsAccounts() {
        try {
            List<CustomerBean> allCustomers = accountantDAO.viewAllCustomers();
            for (CustomerBean customer : allCustomers) {
                String accountType = accountantDAO.getAccountType(customer.getcACno());
                if ("Savings".equals(accountType)) {
                    accountantDAO.calculateAndAddSavingsInterest(customer.getcACno());
                }
            }
        } catch (CustomerException e) {
            System.err.println("Error updating savings accounts interest: " + e.getMessage());
        }
    }
    
    private void updateAllLoanAccounts() {
        try {
            List<CustomerBean> allCustomers = accountantDAO.viewAllCustomers();
            for (CustomerBean customer : allCustomers) {
                List<LoanAccountBean> loanAccounts = accountantDAO.getAllLoanAccounts(customer.getcACno());
                for (LoanAccountBean loan : loanAccounts) {
                    if ("Active".equals(loan.getLoanStatus())) {
                        accountantDAO.updateLoanAmount(loan.getLoanAccountId());
                    }
                }
            }
        } catch (CustomerException e) {
            System.err.println("Error updating loan accounts: " + e.getMessage());
        }
    }
    
    private void clearInputFields() {
        if (customerUsernameField != null) customerUsernameField.setText("");
        if (customerPasswordField != null) customerPasswordField.setText("");
        if (customerAccountField != null) customerAccountField.setText("");
        if (accountantUsernameField != null) accountantUsernameField.setText("");
        if (accountantPasswordField != null) accountantPasswordField.setText("");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
    
        JLabel welcomeLabel = new JLabel("Welcome to Online Banking System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
        panel.add(welcomeLabel, gbc);
    
        JButton accountantButton = new JButton("Accountant Portal");
        accountantButton.setFont(new Font("Bookman Old Style", Font.ITALIC, 16));
        JButton customerButton = new JButton("Customer Portal");
        customerButton.setFont(new Font("Bookman Old Style", Font.ITALIC, 16));
    
        panel.add(accountantButton, gbc);
        panel.add(customerButton, gbc);
    
        accountantButton.addActionListener(e -> cardLayout.show(contentPanel, "accountant"));
        customerButton.addActionListener(e -> cardLayout.show(contentPanel, "customer"));
    
        return panel;
    }
    
    private JPanel createAccountantPanel() {
        // Create main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create top panel with BorderLayout for back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
        // Create back button with styling
        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Arial", Font.ITALIC, 20));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));
        backButton.setBackground(new Color(0, 123, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
    
        // Create wrapper for back button
        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonWrapper.add(backButton);
        
        // Add back button to top panel
        topPanel.add(backButtonWrapper, BorderLayout.WEST);
    
        // Create center panel for login components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        // Create and style title
        JLabel titleLabel = new JLabel("Accountant Login");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
    
        // Create and style input fields
        accountantUsernameField = new JTextField(20);
        accountantPasswordField = new JPasswordField(20);
        accountantUsernameField.setFont(STANDARD_FONT);
        accountantPasswordField.setFont(STANDARD_FONT);
    
        // Create and style login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(STANDARD_FONT);
        loginButton.setBackground(LOGIN_BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
    
        // Add components to center panel
        centerPanel.add(titleLabel, gbc);
        centerPanel.add(new JLabel("Username:"), gbc);
        centerPanel.add(accountantUsernameField, gbc);
        centerPanel.add(new JLabel("Password:"), gbc);
        centerPanel.add(accountantPasswordField, gbc);
        centerPanel.add(loginButton, gbc);
    
        // Add action listeners
        loginButton.addActionListener(e -> loginAccountant());
        backButton.addActionListener(e -> {
            clearInputFields();
            cardLayout.show(contentPanel, "menu");
        });
    
        accountantPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginAccountant();
                }
            }
        });
    
        // Add panels to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
    
        return panel;
    }
    
    private JPanel createCustomerPanel() {
        // Create main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create top panel with BorderLayout for back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
        // Create back button with styling
        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Arial", Font.ITALIC, 20));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));
        backButton.setBackground(new Color(0, 123, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(STANDARD_FONT);
        forgotPasswordButton.setForeground(new Color(0, 123, 255));
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        forgotPasswordButton.addActionListener(e -> handleForgotPassword());
    
        // Create wrapper for back button
        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonWrapper.add(backButton);
        
        // Add back button to top panel
        topPanel.add(backButtonWrapper, BorderLayout.WEST);
    
        // Create center panel for login components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        // Create and style title
        JLabel titleLabel = new JLabel("Customer Login");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
    
        // Create and style input fields
        customerUsernameField = new JTextField(20);
        customerPasswordField = new JPasswordField(20);
        customerAccountField = new JTextField(20);
        customerUsernameField.setFont(STANDARD_FONT);
        customerPasswordField.setFont(STANDARD_FONT);
        customerAccountField.setFont(STANDARD_FONT);
    
        // Create and style login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(STANDARD_FONT);
        loginButton.setBackground(LOGIN_BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
    
        // Add components to center panel
        centerPanel.add(titleLabel, gbc);
        centerPanel.add(new JLabel("Username:"), gbc);
        centerPanel.add(customerUsernameField, gbc);
        centerPanel.add(new JLabel("Password:"), gbc);
        centerPanel.add(customerPasswordField, gbc);
        centerPanel.add(new JLabel("Account Number:"), gbc);
        centerPanel.add(customerAccountField, gbc);
        centerPanel.add(loginButton, gbc);
        centerPanel.add(forgotPasswordButton, gbc);
    
        // Add action listeners
        loginButton.addActionListener(e -> loginCustomer());
        backButton.addActionListener(e -> {
            clearInputFields();
            cardLayout.show(contentPanel, "menu");
        });
    
        customerAccountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginCustomer();
                }
            }
        });
    
        // Add panels to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
    
        return panel;
    }
    
    private void handleForgotPassword() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField emailField = new JTextField();
        JTextField customerIdField = new JTextField();
        JTextField schoolNameField = new JTextField();
    
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Customer ID:"));
        panel.add(customerIdField);
        panel.add(new JLabel("Security Question: What is your school name?"));
        panel.add(schoolNameField);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Password Recovery",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            try {
                String email = emailField.getText();
                int customerId = Integer.parseInt(customerIdField.getText());
                String schoolName = schoolNameField.getText();
    
                CustomerBean customer = customerDAO.getCustomerForPasswordResetById(email, customerId);
                
                if (customer != null && customer.getSchoolName().equalsIgnoreCase(schoolName)) {
                    JPanel passwordPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                    JPasswordField newPasswordField = new JPasswordField();
                    JPasswordField confirmPasswordField = new JPasswordField();
                    
                    passwordPanel.add(new JLabel("Enter New Password:"));
                    passwordPanel.add(newPasswordField);
                    passwordPanel.add(new JLabel("Confirm New Password:"));
                    passwordPanel.add(confirmPasswordField);
    
                    int passwordResult = JOptionPane.showConfirmDialog(null, passwordPanel, 
                        "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
                    if (passwordResult == JOptionPane.OK_OPTION) {
                        String newPassword = new String(newPasswordField.getPassword());
                        String confirmPassword = new String(confirmPasswordField.getPassword());
    
                        if (newPassword.equals(confirmPassword)) {
                            customerDAO.updatePasswordById(customerId, newPassword);
                            JOptionPane.showMessageDialog(this, 
                                "Password changed successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Passwords do not match!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid details or security answer", 
                        "Recovery Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
        
    private void loginAccountant() {
        try {
            AccountantBean accountant = accountantDAO.LoginAccountant(accountantUsernameField.getText(), new String(accountantPasswordField.getPassword()));
            JOptionPane.showMessageDialog(this, "Welcome " + accountant.getEname());
            showAccountantOperations();
        } catch (AccountantException ex) {
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage());
        }
    }

    private void loginCustomer() {
        try {
            CustomerBean customer = customerDAO.LoginCustomer(customerUsernameField.getText(), new String(customerPasswordField.getPassword()), Integer.parseInt(customerAccountField.getText()));
            JOptionPane.showMessageDialog(this, "Welcome " + customer.getCname());
            showCustomerOperations(Integer.parseInt(customerAccountField.getText()));
        } catch (CustomerException ex) {
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage());
        }
    }

    private void showAccountantOperations() {
        // Create main panel with BorderLayout
        JPanel operationsPanel = new JPanel(new BorderLayout());
        
        // Create panel for buttons with GridBagLayout for better organization
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Group related operations
        String[][] operationGroups = {
            // Account Management
            {"Add New Customer Account", "Edit Existing Account", "Remove Account"},
            // View Operations
            {"View Account Details", "View All Accounts", "View Customer Transactions"},
            // Special Account Operations
            {"Add New Account for Existing Customer", "Manage Fixed Deposit Accounts", "Manage Loan Accounts"}
        };
        
        // Add buttons in groups with proper spacing
        int gridy = 0;
        for (String[] group : operationGroups) {
            for (String operation : group) {
                JButton button = new JButton(operation);
                button.setPreferredSize(new Dimension(250, 40));
                button.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
                
                gbc.gridx = 0;
                gbc.gridy = gridy++;
                buttonPanel.add(button, gbc);
                button.addActionListener(e -> handleAccountantOperation(operation));
            }
            // Add spacing between groups
            gbc.gridy = gridy++;
            buttonPanel.add(Box.createVerticalStrut(20), gbc);
        }
        
        // Add logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(250, 40));
        logoutButton.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        gbc.gridy = gridy;
        buttonPanel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> handleAccountantOperation("Logout"));
        
        // Add title at top
        JLabel titleLabel = new JLabel("Accountant Operations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Add components to main panel
        operationsPanel.add(titleLabel, BorderLayout.NORTH);
        operationsPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Add panel to content panel and show it
        contentPanel.add(operationsPanel, "accountantOperations");
        cardLayout.show(contentPanel, "accountantOperations");
    }
    
    private void showCustomerOperations(int accountNo) {
        // Create main panel with BorderLayout
        JPanel operationsPanel = new JPanel(new BorderLayout());
        
        // Create panel for buttons with GridBagLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Group related operations
        String[][] operationGroups = {
            // Basic Account Operations
            {"View Balance", "Deposit Money", "Withdraw Money"},
            // Transfer and History
            {"Transfer Money", "View Transaction History"},
            // Special Accounts
            {"View Fixed Deposit Accounts", "View Loan Accounts"}
        };
        
        // Add buttons in groups with proper spacing
        int gridy = 0;
        for (String[] group : operationGroups) {
            for (String operation : group) {
                JButton button = new JButton(operation);
                button.setPreferredSize(new Dimension(250, 40));
                button.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
                
                gbc.gridx = 0;
                gbc.gridy = gridy++;
                buttonPanel.add(button, gbc);
                button.addActionListener(e -> handleCustomerOperation(operation, accountNo));
            }
            // Add spacing between groups
            gbc.gridy = gridy++;
            buttonPanel.add(Box.createVerticalStrut(20), gbc);
        }
        
        // Add logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(250, 40));
        logoutButton.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        gbc.gridy = gridy;
        buttonPanel.add(logoutButton, gbc);
        logoutButton.addActionListener(e -> handleCustomerOperation("Logout", accountNo));
        
        // Add title at top
        JLabel titleLabel = new JLabel("Customer Operations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Add components to main panel
        operationsPanel.add(titleLabel, BorderLayout.NORTH);
        operationsPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Add panel to content panel and show it
        contentPanel.add(operationsPanel, "customerOperations");
        cardLayout.show(contentPanel, "customerOperations");
    }
    
    private void showFixedDepositOperations() {
        JPanel operationsPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding

        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Arial", Font.ITALIC, 20));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10)); 
        backButton.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(contentPanel, "accountantOperations"));

        // Create a wrapper panel for the back button with its own padding
        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonWrapper.add(backButton);

        topPanel.add(backButtonWrapper, BorderLayout.WEST);
        
        // Title in center
        JLabel titleLabel = new JLabel("Fixed Deposit Operations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 55));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(topPanel.getWidth(), 60));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        String[][] operationGroups = {
            {"Create Fixed Deposit", "View All Fixed Deposits of a Single Customer"},
            {"View Fixed Deposit Account Details", "Close Fixed Deposit"}
        };
        
        int gridy = 0;
        for (String[] group : operationGroups) {
            for (String operation : group) {
                JButton button = new JButton(operation);
                button.setPreferredSize(new Dimension(350, 40));
                button.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
                
                gbc.gridx = 0;
                gbc.gridy = gridy++;
                buttonPanel.add(button, gbc);
                button.addActionListener(e -> handleFixedDepositOperation(operation));
            }
            gbc.gridy = gridy++;
            buttonPanel.add(Box.createVerticalStrut(20), gbc);
        }
        
        operationsPanel.add(topPanel, BorderLayout.NORTH);
        operationsPanel.add(buttonPanel, BorderLayout.CENTER);
        
        contentPanel.add(operationsPanel, "fdOperations");
        cardLayout.show(contentPanel, "fdOperations");
    }
    
    private void showLoanOperations() {
        JPanel operationsPanel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding

        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Arial", Font.ITALIC, 20));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10)); 
        backButton.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(contentPanel, "accountantOperations"));

        // Create a wrapper panel for the back button with its own padding
        JPanel backButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonWrapper.add(backButton);

        topPanel.add(backButtonWrapper, BorderLayout.WEST);
        
        // Title in center
        JLabel titleLabel = new JLabel("Loan Operations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 55));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(topPanel.getWidth(), 60));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        String[][] operationGroups = {
            {"Create New Loan", "View All Loans of a Customer"},
            {"View Loan Details", "Update Loan Payment"},
            {"Close Loan Account"}
        };
        
        int gridy = 0;
        for (String[] group : operationGroups) {
            for (String operation : group) {
                JButton button = new JButton(operation);
                button.setPreferredSize(new Dimension(350, 40));
                button.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
                
                gbc.gridx = 0;
                gbc.gridy = gridy++;
                buttonPanel.add(button, gbc);
                button.addActionListener(e -> handleLoanOperation(operation));
            }
            gbc.gridy = gridy++;
            buttonPanel.add(Box.createVerticalStrut(20), gbc);
        }
        
        operationsPanel.add(topPanel, BorderLayout.NORTH);
        operationsPanel.add(buttonPanel, BorderLayout.CENTER);
        
        contentPanel.add(operationsPanel, "loanOperations");
        cardLayout.show(contentPanel, "loanOperations");
    }
            
    private void handleFixedDepositOperation(String operation) {
        switch (operation) {
            case "Create Fixed Deposit":
                createFixedDeposit();
                break;
            case "View All Fixed Deposits of a Single Customer":
                viewFixedDeposits();
                break;
            case "View Fixed Deposit Account Details":
                viewFixedDepositDetails();
                break;
            case "Close Fixed Deposit":
                closeFixedDeposit();
                break;
            case "Back to Accountant Operations":
                cardLayout.show(contentPanel, "accountantOperations");
                break;
        }
    }

    private void handleLoanOperation(String operation) {
        switch (operation) {
            case "Create New Loan":
                createNewLoan();
                break;
            case "View All Loans of a Customer":
                viewAllLoans();
                break;
            case "View Loan Details":
                viewLoanDetails();
                break;
            case "Update Loan Payment":
                updateLoanPayment();
                break;
            case "Close Loan Account":
                closeLoan();
                break;
            case "Back to Accountant Operations":
                cardLayout.show(contentPanel, "accountantOperations");
                break;
        }
    }
    
    private void createFixedDeposit() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField customerACnoField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField tenureField = new JTextField();
    
        panel.add(new JLabel("Customer Account Number:"));
        panel.add(customerACnoField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Tenure (in months):"));
        panel.add(tenureField);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Create Fixed Deposit",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            try {
                int customerACno = Integer.parseInt(customerACnoField.getText());
                double amount = Double.parseDouble(amountField.getText());
                int tenureInMonths = Integer.parseInt(tenureField.getText());
                
                int fdAccountNo = accountantDAO.createFixedDepositAccount(customerACno, amount, tenureInMonths);
                JOptionPane.showMessageDialog(this, "Fixed Deposit created successfully! FD Account No: " + fdAccountNo);
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void viewFixedDeposits() {
        String customerACnoStr = JOptionPane.showInputDialog("Enter Account Number:");
        if (customerACnoStr != null && !customerACnoStr.isEmpty()) {
            try {
                int customerACno = Integer.parseInt(customerACnoStr);
                List<FixedDepositAccountBean> fixedDeposits = accountantDAO.getAllFixedDepositAccounts(customerACno);
                String[] columnNames = {"FD Account No", "Customer ACno", "Amount", "Interest Rate", "Start Date", "Maturity Date", "Status"};
                Object[][] data = new Object[fixedDeposits.size()][7];
    
                for (int i = 0; i < fixedDeposits.size(); i++) {
                    FixedDepositAccountBean fd = fixedDeposits.get(i);
                    data[i] = new Object[]{
                        fd.getFdAccountNo(),
                        fd.getCustomerACno(),
                        fd.getAmount(),
                        fd.getInterestRate(),
                        fd.getStartDate(),
                        fd.getMaturityDate(),
                        fd.getStatus()
                    };
                }
    
                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(800, 400));
    
                JOptionPane.showMessageDialog(this, scrollPane, "Fixed Deposits for the Customer " + customerACno, JOptionPane.PLAIN_MESSAGE);
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void viewFixedDepositDetails() {
        String fdAccountNoStr = JOptionPane.showInputDialog("Enter Fixed Deposit Account Number:");
        if (fdAccountNoStr != null && !fdAccountNoStr.isEmpty()) {
            try {
                int fdAccountNo = Integer.parseInt(fdAccountNoStr);
                FixedDepositAccountBean fd = accountantDAO.getFixedDepositAccountDetails(fdAccountNo);
                String details = "FD Account No: " + fd.getFdAccountNo() + "\n" +
                                 "Customer Account No: " + fd.getCustomerACno() + "\n" +
                                 "Amount: " + fd.getAmount() + "\n" +
                                 "Interest Rate: " + fd.getInterestRate() + "\n" +
                                 "Start Date: " + fd.getStartDate() + "\n" +
                                 "Maturity Date: " + fd.getMaturityDate() + "\n" +
                                 "Status: " + fd.getStatus();
                JOptionPane.showMessageDialog(this, details, "Fixed Deposit Details", JOptionPane.INFORMATION_MESSAGE);
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void closeFixedDeposit() {
        String fdAccountNoStr = JOptionPane.showInputDialog("Enter Fixed Deposit Account Number to close:");
        if (fdAccountNoStr != null && !fdAccountNoStr.isEmpty()) {
            try {
                int fdAccountNo = Integer.parseInt(fdAccountNoStr);
                boolean closed = accountantDAO.closeFixedDepositAccount(fdAccountNo);
                if (closed) {
                    JOptionPane.showMessageDialog(this, "Fixed Deposit Account closed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to close Fixed Deposit Account.");
                }
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void viewCustomerFixedDepositAccounts(int accountNo) {
        try {
            List<FixedDepositAccountBean> fdAccounts = customerDAO.viewFixedDepositAccounts(accountNo);
            String[] columnNames = {"FD Account No", "Amount", "Interest Rate", "Start Date", "Maturity Date", "Status"};
            Object[][] data = new Object[fdAccounts.size()][6];
    
            for (int i = 0; i < fdAccounts.size(); i++) {
                FixedDepositAccountBean fd = fdAccounts.get(i);
                data[i] = new Object[]{
                    fd.getFdAccountNo(),
                    fd.getAmount(),
                    fd.getInterestRate(),
                    fd.getStartDate(),
                    fd.getMaturityDate(),
                    fd.getStatus()
                };
            }
    
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
    
            JOptionPane.showMessageDialog(this, scrollPane, "Your Fixed Deposit Accounts", JOptionPane.PLAIN_MESSAGE);
        } catch (CustomerException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewCustomerLoanAccounts(int accountNo) {
        try {
            List<LoanAccountBean> loans = customerDAO.viewLoanAccounts(accountNo);
            String[] columnNames = {"Loan ID", "Amount", "Interest Rate", "Term", "Monthly Payment", "Remaining", "Status"};
            Object[][] data = new Object[loans.size()][7];
            
            for (int i = 0; i < loans.size(); i++) {
                LoanAccountBean loan = loans.get(i);
                data[i] = new Object[]{
                    loan.getLoanAccountId(),
                    loan.getLoanAmount(),
                    loan.getInterestRate(),
                    loan.getLoanTerm(),
                    loan.getMonthlyPayment(),
                    loan.getRemainingLoan(),
                    loan.getLoanStatus()
                };
            }
            
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Your Loan Accounts", JOptionPane.PLAIN_MESSAGE);
        } catch (CustomerException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void createNewLoan() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField customerACnoField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField tenureField = new JTextField();
    
        panel.add(new JLabel("Customer Account Number:"));
        panel.add(customerACnoField);
        panel.add(new JLabel("Loan Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Tenure (in months):"));
        panel.add(tenureField);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Create New Loan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            try {
                int customerACno = Integer.parseInt(customerACnoField.getText());
                double amount = Double.parseDouble(amountField.getText());
                int tenure = Integer.parseInt(tenureField.getText());
                
                // Store the returned loan ID
                int loanId = accountantDAO.createLoanAccount(customerACno, amount, tenure);
                
                // Only show success message if we get a valid loan ID
                if (loanId > 0) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Loan created successfully!\nLoan ID: %d\nAmount: %.2f\nTenure: %d months", 
                        loanId, amount, tenure));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values");
            } catch (CustomerException e) {
                // Show specific error message from the DAO layer
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
        
    private void viewAllLoans() {
        String customerACnoStr = JOptionPane.showInputDialog("Enter Account Number:");
        if (customerACnoStr != null && !customerACnoStr.isEmpty()) {
            try {
                int customerACno = Integer.parseInt(customerACnoStr);
                List<LoanAccountBean> loans = accountantDAO.getAllLoanAccounts(customerACno);
                
                String[] columnNames = {"Loan ID", "Amount", "Interest Rate", "Term", "Monthly Payment", "Remaining", "Status"};
                Object[][] data = new Object[loans.size()][7];
                
                for (int i = 0; i < loans.size(); i++) {
                    LoanAccountBean loan = loans.get(i);
                    data[i] = new Object[]{
                        loan.getLoanAccountId(),
                        loan.getLoanAmount(),
                        loan.getInterestRate(),
                        loan.getLoanTerm(),
                        loan.getMonthlyPayment(),
                        loan.getRemainingLoan(),
                        loan.getLoanStatus()
                    };
                }
                
                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(800, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Loans for Customer " + customerACno, JOptionPane.PLAIN_MESSAGE);
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void viewLoanDetails() {
        String loanIdStr = JOptionPane.showInputDialog("Enter Loan ID:");
        if (loanIdStr != null && !loanIdStr.isEmpty()) {
            try {
                int loanId = Integer.parseInt(loanIdStr);
                LoanAccountBean loan = accountantDAO.getLoanAccountDetails(loanId);
                String details = String.format("""
                    Loan ID: %d
                    Customer Account: %d
                    Loan Amount: %.2f
                    Interest Rate: %.2f%%
                    Loan Term: %d months
                    Monthly Payment: %.2f
                    Remaining Amount: %.2f
                    Status: %s
                    """,
                    loan.getLoanAccountId(), loan.getCustomerACno(),
                    loan.getLoanAmount(), loan.getInterestRate(),
                    loan.getLoanTerm(), loan.getMonthlyPayment(),
                    loan.getRemainingLoan(), loan.getLoanStatus());
                
                JOptionPane.showMessageDialog(this, details, "Loan Details", JOptionPane.INFORMATION_MESSAGE);
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void updateLoanPayment() {
        String loanIdStr = JOptionPane.showInputDialog("Enter Loan ID to process payment:");
        if (loanIdStr != null && !loanIdStr.isEmpty()) {
            try {
                int loanId = Integer.parseInt(loanIdStr);
                accountantDAO.updateLoanAmount(loanId);
                JOptionPane.showMessageDialog(this, "Loan payment processed successfully!");
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void closeLoan() {
        String loanIdStr = JOptionPane.showInputDialog("Enter Loan ID to close:");
        if (loanIdStr != null && !loanIdStr.isEmpty()) {
            try {
                int loanId = Integer.parseInt(loanIdStr);
                
                // Create option buttons
                Object[] options = {"Normal Close", "Override Close", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                    "Select closure type:\nNormal Close - Only if loan is fully paid\nOverride Close - Force close with remaining balance",
                    "Loan Closure Options",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice == 0) { // Normal close
                    boolean closed = accountantDAO.closeLoanAccount(loanId, false);
                    if (closed) {
                        JOptionPane.showMessageDialog(this, "Loan account closed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot close loan. Outstanding balance exists.");
                    }
                } else if (choice == 1) { // Override close
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "Are you sure you want to force close this loan? This action cannot be undone.",
                        "Confirm Override Close",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                        
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean closed = accountantDAO.closeLoanAccount(loanId, true);
                        if (closed) {
                            JOptionPane.showMessageDialog(this, "Loan account force closed successfully!");
                        }
                    }
                }
            } catch (CustomerException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
            
    private void handleAccountantOperation(String operation) {
        AccountantDAO a = new AccountantDAOimpl();
        Scanner sc = new Scanner(System.in);

        switch (operation) {
            case "Add New Customer Account":
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
                JTextField nameField = new JTextField();
                JTextField balanceField = new JTextField();
                JTextField emailField = new JTextField();
                JTextField passwordField = new JTextField();
                JTextField mobileField = new JTextField();
                JTextField addressField = new JTextField();
                JTextField schoolNameField = new JTextField();  
                JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
                
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("School Name:")); 
                panel.add(schoolNameField);  
                panel.add(new JLabel("Opening Balance:"));
                panel.add(balanceField);
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);
                panel.add(new JLabel("Mobile:"));
                panel.add(mobileField);
                panel.add(new JLabel("Address:"));
                panel.add(addressField);
                panel.add(new JLabel("Account Type:"));
                panel.add(accountTypeCombo);
            
                int result = JOptionPane.showConfirmDialog(null, panel, "Add New Customer Account",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    double balance = Double.parseDouble(balanceField.getText());
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    String mobile = mobileField.getText();
                    String address = addressField.getText();
                    String accountType = (String) accountTypeCombo.getSelectedItem();
                    String schoolName = (String) schoolNameField.getText();
            
                    try {
                        int customerId = a.addCustomer(name, email, password, mobile, address, schoolName);
                        String accountNumber = "";
                        
                        if ("Savings".equals(accountType)) {
                            accountNumber = a.addSavingsAccount(balance, customerId);
                        } else if ("Current".equals(accountType)) {
                            double overdraftLimit = Double.parseDouble(JOptionPane.showInputDialog("Enter Overdraft Limit:"));
                            double overdraftFee = Double.parseDouble(JOptionPane.showInputDialog("Enter Overdraft Fee:"));
                            accountNumber = a.addCurrentAccount(balance, customerId, overdraftFee, overdraftLimit);
                        }
            
                        String successMessage = String.format("""
                            Account created successfully!
                            Customer ID: %d
                            Account Number: %s
                            Name: %s
                            Account Type: %s
                            Opening Balance: %.2f
                            """, customerId, accountNumber, name, accountType, balance);
                            
                        JOptionPane.showMessageDialog(this, successMessage, "Account Created", JOptionPane.INFORMATION_MESSAGE);
                    } catch (CustomerException | AccountException e) {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                    }
                }
                break;
                
            case "Edit Existing Account":
                JPanel panel3 = new JPanel(new GridLayout(0, 2, 5, 5));
                JTextField accountNoField = new JTextField();
                JTextField newAddressField = new JTextField();
            
                panel3.add(new JLabel("Account Number:"));
                panel3.add(accountNoField);
                panel3.add(new JLabel("New Address:"));
                panel3.add(newAddressField);
            
                int result4 = JOptionPane.showConfirmDialog(null, panel3, "Edit Existing Account",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
                if (result4 == JOptionPane.OK_OPTION) {
                    int accountNo = Integer.parseInt(accountNoField.getText());
                    String newAddress = newAddressField.getText();
                    try {
                        String message = a.updateCustomer(accountNo, newAddress);
                        JOptionPane.showMessageDialog(this, message);
                    } catch (CustomerException e) {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                    }
                }
                break;
        
            case "Remove Account":
                int accountToRemove = Integer.parseInt(JOptionPane.showInputDialog("Enter Account No. to remove"));
                try {
                    String result2 = a.deleteAccount(accountToRemove);
                    JOptionPane.showMessageDialog(this, result2);
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
                break;

            case "View Account Details":
                String accountToView = JOptionPane.showInputDialog("Enter Customer Account No.");
                try {
                    CustomerBean customer = a.viewCustomer(accountToView);
                    if (customer != null) {
                        String accountType = a.getAccountType(customer.getcACno());
                        
                        String details = "Account No: " + customer.getcACno() + "\n" +
                                        "Account Type: " + accountType + "\n" +
                                        "Name: " + customer.getCname() + "\n" +
                                        "Balance: " + customer.getCbal() + "\n" +
                                        "Email: " + customer.getCmail() + "\n" +
                                        "Password: " + customer.getCpass() + "\n" +
                                        "Mobile: " + customer.getCmob() + "\n" +
                                        "Address: " + customer.getCadd();
                        JOptionPane.showMessageDialog(this, details);
                    } else {
                        JOptionPane.showMessageDialog(this, "Account does not Exist");
                    }
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
                break;
            

            case "View All Accounts":
                try {
                    List<CustomerBean> allCustomers = a.viewAllCustomers();
                    String[] columnNames = {"Account No", "Account Type", "Name", "Balance", "Email", "Mobile", "Address"};
                    Object[][] data = new Object[allCustomers.size()][7];
            
                    for (int i = 0; i < allCustomers.size(); i++) {
                        CustomerBean customer = allCustomers.get(i);
                        String accountType = a.getAccountType(customer.getcACno());
                        data[i] = new Object[]{
                            customer.getcACno(),
                            accountType,
                            customer.getCname(),
                            customer.getCbal(),
                            customer.getCmail(),
                            customer.getCmob(),
                            customer.getCadd()
                        };
                    }
            
                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(800, 400));
            
                    JOptionPane.showMessageDialog(this, scrollPane, "All Accounts", JOptionPane.PLAIN_MESSAGE);
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
                break;
            
            case "Add New Account for Existing Customer":
                JPanel panel2 = new JPanel(new GridLayout(0, 2, 5, 5));
                JTextField emailField2 = new JTextField();
                JTextField mobileField2 = new JTextField();
                JTextField balanceField2 = new JTextField();
                JComboBox<String> accountTypeCombo2 = new JComboBox<>(new String[]{"Savings", "Current"});
            
                panel2.add(new JLabel("Email:"));
                panel2.add(emailField2);
                panel2.add(new JLabel("Mobile:"));
                panel2.add(mobileField2);
                panel2.add(new JLabel("New Account Balance:"));
                panel2.add(balanceField2);
                panel2.add(new JLabel("Account Type:"));
                panel2.add(accountTypeCombo2);
            
                int result3 = JOptionPane.showConfirmDialog(null, panel2, "Add New Account for Existing Customer",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
                if (result3 == JOptionPane.OK_OPTION) {
                    String customerEmail = emailField2.getText();
                    String customerMobile = mobileField2.getText();
                    double newAccountBalance = Double.parseDouble(balanceField2.getText());
                    String selectedAccountType = (String) accountTypeCombo2.getSelectedItem();
            
                    try {
                        int customerId = a.getCustomer(customerEmail, customerMobile);
                        String newAccountNumber;
                        
                        if ("Savings".equals(selectedAccountType)) {
                            newAccountNumber = a.addSavingsAccount(newAccountBalance, customerId);
                        } else {
                            double overdraftLimit = Double.parseDouble(JOptionPane.showInputDialog("Enter Overdraft Limit:"));
                            double overdraftFee = Double.parseDouble(JOptionPane.showInputDialog("Enter Overdraft Fee:"));
                            newAccountNumber = a.addCurrentAccount(newAccountBalance, customerId, overdraftFee, overdraftLimit);
                        }
            
                        JOptionPane.showMessageDialog(this, 
                            String.format("New %s account added successfully!\nAccount Number: %s", 
                            selectedAccountType, newAccountNumber),
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (CustomerException | AccountException e) {
                        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                    }
                }
                break;
            
            case "View Customer Transactions":
                int transactionAccountNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account No. to view Transaction Records"));
                CustomerDAO cd = new CustomerDAOimpl();
                try {
                    List<TransactionBean> transactions = cd.viewTransaction(transactionAccountNo);
                    String[] columnNames = {"Account No.", "Amount", "Transaction Type", "Date and Time"};
                    Object[][] data = new Object[transactions.size()][4];
                    
                    for (int i = 0; i < transactions.size(); i++) {
                        TransactionBean v = transactions.get(i);
                        String amount = "";
                        if (v.getDeposit() != 0) {
                            amount = "+" + v.getDeposit();
                        } else if (v.getWithdraw() != 0) {
                            amount = "- " + v.getWithdraw();
                        }
                        data[i] = new Object[]{
                            v.getAccountNo(),
                            amount,
                            v.getAccountType(),
                            v.getTransaction_time()
                        };
                    }
                    
                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(600, 300));
                    
                    JOptionPane.showMessageDialog(this, scrollPane, "Transaction History for Account " + transactionAccountNo, JOptionPane.PLAIN_MESSAGE);
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
                break;
            
            
            case "Manage Fixed Deposit Accounts":
                showFixedDepositOperations();
                break;

            case "Manage Loan Accounts":
                showLoanOperations();
                break;

            case "Logout":
                clearInputFields();
                cardLayout.show(contentPanel, "menu");
                break; 
        }
    }

    private void handleCustomerOperation(String operation, int accountNo) {
        switch (operation) {
            case "View Balance":
                try {
                    double balance = customerDAO.viewBalance(accountNo);
                    JOptionPane.showMessageDialog(this, "Current Balance: " + balance);
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
                
            case "Deposit Money":
                String depositAmount = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
                try {
                    customerDAO.Deposit(accountNo, Double.parseDouble(depositAmount));
                    JOptionPane.showMessageDialog(this, "Deposit successful. New balance: " + customerDAO.viewBalance(accountNo));
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Withdraw Money":
                String withdrawAmount = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
                try {
                    customerDAO.Withdraw(accountNo, Double.parseDouble(withdrawAmount));
                    JOptionPane.showMessageDialog(this, "Withdrawal successful. New balance: " + customerDAO.viewBalance(accountNo));
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Transfer Money":
                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
                JTextField transferAmountField = new JTextField();
                JTextField targetAccountField = new JTextField();
            
                panel.add(new JLabel("Amount to transfer:"));
                panel.add(transferAmountField);
                panel.add(new JLabel("Target account number:"));
                panel.add(targetAccountField);
            
                int result = JOptionPane.showConfirmDialog(null, panel, "Transfer Money",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
                if (result == JOptionPane.OK_OPTION) {
                    String transferAmount = transferAmountField.getText();
                    String targetAccount = targetAccountField.getText();
                    try {
                        customerDAO.Transfer(accountNo, Double.parseDouble(transferAmount), Integer.parseInt(targetAccount));
                        JOptionPane.showMessageDialog(this, "Transfer successful. New balance: " + customerDAO.viewBalance(accountNo));
                    } catch (CustomerException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            
            case "View Transaction History":
                try {
                    List<TransactionBean> transactions = customerDAO.viewTransaction(accountNo);
                    String[] columnNames = {"Date and Time", "Amount", "Transaction Type"};
                    Object[][] data = new Object[transactions.size()][3];
                    
                    for (int i = 0; i < transactions.size(); i++) {
                        TransactionBean t = transactions.get(i);
                        String amount = "";
                        if (t.getDeposit() != 0) {
                            amount = "+" + t.getDeposit();
                        } else if (t.getWithdraw() != 0) {
                            amount = "-" + t.getWithdraw();
                        }
                        data[i] = new Object[]{
                            t.getTransaction_time(),
                            amount,
                            t.getAccountType()
                        };
                    }
                    
                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(600, 300));
                    
                    JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.PLAIN_MESSAGE);
                } catch (CustomerException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            

            case "View Fixed Deposit Accounts":
                viewCustomerFixedDepositAccounts(accountNo);
                break;

            case "View Loan Accounts":
                viewCustomerLoanAccounts(accountNo);
                break;
            
            case "Logout":
                clearInputFields();
                cardLayout.show(contentPanel, "menu");
                break;
            
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI_Desktop().setVisible(true);
        });
    }
}