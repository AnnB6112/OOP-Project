import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MotorPHCSVUtil {

    // Load employees from a CSV file
    public static List<Employee> loadEmployees(String filePath) {
        List<Employee> employees = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("CSV file not found: " + filePath);
            return employees; // return empty list
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Employee emp = Employee.fromCSV(line);
                        employees.add(emp);
                    } catch (Exception e) {
                        System.err.println("Skipping malformed line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + employees.size() + " employees from CSV.");
        } catch (IOException e) {
            System.err.println("Failed to load employees: " + e.getMessage());
        }

        return employees;
    }

    // Save list of employees to CSV
    public static void saveEmployeesToCSV(List<Employee> employees, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(Employee.getCSVHeader());
            writer.newLine();

            for (Employee emp : employees) {
                writer.write(emp.toCSV());
                writer.newLine();
            }

            System.out.println("Saved " + employees.size() + " employees to CSV: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to save employees: " + e.getMessage());
        }
    }
}
