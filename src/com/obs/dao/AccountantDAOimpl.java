package com.obs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import com.obs.bean.AccountantBean;
import com.obs.bean.CustomerBean;
import com.obs.bean.FixedDepositAccountBean;
import com.obs.bean.LoanAccountBean;
import com.obs.exception.AccountException;
import com.obs.exception.AccountantException;
import com.obs.exception.CustomerException;
import com.obs.utility.DBUtil;

public class AccountantDAOimpl implements AccountantDAO {
	private final Map<String, InterestCalculator> interestCalculators;
    
	public AccountantDAOimpl() {
		// Initialize all interest calculators
		interestCalculators = new HashMap<>();
		interestCalculators.put("SAVINGS", new SavingsInterestCalculator());
		interestCalculators.put("FIXED_DEPOSIT", new FixedDepositInterestCalculator());
		interestCalculators.put("LOAN", new LoanInterestCalculator());
	}
	
	// Generic interest calculation method
	private double calculateInterest(String accountType, double principal, int timeInMonths) {
		InterestCalculator calculator = interestCalculators.get(accountType);
		return calculator.calculateInterest(principal, timeInMonths);
	}

	public AccountantBean LoginAccountant(String username, String password) throws AccountantException {
		AccountantBean acc = null;
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("select * from InfoAccountant where email = ? AND epass = ?");			
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String n =rs.getString("ename");				
				String e = rs.getString("email");
				String p = rs.getString("epass");
				acc = new AccountantBean(n,e,p);	
			}
			else
				throw new AccountantException("Invalid Username/Password....Try Again! ");	
		} 
		catch (SQLException e) {
			throw new AccountantException(e.getMessage());
		}
		
		return acc;
	}
	
	public int addCustomer(String cname, String cmail, String cpass, String cmob, String cadd, String schoolName) throws CustomerException {
		int cid = -1;    
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("insert into InfoCustomer(cname,cmail,cpass,cmob,cadd,schoolName) values(?,?,?,?,?,?)");
			ps.setString(1,cname);
			ps.setString(2,cmail);
			ps.setString(3,cpass);
			ps.setString(4,cmob);
			ps.setString(5,cadd);
			ps.setString(6,schoolName);
		 
			int x = ps.executeUpdate();
		
			if (x > 0) {
				PreparedStatement ps2 = conn.prepareStatement("select cid from InfoCustomer where cmail=? AND cmob=?");
				ps2.setString(1, cmail);
				ps2.setString(2, cmob);
				ResultSet rs = ps2.executeQuery();
				
				if (rs.next()) {
					cid=rs.getInt("cid");
				}  
			}
			else
				System.out.println("Inserted data is not correct");
		}
		catch(SQLException e) {    
			e.printStackTrace();
		}
		
		return cid;
	}
	
	
	public String addSavingsAccount(double cbal, int cid) throws AccountException {
		String message = null;
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("insert into Account(cbal, cid, accountType) values(?,?,'Savings')", Statement.RETURN_GENERATED_KEYS);    
			ps.setDouble(1,cbal);
			ps.setInt(2,cid);
			
			int x = ps.executeUpdate();
			if (x > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					message = String.valueOf(rs.getInt(1));
				}
			}
		} catch(SQLException e) {
			throw new AccountException(e.getMessage());
		}
		return message;
	}
	
	public String addCurrentAccount(double cbal, int cid, double overdraftFee, double overdraftLimit) throws AccountException {
		String message = null;
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("insert into Account(cbal, cid, accountType, overdraftFee, overdraftLimit) values(?,?,'Current',?,?)", Statement.RETURN_GENERATED_KEYS);    
			ps.setDouble(1,cbal);
			ps.setInt(2,cid);
			ps.setDouble(3,overdraftFee);
			ps.setDouble(4,overdraftLimit);
			
			int x = ps.executeUpdate();
			if (x > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					message = String.valueOf(rs.getInt(1));
				}
			}
		} catch(SQLException e) {
			throw new AccountException(e.getMessage());
		}
		return message;
	}
	
	public String updateCustomer(int cACno,String cadd) throws CustomerException {
		String message = null;
		try(Connection conn = DBUtil.provideConnection()) {
		    PreparedStatement ps = conn.prepareStatement(" update infocustomer i inner join account a on i.cid=a.cid AND a.cACno=? set i.cadd=?;");
		 	ps.setInt(1, cACno);
		 	ps.setString(2,cadd);
			int x = ps.executeUpdate();
		 
		 	if (x > 0) {
				message = "Address updated sucessfully..!";
		 	}
			else {
				message = "Updation failed....Account Not Found";
		 	}
		}
		catch(SQLException e) {	
			e.printStackTrace();
			message=e.getMessage();
		}
		
		return message;
	}
		
	public String deleteAccount(int accountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			try {
				// First delete associated loan accounts
				PreparedStatement loanPs = conn.prepareStatement("DELETE FROM LoanAccount WHERE customerACno = ?");
				loanPs.setInt(1, accountNo);
				loanPs.executeUpdate();
	
				// Then delete associated fixed deposits
				PreparedStatement fdPs = conn.prepareStatement("DELETE FROM FixedDepositAccount WHERE customerACno = ?");
				fdPs.setInt(1, accountNo);
				fdPs.executeUpdate();
	
				// Delete transaction records
				PreparedStatement transPs = conn.prepareStatement("DELETE FROM Transaction WHERE cACno = ?");
				transPs.setInt(1, accountNo);
				transPs.executeUpdate();
	
				// Finally delete the account
				PreparedStatement accountPs = conn.prepareStatement("DELETE FROM Account WHERE cACno = ?");
				accountPs.setInt(1, accountNo);
				int x = accountPs.executeUpdate();
	
				conn.commit();
				return "Account deleted successfully";
	
			} catch (SQLException e) {
				conn.rollback();
				throw new CustomerException("Error while deleting account: " + e.getMessage());
			}
		} catch (SQLException e) {
			throw new CustomerException("Database connection error: " + e.getMessage());
		}
	}
	
	public CustomerBean viewCustomer(String cACno) throws CustomerException {
		CustomerBean cb = null;
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("select * from InfoCustomer i inner join Account a on a.cid=i.cid where cACno = ?");            
			ps.setString(1, cACno);
			ResultSet rs = ps.executeQuery();
	
			if (rs.next()) {
				int a = rs.getInt("cACno");
				String n = rs.getString("cname");
				double b = rs.getDouble("cbal");
				String e = rs.getString("cmail");
				String p = rs.getString("cpass");
				String m = rs.getString("cmob");
				String ad = rs.getString("cadd");
				String school = rs.getString("schoolName");
				cb = new CustomerBean(a,n,b,e,p,m,ad,school);
			}
			else
				throw new CustomerException("Invalid Account No ");
		} 
		catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	
		return cb;
	}
	
	public List<CustomerBean> viewAllCustomers() throws CustomerException {
		List<CustomerBean> customers = new ArrayList<>();
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("select * from InfoCustomer i inner join Account a on a.cid=i.cid");            
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int a = rs.getInt("cACno");
				String n = rs.getString("cname");
				double b = rs.getDouble("cbal");
				String e = rs.getString("cmail");
				String p = rs.getString("cpass");
				String m = rs.getString("cmob");
				String ad = rs.getString("cadd");
				String school = rs.getString("schoolName");
				CustomerBean cb = new CustomerBean(a,n,b,e,p,m,ad,school);
				customers.add(cb);
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
		return customers;
	}
	
	public String getAccountType(int accountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT accountType FROM Account WHERE cACno = ?");
			ps.setInt(1, accountNo);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getString("accountType");
			}
			
			throw new CustomerException("Account not found");
			
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public int getCustomer(String cmail, String cmob) throws CustomerException {
		int cid = -1;
		try(Connection conn = DBUtil.provideConnection()){
			PreparedStatement ps2 = conn.prepareStatement("select cid from InfoCustomer where cmail=? AND cmob=?");
				ps2.setString(1, cmail);
				ps2.setString(2, cmob);
				ResultSet rs = ps2.executeQuery();
				if (rs.next()) {
					cid=rs.getInt("cid");
				}
		}
		catch(SQLException e) {
			throw new CustomerException("Invalid Account No.");
		}

		return cid;
	}

	public int createFixedDepositAccount(int customerACno, double amount, int tenureInMonths) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement checkPs = conn.prepareStatement("SELECT cid FROM Account WHERE cACno = ?");
			checkPs.setInt(1, customerACno);
			ResultSet checkRs = checkPs.executeQuery();
			if (!checkRs.next()) {
				throw new CustomerException("Customer account not found");
			}
	
			Date startDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, tenureInMonths);
			Date maturityDate = calendar.getTime();
			double interestRate = calculateInterest("FIXED_DEPOSIT", amount, tenureInMonths);
	
			PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO FixedDepositAccount (customerACno, amount, interestRate, startDate, maturityDate, status) VALUES (?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
			);
			ps.setInt(1, customerACno);
			ps.setDouble(2, amount);
			ps.setDouble(3, interestRate);
			ps.setDate(4, new java.sql.Date(startDate.getTime()));
			ps.setDate(5, new java.sql.Date(maturityDate.getTime()));
			ps.setString(6, "ACTIVE");
	
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				throw new CustomerException("Creating fixed deposit account failed, no rows affected.");
			}
	
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new CustomerException("Creating fixed deposit account failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			throw new CustomerException("Error creating fixed deposit account: " + e.getMessage());
		}
	}
	
	public FixedDepositAccountBean getFixedDepositAccountDetails(int fdAccountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM FixedDepositAccount WHERE fdAccountNo = ?");
			ps.setInt(1, fdAccountNo);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				FixedDepositAccountBean fdAccount = new FixedDepositAccountBean();
				fdAccount.setFdAccountNo(rs.getInt("fdAccountNo"));
				fdAccount.setCustomerACno(rs.getInt("customerACno"));
				fdAccount.setAmount(rs.getDouble("amount"));
				fdAccount.setInterestRate(rs.getDouble("interestRate"));
				fdAccount.setStartDate(rs.getDate("startDate"));
				fdAccount.setMaturityDate(rs.getDate("maturityDate"));
				fdAccount.setStatus(rs.getString("status"));
				return fdAccount;
			} else {
				throw new CustomerException("Fixed Deposit Account not found");
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}

	public List<FixedDepositAccountBean> getAllFixedDepositAccounts(int customerACno) throws CustomerException {
		List<FixedDepositAccountBean> fdAccounts = new ArrayList<>();
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM FixedDepositAccount WHERE customerACno = ?");
			ps.setInt(1, customerACno);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				FixedDepositAccountBean fdAccount = new FixedDepositAccountBean();
				fdAccount.setFdAccountNo(rs.getInt("fdAccountNo"));
				fdAccount.setCustomerACno(rs.getInt("customerACno"));
				fdAccount.setAmount(rs.getDouble("amount"));
				fdAccount.setInterestRate(rs.getDouble("interestRate"));
				fdAccount.setStartDate(rs.getDate("startDate"));
				fdAccount.setMaturityDate(rs.getDate("maturityDate"));
				fdAccount.setStatus(rs.getString("status"));
				fdAccounts.add(fdAccount);
			}

			if (fdAccounts.isEmpty()) {
				throw new CustomerException("No Fixed Deposit Accounts found for the customer");
			}

			return fdAccounts;
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}

	public void updateFixedDepositAmount(int fdAccountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM FixedDepositAccount WHERE fdAccountNo = ?");
			ps.setInt(1, fdAccountNo);
			ResultSet rs = ps.executeQuery();
	
			if (rs.next()) {
				double principal = rs.getDouble("amount");
				double rate = rs.getDouble("interestRate");
				Date startDate = rs.getDate("startDate");
				Date maturityDate = rs.getDate("maturityDate");
	
				long diffInMillies = Math.abs(maturityDate.getTime() - startDate.getTime());
				long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
				int tenureInYears = (int) (diff / 365);
	
				double updatedAmount = principal * Math.pow((1 + rate/100), tenureInYears);
	
				PreparedStatement updatePs = conn.prepareStatement("UPDATE FixedDepositAccount SET amount = ? WHERE fdAccountNo = ?");
				updatePs.setDouble(1, updatedAmount);
				updatePs.setInt(2, fdAccountNo);
				updatePs.executeUpdate();
			} else {
				throw new CustomerException("Fixed Deposit Account not found");
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public boolean closeFixedDepositAccount(int fdAccountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
	
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM FixedDepositAccount WHERE fdAccountNo = ? AND status = 'ACTIVE'");
			ps.setInt(1, fdAccountNo);
			ResultSet rs = ps.executeQuery();
	
			if (rs.next()) {
				int customerACno = rs.getInt("customerACno");
				double principal = rs.getDouble("amount");
				double rate = rs.getDouble("interestRate");
				Date startDate = rs.getDate("startDate");
				Date maturityDate = rs.getDate("maturityDate");
	
				long diffInMillies = Math.abs(maturityDate.getTime() - startDate.getTime());
				long months = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 30;
				
				double finalAmount = principal * (1 + (rate/100) * (months/12.0));
	
				PreparedStatement updateFdPs = conn.prepareStatement("UPDATE FixedDepositAccount SET status = 'CLOSED' WHERE fdAccountNo = ?");
				updateFdPs.setInt(1, fdAccountNo);
				updateFdPs.executeUpdate();
	
				PreparedStatement updateAccountPs = conn.prepareStatement("UPDATE Account SET cbal = cbal + ? WHERE cACno = ?");
				updateAccountPs.setDouble(1, finalAmount);
				updateAccountPs.setInt(2, customerACno);
				updateAccountPs.executeUpdate();
	
				PreparedStatement transactionPs = conn.prepareStatement("INSERT INTO Transaction(cACno, deposit, withdraw, transaction_time, accountType) VALUES(?, ?, 0, NOW(), 'FD Closure')");
				transactionPs.setInt(1, customerACno);
				transactionPs.setDouble(2, finalAmount);
				transactionPs.executeUpdate();
	
				conn.commit();
				return true;
			} else {
				throw new CustomerException("Failed to close Fixed Deposit Account. Account may not exist or is already closed.");
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public int createLoanAccount(int customerACno, double amount, int tenureInMonths) throws CustomerException {
		int loanId = 0;
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement checkLoansPs = conn.prepareStatement(
				"SELECT SUM(loanAmount) as totalLoans FROM LoanAccount WHERE customerACno = ? AND loanStatus = 'Active'"
			);
			checkLoansPs.setInt(1, customerACno);
			ResultSet loansRs = checkLoansPs.executeQuery();
			
			double existingLoans = 0;
			if (loansRs.next()) {
				existingLoans = loansRs.getDouble("totalLoans");
			}
			
			if (existingLoans + amount > 5000000) {
				throw new CustomerException("Loan limit exceeded. Maximum total loan amount allowed is 50,00,000");
			}
	
			double interestRate = calculateInterest("LOAN", amount, tenureInMonths);
			double monthlyPayment = (amount * interestRate/1200) / (1 - Math.pow(1 + interestRate/1200, -tenureInMonths));
			
			PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO LoanAccount (customerACno, loanAmount, interestRate, loanTerm, monthlyPayment, remainingLoan, loanStatus) VALUES (?, ?, ?, ?, ?, ?, 'Active')",
				Statement.RETURN_GENERATED_KEYS
			);
			
			ps.setInt(1, customerACno);
			ps.setDouble(2, amount);
			ps.setDouble(3, interestRate);
			ps.setInt(4, tenureInMonths);
			ps.setDouble(5, monthlyPayment);
			ps.setDouble(6, amount);
			
			int affectedRows = ps.executeUpdate();
			
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if (generatedKeys.next()) {
				loanId = generatedKeys.getInt(1);
			}
			
			if (loanId > 0) {
				return loanId;
			}
			throw new CustomerException("Creating loan account failed");
			
		} catch (SQLException e) {
			throw new CustomerException("Error creating loan account: " + e.getMessage());
		}
	}
			
	public LoanAccountBean getLoanAccountDetails(int loanAccountId) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM LoanAccount WHERE loanAccountId = ?");
			ps.setInt(1, loanAccountId);
			ResultSet rs = ps.executeQuery();
	
			if (rs.next()) {
				return new LoanAccountBean(
					rs.getInt("loanAccountId"),
					rs.getInt("customerACno"),
					rs.getDouble("loanAmount"),
					rs.getDouble("interestRate"),
					rs.getInt("loanTerm"),
					rs.getDouble("monthlyPayment"),
					rs.getDouble("remainingLoan"),
					rs.getString("loanStatus")
				);
			}
			throw new CustomerException("Loan Account not found");
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public List<LoanAccountBean> getAllLoanAccounts(int customerACno) throws CustomerException {
		List<LoanAccountBean> loanAccounts = new ArrayList<>();
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM LoanAccount WHERE customerACno = ?");
			ps.setInt(1, customerACno);
			ResultSet rs = ps.executeQuery();
	
			while (rs.next()) {
				loanAccounts.add(new LoanAccountBean(
					rs.getInt("loanAccountId"),
					rs.getInt("customerACno"),
					rs.getDouble("loanAmount"),
					rs.getDouble("interestRate"),
					rs.getInt("loanTerm"),
					rs.getDouble("monthlyPayment"),
					rs.getDouble("remainingLoan"),
					rs.getString("loanStatus")
				));
			}
			return loanAccounts;
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public void updateLoanAmount(int loanAccountId) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			
			// Get loan details first
			PreparedStatement getLoanPs = conn.prepareStatement(
				"SELECT customerACno, monthlyPayment FROM LoanAccount WHERE loanAccountId = ? AND loanStatus = 'Active'"
			);
			getLoanPs.setInt(1, loanAccountId);
			ResultSet loanRs = getLoanPs.executeQuery();
			
			if (loanRs.next()) {
				int customerACno = loanRs.getInt("customerACno");
				double monthlyPayment = loanRs.getDouble("monthlyPayment");
				
				// Update loan remaining amount
				PreparedStatement updateLoanPs = conn.prepareStatement(
					"UPDATE LoanAccount SET remainingLoan = remainingLoan - monthlyPayment WHERE loanAccountId = ? AND loanStatus = 'Active'"
				);
				updateLoanPs.setInt(1, loanAccountId);
				updateLoanPs.executeUpdate();
				
				// Update account balance
				PreparedStatement updateBalancePs = conn.prepareStatement(
					"UPDATE Account SET cbal = cbal - ? WHERE cACno = ?"
				);
				updateBalancePs.setDouble(1, monthlyPayment);
				updateBalancePs.setInt(2, customerACno);
				updateBalancePs.executeUpdate();
				
				// Add transaction record
				PreparedStatement addTransactionPs = conn.prepareStatement(
					"INSERT INTO transaction(cACno, deposit, withdraw, transaction_time, accountType) VALUES(?, 0, ?, NOW(), 'Loan Payment')"
				);
				addTransactionPs.setInt(1, customerACno);
				addTransactionPs.setDouble(2, monthlyPayment);
				addTransactionPs.executeUpdate();
				
				conn.commit();
			} else {
				throw new CustomerException("Loan account not found or inactive");
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
		
	public boolean closeLoanAccount(int loanAccountId, boolean override) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			
			PreparedStatement checkPs = conn.prepareStatement(
				"SELECT remainingLoan FROM LoanAccount WHERE loanAccountId = ? AND loanStatus = 'ACTIVE'"
			);
			checkPs.setInt(1, loanAccountId);
			ResultSet rs = checkPs.executeQuery();
			
			if (rs.next()) {
				if (rs.getDouble("remainingLoan") <= 0 || override) {
					PreparedStatement updatePs = conn.prepareStatement(
						"UPDATE LoanAccount SET loanStatus = 'CLOSED' WHERE loanAccountId = ?"
					);
					updatePs.setInt(1, loanAccountId);
					updatePs.executeUpdate();
					
					conn.commit();
					return true;
				}
			}
			return false;
		} catch(SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
	
	public void calculateAndAddSavingsInterest(int accountNo) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			
			PreparedStatement ps = conn.prepareStatement(
				"SELECT cbal FROM Account WHERE cACno = ? AND accountType = 'Savings'"
			);
			ps.setInt(1, accountNo);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				double balance = rs.getDouble("cbal");
				double interest = calculateInterest("SAVINGS", balance, 1); // Monthly interest
				
				PreparedStatement updatePs = conn.prepareStatement(
					"UPDATE Account SET cbal = cbal + ? WHERE cACno = ?"
				);
				updatePs.setDouble(1, interest);
				updatePs.setInt(2, accountNo);
				updatePs.executeUpdate();
				
				PreparedStatement transactionPs = conn.prepareStatement(
					"INSERT INTO Transaction(cACno, deposit, withdraw, transaction_time, accountType) VALUES(?, ?, 0, NOW(), 'Interest Credit')"
				);
				transactionPs.setInt(1, accountNo);
				transactionPs.setDouble(2, interest);
				transactionPs.executeUpdate();
				
				conn.commit();
			} else {
				throw new CustomerException("Savings account not found");
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}
}