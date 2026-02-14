package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCsvRepository implements CrudOperations<EmployeeRecord, String> {
    static final Path CSV_PATH = Paths.get("src/main/resources/motorph_employee_data.csv");
    private static final String HEADER = "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";

    @Override
    public EmployeeRecord create(EmployeeRecord item) throws IOException {
        append(item);
        return item;
    }

    @Override
    public EmployeeRecord read(String id) throws IOException {
        for (EmployeeRecord r : list()) {
            if (r.employeeNumber.equals(id)) return r;
        }
        return null;
    }

    @Override
    public EmployeeRecord update(String id, EmployeeRecord item) throws IOException {
        List<EmployeeRecord> records = list();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).employeeNumber.equals(id)) {
                records.set(i, item);
                saveAll(records);
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean delete(String id) throws IOException {
        List<EmployeeRecord> records = list();
        boolean removed = records.removeIf(r -> r.employeeNumber.equals(id));
        if (removed) saveAll(records);
        return removed;
    }

    @Override
    public List<EmployeeRecord> list() throws IOException {
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

    void append(EmployeeRecord record) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(CSV_PATH, StandardOpenOption.APPEND)) {
            bw.newLine();
            bw.write(joinCsv(record.toCsvRow()));
        }
    }

    void saveAll(List<EmployeeRecord> records) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(CSV_PATH, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            bw.write(HEADER);
            for (EmployeeRecord record : records) {
                bw.newLine();
                bw.write(joinCsv(record.toCsvRow()));
            }
        }
    }

    static String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private static String joinCsv(String[] fields) {
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
