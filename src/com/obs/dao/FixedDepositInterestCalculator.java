package com.obs.dao;

public class FixedDepositInterestCalculator implements InterestCalculator {
    @Override
    public double calculateInterest(double principal, int timeInMonths) {
        if (timeInMonths <= 6) return 5.5;
        else if (timeInMonths > 6 && timeInMonths <= 12) return 6;
        else if (timeInMonths > 12 && timeInMonths <= 15) return 6.25;
        else if (timeInMonths > 15 && timeInMonths <= 18) return 6.5;
        else if (timeInMonths > 18 && timeInMonths <= 21) return 6.75;
        else return 7;
    }
}