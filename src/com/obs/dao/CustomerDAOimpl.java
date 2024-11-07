package com.obs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.obs.bean.CustomerBean;
import com.obs.bean.FixedDepositAccountBean;
import com.obs.bean.LoanAccountBean;
import com.obs.bean.TransactionBean;
import com.obs.exception.CustomerException;
import com.obs.utility.DBUtil;

public class CustomerDAOimpl implements CustomerDAO {
	public CustomerBean LoginCustomer(String username, String password, int accountno) throws CustomerException {
		CustomerBean cus = null;

		try(Connection conn = DBUtil.provideConnection()) {
		PreparedStatement ps = conn.prepareStatement("select * from InfoCustomer i inner join Account a on i.cid=a.cid where cmail = ? AND cpass = ? AND cACno=?;" );
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setInt(3, accountno);
			
			ResultSet rs= ps.executeQuery();
			if (rs.next()) {
				int ac = rs.getInt("cACno");			
				String n = rs.getString("cname");
				int b = rs.getInt("cbal");
				String e = rs.getString("cmail");
				String p = rs.getString("cpass");
				String m = rs.getString("cmob");
				String ad = rs.getString("cadd");
				String sn = rs.getString("schoolName");
				cus = new CustomerBean(ac,n,b,e,p,m,ad,sn);		
			} 
			else {
				throw new CustomerException("Invalid Username or password....Try Again!");
			}
		} 
		catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}

		return cus;
	}

    public double viewBalance(int cACno) throws CustomerException {
        double b = -1; // Changed to double to handle overdraft
        try (Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("Select cbal, overdraftLimit from Account where cACno = ?");
            ps.setInt(1, cACno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                b = rs.getDouble("cbal");
                // Retrieve overdraft limit if available
            }
        } catch (SQLException e) {
            throw new CustomerException(e.getMessage());
        }
        return b;
    }
	
	public double Deposit(int cACno, double amount) throws CustomerException {
		double currentBalance = viewBalance(cACno);
		
		try(Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			try {
				// Update account balance
				PreparedStatement ps = conn.prepareStatement("UPDATE Account SET cbal = cbal + ? WHERE cACno = ?");
				ps.setDouble(1, amount);
				ps.setInt(2, cACno);
				
				// Record transaction
				PreparedStatement transPs = conn.prepareStatement("INSERT INTO transaction(cACno, deposit, withdraw, accountType, Transaction_time) VALUES(?, ?, 0, 'Deposit', CURRENT_TIMESTAMP)");
				transPs.setInt(1, cACno);
				transPs.setDouble(2, amount);
				
				int x = ps.executeUpdate();
				transPs.executeUpdate();
				
				conn.commit();
				if(x > 0) {
					return currentBalance + amount;
				}
			} catch(SQLException e) {
				conn.rollback();
				throw new CustomerException(e.getMessage());
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
		throw new CustomerException("Deposit failed");
	}
	
	public double Withdraw(int cACno, double amount) throws CustomerException {
		double currentBalance = viewBalance(cACno);
		
		try(Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			try {
				PreparedStatement ps = conn.prepareStatement("SELECT cbal, accountType, overdraftLimit, overdraftFee FROM Account WHERE cACno = ?");
				ps.setInt(1, cACno);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					double balance = rs.getDouble("cbal");
					double overdraftLimit = rs.getDouble("overdraftLimit");
					double overdraftFee = rs.getDouble("overdraftFee");
					
					double newBalance;
					double totalWithdrawal = amount;
					if(balance >= amount) {
						newBalance = balance - amount;
					} else if(balance + overdraftLimit >= amount) {
						double overdraftUsed = amount - balance;
						newBalance = -overdraftUsed;
						totalWithdrawal += overdraftFee;
					} else {
						throw new CustomerException("Amount exceeds available balance and overdraft limit");
					}
					
					PreparedStatement updatePs = conn.prepareStatement("UPDATE Account SET cbal = ? WHERE cACno = ?");
					updatePs.setDouble(1, newBalance);
					updatePs.setInt(2, cACno);
					
					// Record transaction
					PreparedStatement transPs = conn.prepareStatement("INSERT INTO transaction(cACno, deposit, withdraw, accountType, Transaction_time) VALUES(?, 0, ?, 'Withdrawal', CURRENT_TIMESTAMP)");
					transPs.setInt(1, cACno);
					transPs.setDouble(2, totalWithdrawal);
					
					updatePs.executeUpdate();
					transPs.executeUpdate();
					
					conn.commit();
					return newBalance;
				}
			} catch(SQLException e) {
				conn.rollback();
				throw new CustomerException(e.getMessage());
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
		throw new CustomerException("Withdrawal failed");
	}
	
	public double Transfer(int fromAccount, double amount, int toAccount) throws CustomerException {
		try(Connection conn = DBUtil.provideConnection()) {
			conn.setAutoCommit(false);
			try {
				// Get current balances
				PreparedStatement ps1 = conn.prepareStatement("SELECT cbal FROM Account WHERE cACno = ?");
				ps1.setInt(1, fromAccount);
				ResultSet rs1 = ps1.executeQuery();
				
				PreparedStatement ps2 = conn.prepareStatement("SELECT cbal FROM Account WHERE cACno = ?");
				ps2.setInt(1, toAccount);
				ResultSet rs2 = ps2.executeQuery();
				
				if(rs1.next() && rs2.next()) {
					double sourceBalance = rs1.getDouble("cbal");
					
					if(sourceBalance >= amount) {
						// Update source account
						PreparedStatement updateSource = conn.prepareStatement("UPDATE Account SET cbal = cbal - ? WHERE cACno = ?");
						updateSource.setDouble(1, amount);
						updateSource.setInt(2, fromAccount);
						updateSource.executeUpdate();
						
						// Update destination account
						PreparedStatement updateDest = conn.prepareStatement("UPDATE Account SET cbal = cbal + ? WHERE cACno = ?");
						updateDest.setDouble(1, amount);
						updateDest.setInt(2, toAccount);
						updateDest.executeUpdate();
						
						// Record withdrawal transaction
						PreparedStatement transPs = conn.prepareStatement("INSERT INTO transaction(cACno, deposit, withdraw, accountType, Transaction_time) VALUES(?, 0, ?, 'Transfer', CURRENT_TIMESTAMP)");
						transPs.setInt(1, fromAccount);
						transPs.setDouble(2, amount);
						transPs.executeUpdate();
						
						// Record deposit transaction
						PreparedStatement transPs2 = conn.prepareStatement("INSERT INTO transaction(cACno, deposit, withdraw, accountType, Transaction_time) VALUES(?, ?, 0, 'Transfer', CURRENT_TIMESTAMP)");
						transPs2.setInt(1, toAccount);
						transPs2.setDouble(2, amount);
						transPs2.executeUpdate();
						
						conn.commit();
						return sourceBalance - amount;
					} else {
						throw new CustomerException("Insufficient balance for transfer");
					}
				}
			} catch(SQLException e) {
				conn.rollback();
				throw new CustomerException(e.getMessage());
			}
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
		throw new CustomerException("Transfer failed");
	}
	
	private boolean checkAccount(int cACno) {
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps=conn.prepareStatement("select * from Account where cACno=?;");
			ps.setInt(1, cACno);
			ResultSet rs=ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public List<TransactionBean> viewTransaction(int cACno) throws CustomerException {
		List<TransactionBean> li=new ArrayList<>();
		try(Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps=conn.prepareStatement("select * from transaction where cACno=?;");
			ps.setInt(1, cACno);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int ac = rs.getInt("cACno");
				int dep = rs.getInt("deposit");
				int wid = rs.getInt("withdraw");
				String at = rs.getString("accountType");
				Timestamp tt = rs.getTimestamp("Transaction_time");
				
				li.add(new TransactionBean(ac,dep,wid,at,tt));
			}
			if (li.size() == 0) {
				throw new CustomerException("No Transaction Found");
			}
		} 
		catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
		
		return li;
	}

	public List<FixedDepositAccountBean> viewFixedDepositAccounts(int customerACno) throws CustomerException {
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
	
	public List<LoanAccountBean> viewLoanAccounts(int customerACno) throws CustomerException {
		List<LoanAccountBean> loanAccounts = new ArrayList<>();
		try (Connection conn = DBUtil.provideConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM LoanAccount WHERE customerACno = ?");
			ps.setInt(1, customerACno);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				LoanAccountBean loan = new LoanAccountBean(
					rs.getInt("loanAccountId"),
					rs.getInt("customerACno"),
					rs.getDouble("loanAmount"),
					rs.getDouble("interestRate"),
					rs.getInt("loanTerm"),
					rs.getDouble("monthlyPayment"),
					rs.getDouble("remainingLoan"),
					rs.getString("loanStatus")
				);
				loanAccounts.add(loan);
			}

			if (loanAccounts.isEmpty()) {
				throw new CustomerException("No Loan Accounts found for the customer");
			}

			return loanAccounts;
		} catch (SQLException e) {
			throw new CustomerException(e.getMessage());
		}
	}

	public CustomerBean getCustomerForPasswordResetById(String email, int customerId) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			String query = "SELECT * FROM InfoCustomer WHERE cmail = ? AND cid = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, email);
			ps.setInt(2, customerId);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				CustomerBean customer = new CustomerBean();
				customer.setcACno(rs.getInt("cid"));
				customer.setCname(rs.getString("cname"));
				customer.setCmail(rs.getString("cmail"));
				customer.setCpass(rs.getString("cpass"));
				customer.setSchoolName(rs.getString("schoolName"));
				return customer;
			}
			throw new CustomerException("No customer found with given email and customer ID");
			
		} catch (SQLException e) {
			throw new CustomerException("Error retrieving customer details: " + e.getMessage());
		}
	}
	
	public void updatePasswordById(int customerId, String newPassword) throws CustomerException {
		try (Connection conn = DBUtil.provideConnection()) {
			String updateQuery = "UPDATE infocustomer SET cpass = ? WHERE cid = ?";
			
			PreparedStatement ps = conn.prepareStatement(updateQuery);
			ps.setString(1, newPassword);
			ps.setInt(2, customerId);
			
			int rowsAffected = ps.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new CustomerException("No customer found with Customer ID: " + customerId);
			}
			
		} catch (SQLException e) {
			throw new CustomerException("Error updating password: " + e.getMessage());
		}
	}	
}
