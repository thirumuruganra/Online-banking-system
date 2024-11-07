package com.obs.dao;

import java.util.List;

import com.obs.bean.CustomerBean;
import com.obs.bean.FixedDepositAccountBean;
import com.obs.bean.LoanAccountBean;
import com.obs.bean.TransactionBean;
import com.obs.exception.CustomerException;

public interface CustomerDAO {
	public CustomerBean LoginCustomer(String username, String password, int accountno)throws CustomerException; 
	public double viewBalance(int cACno) throws CustomerException;
	public double Deposit(int cACno, double amount) throws CustomerException; 
	public double Withdraw(int cACno, double amount) throws CustomerException;
	public double Transfer(int cACno, double amount, int cACno2) throws CustomerException; 
	public List<TransactionBean> viewTransaction(int cACno) throws CustomerException;

	public List<FixedDepositAccountBean> viewFixedDepositAccounts(int customerACno) throws CustomerException;

	public List<LoanAccountBean> viewLoanAccounts(int customerACno) throws CustomerException;

	CustomerBean getCustomerForPasswordResetById(String email, int customerId) throws CustomerException;
	void updatePasswordById(int customerId, String newPassword) throws CustomerException;
}