public class Employee {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String birthday;
    private String address;
    private String phoneNumber;
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;
    private String status;
    private String position;
    private String immediateSupervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    // Constructor used in fromCSV
    public Employee(String employeeNumber, String lastName, String firstName, String birthday, String address,
                    String phoneNumber, String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber,
                    String status, String position, String immediateSupervisor, double basicSalary, double riceSubsidy,
                    double phoneAllowance, double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {

        // Ensure employee number always starts with EMP
        this.employeeNumber = employeeNumber.startsWith("EMP") ? employeeNumber : "EMP" + employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
        this.status = status;
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    // Static method to parse from CSV line
    public static Employee fromCSV(String csvLine) {
        String[] fields = csvLine.split(",", -1);
        if (fields.length < 19) {
            throw new IllegalArgumentException("CSV line does not have 19 fields: " + csvLine);
        }

        return new Employee(
                fields[0].trim(),                         // Employee Number (will auto "EMP" prefix if missing)
                fields[1].trim(),                         // Last Name
                fields[2].trim(),                         // First Name
                fields[3].trim(),                         // Birthday
                fields[4].trim(),                         // Address
                fields[5].trim(),                         // Phone Number
                fields[6].trim(),                         // SSS #
                fields[7].trim(),                         // PhilHealth #
                fields[8].trim(),                         // TIN #
                fields[9].trim(),                         // Pag-ibig #
                fields[10].trim(),                        // Status
                fields[11].trim(),                        // Position
                fields[12].trim(),                        // Immediate Supervisor
                Double.parseDouble(fields[13].trim()),    // Basic Salary
                Double.parseDouble(fields[14].trim()),    // Rice Subsidy
                Double.parseDouble(fields[15].trim()),    // Phone Allowance
                Double.parseDouble(fields[16].trim()),    // Clothing Allowance
                Double.parseDouble(fields[17].trim()),    // Gross Semi-monthly Rate
                Double.parseDouble(fields[18].trim())     // Hourly Rate
        );
    }

    // CSV representation for saving
    public String toCSV() {
        String rawEmpNumber = employeeNumber.startsWith("EMP") ? employeeNumber.substring(3) : employeeNumber;
        return String.join(",",
                rawEmpNumber,
                lastName,
                firstName,
                birthday,
                address,
                phoneNumber,
                sssNumber,
                philHealthNumber,
                tinNumber,
                pagIbigNumber,
                status,
                position,
                immediateSupervisor,
                String.valueOf(basicSalary),
                String.valueOf(riceSubsidy),
                String.valueOf(phoneAllowance),
                String.valueOf(clothingAllowance),
                String.valueOf(grossSemiMonthlyRate),
                String.valueOf(hourlyRate)
        );
    }

    public static String getCSVHeader() {
        return "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #," +
                "Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance," +
                "Gross Semi-monthly Rate,Hourly Rate";
    }

    // ========== GETTERS ==========
    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getBirthday() { return birthday; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSssNumber() { return sssNumber; }
    public String getPhilHealthNumber() { return philHealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagIbigNumber() { return pagIbigNumber; }
    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getImmediateSupervisor() { return immediateSupervisor; }
    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public double getHourlyRate() { return hourlyRate; }
}
