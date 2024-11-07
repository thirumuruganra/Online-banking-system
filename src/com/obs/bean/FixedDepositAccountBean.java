package com.obs.bean;

import java.util.Date;

public class FixedDepositAccountBean {
    private int fdAccountNo;
    private int customerACno;
    private double amount;
    private double interestRate;
    private Date startDate;
    private Date maturityDate;
    private String status;

    public FixedDepositAccountBean() {
        super();
    }

    public FixedDepositAccountBean(int fdAccountNo, int customerACno, double amount, double interestRate, Date startDate, Date maturityDate, String status) {
        super();
        this.fdAccountNo = fdAccountNo;
        this.customerACno = customerACno;
        this.amount = amount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.status = status;
    }

    public int getFdAccountNo() {
        return fdAccountNo;
    }

    public int getCustomerACno() {
        return customerACno;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setFdAccountNo(int fdAccountNo) {
        this.fdAccountNo = fdAccountNo;
    }

    public void setCustomerACno(int customerACno) {
        this.customerACno = customerACno;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}