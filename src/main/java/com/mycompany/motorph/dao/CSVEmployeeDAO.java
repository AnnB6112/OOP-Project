package com.mycompany.motorph.dao;

import com.mycompany.motorph.EmployeeRecord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CSVEmployeeDAO implements EmployeeDAO {
    private static final Path CSV_PATH = Paths.get("src/main/resources/motorph_employee_data.csv");
    private static final String HEADER = "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";

    @Override
    public List<EmployeeRecord> getAllEmployees() throws IOException {
        List<EmployeeRecord> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(CSV_PATH)) {
            String line = br.readLine();
            if (line == null) return list;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = splitCsv(line);
                if (cols.length == 19) list.add(EmployeeRecord.fromCsvRow(cols));
            }
        }
        return list;
    }

    @Override
    public EmployeeRecord getByEmployeeNumber(String employeeNumber) throws IOException {
        for (EmployeeRecord record : getAllEmployees()) {
            if (record.employeeNumber.equals(employeeNumber)) {
                return record;
            }
        }
        return null;
    }

    @Override
    public void addEmployee(EmployeeRecord employee) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(CSV_PATH, StandardOpenOption.APPEND)) {
            bw.newLine();
            bw.write(joinCsv(employee.toCsvRow()));
        }
    }

    @Override
    public void updateEmployee(String employeeNumber, EmployeeRecord employee) throws IOException {
        List<EmployeeRecord> records = getAllEmployees();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).employeeNumber.equals(employeeNumber)) {
                records.set(i, employee);
                saveAll(records);
                return;
            }
        }
    }

    @Override
    public void deleteEmployee(String employeeNumber) throws IOException {
        List<EmployeeRecord> records = getAllEmployees();
        records.removeIf(r -> r.employeeNumber.equals(employeeNumber));
        saveAll(records);
    }

    private void saveAll(List<EmployeeRecord> records) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(CSV_PATH, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            bw.write(HEADER);
            for (EmployeeRecord record : records) {
                bw.newLine();
                bw.write(joinCsv(record.toCsvRow()));
            }
        }
    }

    private String[] splitCsv(String line) {
        String[] rawFields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        String[] fields = new String[rawFields.length];
        for (int i = 0; i < rawFields.length; i++) {
            fields[i] = unquoteCsvField(rawFields[i]);
        }
        return fields;
    }

    private String unquoteCsvField(String field) {
        if (field == null) {
            return "";
        }

        String trimmed = field.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            return inner.replace("\"\"", "\"");
        }
        return trimmed;
    }

    private String joinCsv(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String f = fields[i] == null ? "" : fields[i];
            if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                sb.append('"').append(f.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(f);
            }
        }
        return sb.toString();
    }
}
