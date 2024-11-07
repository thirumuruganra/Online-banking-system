package com.obs.dao;

public class SavingsInterestCalculator implements InterestCalculator {
    @Override
    public double calculateInterest(double principal, int timeInMonths) {
        return principal * 0.04 * (timeInMonths/12.0); // 4% annual interest
    }
}