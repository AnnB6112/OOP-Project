package com.mycompany.motorph;

public class FinanceEmployee extends Employee {
    public FinanceEmployee(int empNumber, String name, String birthday, String status,
                           String address, String phoneNumber, String position,
                           String supervisor, double hourlyRate, double basicSalary,
                           double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        super(empNumber, name, birthday, status, address, phoneNumber, position, supervisor,
            hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
    }

    @Override
    public String getUserType() {
        return "Finance";
    }
}
