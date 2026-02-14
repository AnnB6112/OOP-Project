package com.mycompany.motorph;

public class EmployeeRecord {
    String employeeNumber;
    String lastName;
    String firstName;
    String birthday;
    String address;
    String phoneNumber;
    String sssNumber;
    String philHealthNumber;
    String tinNumber;
    String pagIbigNumber;
    String status;
    String position;
    String supervisor;
    String basicSalary;
    String riceSubsidy;
    String phoneAllowance;
    String clothingAllowance;
    String grossSemiMonthly;
    String hourlyRate;

    static EmployeeRecord fromCsvRow(String[] row) {
        EmployeeRecord r = new EmployeeRecord();
        r.employeeNumber = row[0].trim();
        r.lastName = row[1].trim();
        r.firstName = row[2].trim();
        r.birthday = row[3].trim();
        r.address = row[4].trim();
        r.phoneNumber = row[5].trim();
        r.sssNumber = row[6].trim();
        r.philHealthNumber = row[7].trim();
        r.tinNumber = row[8].trim();
        r.pagIbigNumber = row[9].trim();
        r.status = row[10].trim();
        r.position = row[11].trim();
        r.supervisor = row[12].trim();
        r.basicSalary = row[13].trim();
        r.riceSubsidy = row[14].trim();
        r.phoneAllowance = row[15].trim();
        r.clothingAllowance = row[16].trim();
        r.grossSemiMonthly = row[17].trim();
        r.hourlyRate = row[18].trim();
        return r;
    }

    String[] toCsvRow() {
        return new String[]{employeeNumber,lastName,firstName,birthday,address,phoneNumber,sssNumber,philHealthNumber,
            tinNumber,pagIbigNumber,status,position,supervisor,basicSalary,riceSubsidy,phoneAllowance,
            clothingAllowance,grossSemiMonthly,hourlyRate};
    }
}
