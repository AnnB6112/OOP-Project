package com.mycompany.motorph;

public interface PayrollCalculations {
    double calculateGrossSalary(double hoursWorked, double overtimeHours);
    double calculateSSS();
    double calculatePhilhealth();
    double calculatePagibig();
    double calculateDeductions();
    double calculateTax(double monthlyGross);
    double calculateNetSalary(double hoursWorked, double overtimeHours);
}
