package com.mycompany.motorph.dao;

import com.mycompany.motorph.EmployeeRecord;
import java.io.IOException;
import java.util.List;

public interface EmployeeDAO {
    List<EmployeeRecord> getAllEmployees() throws IOException;
    EmployeeRecord getByEmployeeNumber(String employeeNumber) throws IOException;
    void addEmployee(EmployeeRecord employee) throws IOException;
    void updateEmployee(String employeeNumber, EmployeeRecord employee) throws IOException;
    void deleteEmployee(String employeeNumber) throws IOException;
}
