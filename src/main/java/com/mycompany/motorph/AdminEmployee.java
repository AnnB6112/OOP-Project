package com.mycompany.motorph;

public class AdminEmployee extends Employee implements AdminOperations {
    public AdminEmployee(int empNumber, String name, String birthday, String status,
                         String address, String phoneNumber, String position,
                         String supervisor, double hourlyRate, double basicSalary,
                         double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        super(empNumber, name, birthday, status, address, phoneNumber, position, supervisor,
            hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
    }

    @Override
    public String getUserType() {
        return "Admin";
    }

    @Override
    public String manageSystemUsers() {
        return "Admin managed system users.";
    }

    @Override
    public String generateAuditTrail() {
        return "Audit trail exported by Admin.";
    }
}
