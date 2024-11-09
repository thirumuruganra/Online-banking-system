package com.obs.dao;

import java.util.*;
import com.obs.bean.AccountantBean;
import com.obs.bean.CustomerBean;
import com.obs.bean.FixedDepositAccountBean;
import com.obs.bean.LoanAccountBean;
import com.obs.bean.TransactionBean;
import com.obs.exception.AccountException;
import com.obs.exception.AccountantException;
import com.obs.exception.CustomerException;

public interface AccountantDAO {
    public AccountantBean LoginAccountant(String username, String password) throws AccountantException;
    public int addCustomer(String cname, String cmail, String cpass, String cmob, String cadd, String schoolName) throws CustomerException;    
    public String addSavingsAccount(double cbal,int cid) throws AccountException;
    public String addCurrentAccount(double cbal, int cid, double overdraftFee, double overdraftLimit) throws AccountException;
    public String updateCustomer(int cACno,String cadd) throws CustomerException;
    public CustomerBean viewCustomer(String cACno) throws CustomerException;
    public int getCustomer(String cmail,String cmob) throws CustomerException;
    public List<CustomerBean> viewAllCustomers() throws CustomerException;
    public String getAccountType(int accountNo) throws CustomerException;
    public String deleteAccount(int cACno) throws CustomerException;    

    public int createFixedDepositAccount(int customerACno, double amount, int tenureInMonths) throws CustomerException;
    public FixedDepositAccountBean getFixedDepositAccountDetails(int fdAccountNo) throws CustomerException;
    public List<FixedDepositAccountBean> getAllFixedDepositAccounts(int customerACno) throws CustomerException;
    public void updateFixedDepositAmount(int fdAccountNo) throws CustomerException;
    public boolean closeFixedDepositAccount(int fdAccountNo) throws CustomerException;

    public int createLoanAccount(int customerACno, double amount, int tenureInMonths) throws CustomerException;
    public LoanAccountBean getLoanAccountDetails(int loanAccountNo) throws CustomerException;
    public List<LoanAccountBean> getAllLoanAccounts(int customerACno) throws CustomerException;
    public void updateLoanAmount(int loanAccountNo) throws CustomerException;
    public boolean closeLoanAccount(int loanAccountNo, boolean Override) throws CustomerException;

    public void calculateAndAddSavingsInterest(int accountNo) throws CustomerException;
    
    public int getCustomerIdByAccountNo(int accountNo) throws CustomerException;
    public int getAccountNumberByCustomerId(int customerId) throws CustomerException;
    public List<TransactionBean> getTransactionsBetweenDates(java.sql.Date startDate, java.sql.Date endDate) throws CustomerException;
}