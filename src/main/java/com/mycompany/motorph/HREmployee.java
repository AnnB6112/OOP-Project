package com.mycompany.motorph;

public class HREmployee extends Employee implements HROperations {
    public HREmployee(int empNumber, String name, String birthday, String status,
                      String address, String phoneNumber, String position,
                      String supervisor, double hourlyRate, double basicSalary,
                      double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        super(empNumber, name, birthday, status, address, phoneNumber, position, supervisor,
            hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
    }

    @Override
    public String getUserType() {
        return "HR";
    }

    @Override
    public String onboardEmployee() {
        return "HR onboarding completed.";
    }

    @Override
    public String processLeaveRequest() {
        return "Leave request processed by HR.";
    }
}
