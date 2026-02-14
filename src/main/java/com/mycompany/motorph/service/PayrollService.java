package com.mycompany.motorph.service;

import com.mycompany.motorph.EmployeeRecord;
import com.mycompany.motorph.dao.EmployeeDAO;
import java.io.IOException;
import java.util.List;

public class PayrollService {
    private final EmployeeDAO employeeDAO;

    public PayrollService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public List<EmployeeRecord> getAllEmployees() throws IOException {
        return employeeDAO.getAllEmployees();
    }

    public EmployeeRecord getEmployeeByNumber(String employeeNumber) throws IOException {
        return employeeDAO.getByEmployeeNumber(employeeNumber);
    }

    public void addEmployee(EmployeeRecord employee) throws IOException {
        employeeDAO.addEmployee(employee);
    }

    public void updateEmployee(String employeeNumber, EmployeeRecord employee) throws IOException {
        employeeDAO.updateEmployee(employeeNumber, employee);
    }

    public void deleteEmployee(String employeeNumber) throws IOException {
        employeeDAO.deleteEmployee(employeeNumber);
    }
}
