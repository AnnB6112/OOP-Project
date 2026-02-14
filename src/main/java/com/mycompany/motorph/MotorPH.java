package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class MotorPH {

    private static final String CSV_FILE_PATH = "/motorph_employee_data.csv";
    private static final int EXPECTED_FIELDS = 19;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    static List<Employee> loadEmployees() {
        try {
            return loadEmployeesFromCSV();
        } catch (Exception e) {
            System.err.println("Error loading employee CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static List<Employee> loadEmployeesFromCSV() throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (InputStream is = MotorPH.class.getResourceAsStream(CSV_FILE_PATH)) {
            if (is == null) {
                throw new IOException("Resource not found: " + CSV_FILE_PATH);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                br.readLine(); // Skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = parseCSVLine(line);
                    if (data.length != EXPECTED_FIELDS) {
                        System.err.println("Skipping malformed row (expected 19 fields): " + line);
                        continue;
                    }

                    try {
                        employees.add(createEmployeeFromCSV(data));
                    } catch (NumberFormatException ex) {
                        System.err.println("Skipping row with invalid numeric values: " + line);
                    }
                }
            }
        }

        return employees;
    }

    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private static Employee createEmployeeFromCSV(String[] data) {
        int empNumber = Integer.parseInt(data[0].trim());
        String name = data[2].trim() + " " + data[1].trim();
        String birthday = data[3].trim();
        String address = data[4].trim();
        String phone = data[5].trim();
        String status = data[10].trim();
        String position = data[11].trim();
        String supervisor = data[12].trim();

        double basicSalary = parseMoney(data[13]);
        double riceSubsidy = parseMoney(data[14]);
        double phoneAllowance = parseMoney(data[15]);
        double clothingAllowance = parseMoney(data[16]);
        double hourlyRate = parseMoney(data[18]);

        String positionLower = position.toLowerCase();
        if (positionLower.contains("hr")) {
            return new HREmployee(empNumber, name, birthday, status, address, phone, position, supervisor,
                hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
        }
        if (positionLower.contains("finance") || positionLower.contains("payroll") || positionLower.contains("account")) {
            return new FinanceEmployee(empNumber, name, birthday, status, address, phone, position, supervisor,
                hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
        }
        if (positionLower.contains("it")) {
            return new ITEmployee(empNumber, name, birthday, status, address, phone, position, supervisor,
                hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
        }
        if (positionLower.contains("chief") || positionLower.contains("manager") || positionLower.contains("admin")) {
            return new AdminEmployee(empNumber, name, birthday, status, address, phone, position, supervisor,
                hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
        }
        return new StaffEmployee(empNumber, name, birthday, status, address, phone, position, supervisor,
            hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);
    }

    private static double parseMoney(String value) {
        String cleaned = value.replaceAll("[^\\d.]", "").trim();
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(cleaned);
    }

    public static Employee findEmployee(List<Employee> employees, int empNumber) {
        return employees.stream()
            .filter(emp -> emp.getEmpNumber() == empNumber)
            .findFirst()
            .orElse(null);
    }
}
