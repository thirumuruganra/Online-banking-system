package com.obs.bean;

import java.sql.Timestamp;

public class TransactionBean {
	
	private int accountNo;
	private double deposit;
	private double withdraw;
	private String accountType;
	private Timestamp transaction_time;
	
	public TransactionBean(int accountNo, double deposit, double withdraw, String accountType, Timestamp transaction_time) {
		super();
		this.accountNo = accountNo;
		this.deposit = deposit;
		this.withdraw = withdraw;
		this.accountType = accountType;
		this.transaction_time=transaction_time;
	}

	public TransactionBean() {
		super();
	}

	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(double withdraw) {
		this.withdraw = withdraw;
	}
	
	public Timestamp getTransaction_time() {
		return transaction_time;
	}

	public void setTransaction_time(Timestamp transaction_time) {
		this.transaction_time = transaction_time;
	}

	public String getAccountType() {
		return accountType;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
}
