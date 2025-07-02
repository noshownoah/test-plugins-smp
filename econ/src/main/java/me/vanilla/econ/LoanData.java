package me.vanilla.econ;

public class LoanData {
    private double principal;
    private double interestRate;
    private long timestamp;
    private int dueDays;
    private boolean repaid;

    public LoanData(double principal, double interestRate, long timestamp, int dueDays) {
        this.principal = principal;
        this.interestRate = interestRate;
        this.timestamp = timestamp;
        this.dueDays = dueDays;
        this.repaid = false;
    }

    public double getPrincipal() { return principal; }
    public double getInterestRate() { return interestRate; }
    public long getTimestamp() { return timestamp; }
    public int getDueDays() { return dueDays; }
    public boolean isRepaid() { return repaid; }

    public void markRepaid() { this.repaid = true; }

    public double getTotalOwed() {
        return principal + (principal * interestRate);
    }
}
