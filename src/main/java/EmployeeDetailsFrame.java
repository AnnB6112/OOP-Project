import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmployeeDetailsFrame extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField birthdayField;
    private JTextField employeeNumberField;
    private JTextField sssNumberField;
    private JTextField philHealthField;
    private JTextField tinField;
    private JTextField pagIbigField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField positionField;
    private JTextField statusField;
    private JButton updateButton;
    private JButton deleteButton;
    private Employee employee;
    private int employeeIndex;
    private EmployeeManagementSystem parentFrame;
    private java.util.List<Employee> allEmployees;

    public EmployeeDetailsFrame(EmployeeManagementSystem parent, Employee emp, int index) {
        this.parentFrame = parent;
        this.employee = emp;
        this.employeeIndex = index;
        this.allEmployees = parent.getEmployees(); // Get all employees from parent
        
        setTitle("Employee Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        loadEmployeeData();
        
        setVisible(true);
    }

    private void initializeComponents() {
        // Create main panel with split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(5);

        // Left panel - Table
        JPanel leftPanel = createTablePanel();
        splitPane.setLeftComponent(leftPanel);

        // Right panel - Form
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Records"));

        // Create table with employee data columns
        String[] columnNames = {
            "Employee Number", "Last Name", "First Name", "SSS Number",
            "PhilHealth Number", "TIN", "Pag-IBIG Number"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add employee data
        addEmployeeData();

        // Add selection listener to update form when table row is selected
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadEmployeeDataFromTable(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(128, 0, 128));
        updateButton.setForeground(Color.WHITE);
        updateButton.setBorder(BorderFactory.createDashedBorder(Color.WHITE));

        deleteButton = new JButton("Delete Record");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);

        JButton computeSalaryButton = new JButton("Compute Salary");
        computeSalaryButton.setBackground(new Color(0, 128, 0));
        computeSalaryButton.setForeground(Color.WHITE);

        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(computeSalaryButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // Add action listeners
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        computeSalaryButton.addActionListener(e -> openSalaryComputation());

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Employee Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee Number:"), gbc);
        gbc.gridx = 1;
        employeeNumberField = new JTextField(20);
        employeeNumberField.setEditable(false);
        formPanel.add(employeeNumberField, gbc);

        // First Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        formPanel.add(firstNameField, gbc);



        // Last Name
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Last name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);

        // Birthday
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Birthday:"), gbc);
        gbc.gridx = 1;
        birthdayField = new JTextField(20);
        formPanel.add(birthdayField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Phone Number
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Position
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        positionField = new JTextField(20);
        formPanel.add(positionField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusField = new JTextField(20);
        formPanel.add(statusField, gbc);

        // SSS Number
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("SSS Number:"), gbc);
        gbc.gridx = 1;
        sssNumberField = new JTextField(20);
        formPanel.add(sssNumberField, gbc);

        // PhilHealth Number
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("PhilHealth Number:"), gbc);
        gbc.gridx = 1;
        philHealthField = new JTextField(20);
        formPanel.add(philHealthField, gbc);

        // TIN Number
        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("TIN Number:"), gbc);
        gbc.gridx = 1;
        tinField = new JTextField(20);
        formPanel.add(tinField, gbc);

        // Pag-IBIG Number
        gbc.gridx = 0; gbc.gridy = 11;
        formPanel.add(new JLabel("Pag-IBIG Number:"), gbc);
        gbc.gridx = 1;
        pagIbigField = new JTextField(20);
        formPanel.add(pagIbigField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addEmployeeData() {
        // Add all employees to the table
        if (allEmployees != null) {
            for (Employee emp : allEmployees) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getSssNumber(),
                    emp.getPhilHealthNumber(),
                    emp.getTinNumber(),
                    emp.getPagIbigNumber()
                });
            }
        }
    }

    private void loadEmployeeData() {
        // Load current employee data into form fields
        employeeNumberField.setText(employee.getEmployeeNumber());
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        birthdayField.setText(employee.getBirthday());
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhoneNumber());
        positionField.setText(employee.getPosition());
        statusField.setText(employee.getStatus());
        sssNumberField.setText(employee.getSssNumber());
        philHealthField.setText(employee.getPhilHealthNumber());
        tinField.setText(employee.getTinNumber());
        pagIbigField.setText(employee.getPagIbigNumber());
    }

    private void loadEmployeeDataFromTable(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < allEmployees.size()) {
            Employee selectedEmployee = allEmployees.get(rowIndex);
            employeeNumberField.setText(selectedEmployee.getEmployeeNumber());
            firstNameField.setText(selectedEmployee.getFirstName());
            lastNameField.setText(selectedEmployee.getLastName());
            birthdayField.setText(selectedEmployee.getBirthday());
            addressField.setText(selectedEmployee.getAddress());
            phoneField.setText(selectedEmployee.getPhoneNumber());
            positionField.setText(selectedEmployee.getPosition());
            statusField.setText(selectedEmployee.getStatus());
            sssNumberField.setText(selectedEmployee.getSssNumber());
            philHealthField.setText(selectedEmployee.getPhilHealthNumber());
            tinField.setText(selectedEmployee.getTinNumber());
            pagIbigField.setText(selectedEmployee.getPagIbigNumber());
            
            // Update the current employee reference
            this.employee = selectedEmployee;
            this.employeeIndex = rowIndex;
        }
    }

    private void openSalaryComputation() {
        new SalaryComputationFrame(employee);
    }

    private void updateEmployee() {
        try {
            // Create a new Employee object with updated data from form fields
            Employee updatedEmployee = new Employee(
                employeeNumberField.getText(),
                lastNameField.getText(),
                firstNameField.getText(),
                birthdayField.getText(),
                addressField.getText(),
                phoneField.getText(),
                sssNumberField.getText(),
                philHealthField.getText(),
                tinField.getText(),
                pagIbigField.getText(),
                statusField.getText(),
                positionField.getText(),
                employee.getImmediateSupervisor(), // Keep existing supervisor
                employee.getBasicSalary(), // Keep existing salary
                employee.getRiceSubsidy(), // Keep existing subsidy
                employee.getPhoneAllowance(), // Keep existing allowance
                employee.getClothingAllowance(), // Keep existing allowance
                employee.getGrossSemiMonthlyRate(), // Keep existing rate
                employee.getHourlyRate() // Keep existing rate
            );
            
            // Update in parent frame
            parentFrame.updateEmployeeInList(updatedEmployee, employeeIndex);
            
            // Update the current employee reference
            this.employee = updatedEmployee;
            
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", 
                                        "Update Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), 
                                        "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.deleteEmployeeAtIndex(employeeIndex);
            dispose();
        }
    }


}
