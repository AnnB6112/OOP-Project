import javax.swing.*;
import java.awt.*;

public class UpdateEmployeeFrame extends JFrame {
    private EmployeeManagementSystem parentFrame;
    private Employee employee;
    private int employeeIndex;

    private JTextField employeeNumberField, lastNameField, firstNameField, birthdayField, addressField, phoneField;
    private JTextField sssField, philHealthField, tinField, pagIbigField, statusField, positionField, supervisorField;
    private JTextField basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField;
    private JTextField grossSemiMonthlyField, hourlyRateField;

    public UpdateEmployeeFrame(EmployeeManagementSystem parent, Employee employee, int index) {
        this.parentFrame = parent;
        this.employee = employee;
        this.employeeIndex = index;

        setTitle("MotorPH - Update Employee - " + employee.getFirstName() + " " + employee.getLastName());
        setSize(500, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeComponents();
        populateFields();
        setVisible(true);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        employeeNumberField = new JTextField();
        lastNameField = new JTextField();
        firstNameField = new JTextField();
        birthdayField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        sssField = new JTextField();
        philHealthField = new JTextField();
        tinField = new JTextField();
        pagIbigField = new JTextField();
        statusField = new JTextField();
        positionField = new JTextField();
        supervisorField = new JTextField();
        basicSalaryField = new JTextField();
        riceSubsidyField = new JTextField();
        phoneAllowanceField = new JTextField();
        clothingAllowanceField = new JTextField();
        grossSemiMonthlyField = new JTextField();
        hourlyRateField = new JTextField();

        addLabeledField(formPanel, "Employee Number:", employeeNumberField);
        addLabeledField(formPanel, "Last Name:", lastNameField);
        addLabeledField(formPanel, "First Name:", firstNameField);
        addLabeledField(formPanel, "Birthday (yyyy-mm-dd):", birthdayField);
        addLabeledField(formPanel, "Address:", addressField);
        addLabeledField(formPanel, "Phone Number:", phoneField);
        addLabeledField(formPanel, "SSS Number:", sssField);
        addLabeledField(formPanel, "PhilHealth Number:", philHealthField);
        addLabeledField(formPanel, "TIN Number:", tinField);
        addLabeledField(formPanel, "Pag-IBIG Number:", pagIbigField);
        addLabeledField(formPanel, "Status:", statusField);
        addLabeledField(formPanel, "Position:", positionField);
        addLabeledField(formPanel, "Immediate Supervisor:", supervisorField);
        addLabeledField(formPanel, "Basic Salary:", basicSalaryField);
        addLabeledField(formPanel, "Rice Subsidy:", riceSubsidyField);
        addLabeledField(formPanel, "Phone Allowance:", phoneAllowanceField);
        addLabeledField(formPanel, "Clothing Allowance:", clothingAllowanceField);
        addLabeledField(formPanel, "Gross Semi-monthly Rate:", grossSemiMonthlyField);
        addLabeledField(formPanel, "Hourly Rate:", hourlyRateField);

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        updateButton.addActionListener(e -> updateEmployee());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabeledField(JPanel panel, String label, JTextField field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private void populateFields() {
        employeeNumberField.setText(employee.getEmployeeNumber());
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        birthdayField.setText(employee.getBirthday());
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhoneNumber());
        sssField.setText(employee.getSssNumber());
        philHealthField.setText(employee.getPhilHealthNumber());
        tinField.setText(employee.getTinNumber());
        pagIbigField.setText(employee.getPagIbigNumber());
        statusField.setText(employee.getStatus());
        positionField.setText(employee.getPosition());
        supervisorField.setText(employee.getImmediateSupervisor());
        basicSalaryField.setText(String.valueOf(employee.getBasicSalary()));
        riceSubsidyField.setText(String.valueOf(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(String.valueOf(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(String.valueOf(employee.getClothingAllowance()));
        grossSemiMonthlyField.setText(String.valueOf(employee.getGrossSemiMonthlyRate()));
        hourlyRateField.setText(String.valueOf(employee.getHourlyRate()));
    }

    private void updateEmployee() {
        if (!validateFields()) {
            return;
        }

        try {
            double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
            double riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
            double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
            double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
            double grossSemiMonthly = Double.parseDouble(grossSemiMonthlyField.getText().trim());
            double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());

            Employee updatedEmployee = new Employee(
                employeeNumberField.getText().trim(),
                lastNameField.getText().trim(),
                firstNameField.getText().trim(),
                birthdayField.getText().trim(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                sssField.getText().trim(),
                philHealthField.getText().trim(),
                tinField.getText().trim(),
                pagIbigField.getText().trim(),
                statusField.getText().trim(),
                positionField.getText().trim(),
                supervisorField.getText().trim(),
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthly,
                hourlyRate
            );

            parentFrame.updateEmployeeInList(updatedEmployee, employeeIndex);
            dispose();
            JOptionPane.showMessageDialog(parentFrame, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for salary and allowance fields.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields() {
        JTextField[] fields = {
            employeeNumberField, lastNameField, firstNameField, birthdayField, addressField, phoneField,
            sssField, philHealthField, tinField, pagIbigField, statusField, positionField, supervisorField,
            basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField,
            grossSemiMonthlyField, hourlyRateField
        };

        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}
