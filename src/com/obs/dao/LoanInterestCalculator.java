package com.obs.dao;

public class LoanInterestCalculator implements InterestCalculator {
    @Override
    public double calculateInterest(double principal, int timeInMonths) {
        if (timeInMonths <= 12) return 15; 
        else if (timeInMonths <= 36) return 14; 
        else if (timeInMonths <= 60) return 13;  
        else return 12; 
    }
}