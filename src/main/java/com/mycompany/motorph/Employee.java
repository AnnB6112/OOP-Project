package com.mycompany.motorph;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;

public abstract class Employee implements Payables {
    protected int empNumber;
    protected String name;
    protected String birthday;
    protected String status;
    protected String address;
    protected String phoneNumber;
    protected String position;
    protected String supervisor;
    protected double hourlyRate;
    protected double basicSalary;
    protected double riceSubsidy;
    protected double phoneAllowance;
    protected double clothingAllowance;

    public Employee(int empNumber, String name, String birthday, String status,
               String address, String phoneNumber, String position,
               String supervisor, double hourlyRate, double basicSalary,
               double riceSubsidy, double phoneAllowance,
               double clothingAllowance) {
        this.empNumber = empNumber;
        this.name = name;
        this.birthday = birthday;
        this.status = status;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.supervisor = supervisor;
        this.hourlyRate = hourlyRate;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }

    public abstract String getUserType();

    public int getEmpNumber() { return empNumber; }
    public String getName() { return name; }
    public String getBirthday() { return birthday; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPosition() { return position; }
    public String getSupervisor() { return supervisor; }
    public double getHourlyRate() { return hourlyRate; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }

    public void setEmpNumber(int empNumber) { this.empNumber = empNumber; }
    public void setName(String name) { this.name = name; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setStatus(String status) { this.status = status; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPosition(String position) { this.position = position; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }

    protected String getCurrentPayPeriod() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        return (today.getDayOfMonth() <= 15)
            ? String.format("%02d/01/%02d - %02d/15/%02d", month, year % 100, month, year % 100)
            : String.format("%02d/16/%02d - %02d/%02d/%02d", month, year % 100, month, today.lengthOfMonth(), year % 100);
    }

    @Override
    public double calculateGrossSalary(double hoursWorked, double overtimeHours) {
        return (hoursWorked * hourlyRate) + (overtimeHours * hourlyRate * 1.25);
    }

    @Override
    public double calculateSSS() {
        double monthlySalary = basicSalary;
        if (monthlySalary < 3250) return 135.0;
        if (monthlySalary > 24750) return 1125.0;

        double lowerBound = 3250;
        for (int i = 0; i < 40; i++) {
            double upperBound = lowerBound + 500;
            if (monthlySalary <= upperBound) {
                return (135 + (i * 22.5));
            }
            lowerBound = upperBound;
        }
        return 0;
    }

    @Override
    public double calculatePhilhealth() {
        double premium = basicSalary * 0.03;
        premium = Math.max(premium, 300);
        premium = Math.min(premium, 1800);
        return premium / 2;
    }

    @Override
    public double calculatePagibig() {
        double monthlyContribution = (basicSalary > 1500) ? basicSalary * 0.02 : basicSalary * 0.01;
        monthlyContribution = Math.min(monthlyContribution, 100);
        return monthlyContribution;
    }

    @Override
    public double calculateDeductions() {
        return calculateSSS() + calculatePhilhealth() + calculatePagibig();
    }

    protected static final double TAX_FREE = 20833;

    @Override
    public double calculateTax(double monthlyGross) {
        double monthlyDeductions = calculateDeductions();
        double taxableIncome = monthlyGross - monthlyDeductions;

        if (taxableIncome <= TAX_FREE) return 0;
        if (taxableIncome <= 33333) return (taxableIncome - TAX_FREE) * 0.20 / 4;
        if (taxableIncome <= 66667) return (2500 + (taxableIncome - 33333) * 0.25) / 4;
        if (taxableIncome <= 166667) return (10833 + (taxableIncome - 66667) * 0.30) / 4;
        if (taxableIncome <= 666667) return (40833.33 + (taxableIncome - 166667) * 0.32) / 4;
        return (200833.33 + (taxableIncome - 666667) * 0.35) / 4;
    }

    @Override
    public double calculateNetSalary(double hoursWorked, double overtimeHours) {
        double gross = calculateGrossSalary(hoursWorked, overtimeHours);
        double tax = calculateTax(gross);
        double deductions = calculateDeductions();
        double net = gross - tax - deductions;
        return Math.max(net, 0);
    }

    public void printPayrollSummary(PrintWriter out, int weekNumber, double regularHours, double overtimeHours, double lateMinutes) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double monthlyGross = basicSalary + riceSubsidy + phoneAllowance + clothingAllowance;
        double weeklyGross = calculateGrossSalary(regularHours, overtimeHours);
        double weeklyTax = calculateTax(monthlyGross) / 4;
        double weeklyDeductions = (weekNumber == 4) ? calculateDeductions() / 4 : 0;
        double lateDeduction = Math.max(0, lateMinutes - 10) * hourlyRate / 60;
        double netPay = weeklyGross - weeklyTax - weeklyDeductions - lateDeduction;

        out.println("\n=====================================");
        out.println("          PAYROLL SUMMARY (Week " + weekNumber + ")");
        out.println("=====================================");
        out.println("Pay Period: " + getCurrentPayPeriod());
        out.println("User Type: " + getUserType());
        out.println("-------------------------------------");
        out.println("ALLOWANCES (Weekly):");
        out.println("  Rice Subsidy: " + df.format(riceSubsidy / 4));
        out.println("  Phone Allowance: " + df.format(phoneAllowance / 4));
        out.println("  Clothing Allowance: " + df.format(clothingAllowance / 4));
        out.println("-------------------------------------");
        out.println("WEEKLY CALCULATION:");
        out.println("  Regular Hours: " + regularHours + " @ " + df.format(hourlyRate) + "/hr");
        out.println("  Overtime Hours: " + overtimeHours + " @ " + df.format(hourlyRate * 1.25) + "/hr");
        out.println("  Late Minutes: " + lateMinutes + " (after 10min grace)");
        out.println("  -------------------------------------");
        out.println("  Weekly Gross Pay: " + df.format(weeklyGross));
        out.println("-------------------------------------");
        out.println("DEDUCTIONS:");
        out.println("  Tax: " + df.format(weeklyTax));
        if (weekNumber == 4) {
            out.println("  SSS: " + df.format(calculateSSS() / 4));
            out.println("  PhilHealth: " + df.format(calculatePhilhealth() / 4));
            out.println("  Pag-IBIG: " + df.format(calculatePagibig() / 4));
        }
        out.println("  Late Deduction: " + df.format(lateDeduction));
        out.println("  -------------------------------------");
        out.println("  Total Deductions: " + df.format(weeklyTax + weeklyDeductions + lateDeduction));
        out.println("-------------------------------------");
        out.println("NET PAY: " + df.format(netPay));
        out.println("=====================================");
    }
}
