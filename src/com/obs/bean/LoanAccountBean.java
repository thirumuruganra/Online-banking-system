package com.obs.bean;

public class LoanAccountBean {
    private int loanAccountId;
    private int customerACno;
    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    private double monthlyPayment;
    private double remainingLoan;
    private String loanStatus;

    public LoanAccountBean() {
        super();
    }

    public LoanAccountBean(int loanAccountId, int customerACno, double loanAmount, double interestRate, int loanTerm,
            double monthlyPayment, double remainingLoan, String loanStatus) {
        this.loanAccountId = loanAccountId;
        this.customerACno = customerACno;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.monthlyPayment = monthlyPayment;
        this.remainingLoan = remainingLoan;
        this.loanStatus = loanStatus;
    }

    public int getLoanAccountId() {
        return loanAccountId;
    }

    public void setLoanAccountId(int loanAccountId) {
        this.loanAccountId = loanAccountId;
    }

    public int getCustomerACno() {
        return customerACno;
    }

    public void setCustomerACno(int customerACno) {
        this.customerACno = customerACno;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double getRemainingLoan() {
        return remainingLoan;
    }

    public void setRemainingLoan(double remainingLoan) {
        this.remainingLoan = remainingLoan;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    @Override
    public String toString() {
        return "LoanAccount [loanAccountId=" + loanAccountId + ", customerACno=" + customerACno + ", loanAmount=" + loanAmount
                + ", interestRate=" + interestRate + ", loanTerm=" + loanTerm + ", monthlyPayment=" + monthlyPayment
                + ", remainingLoan=" + remainingLoan + ", loanStatus=" + loanStatus + "]";
    }
}
