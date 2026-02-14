import javax.swing.*;
import java.awt.*;

public class NewEmployeeFrame extends JFrame {
    private EmployeeManagementSystem parentFrame;
    private JTextField employeeNumberField, lastNameField, firstNameField, birthdayField, addressField, phoneField;
    private JTextField sssField, philHealthField, tinField, pagIbigField, statusField, positionField, supervisorField;
    private JTextField basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField;
    private JTextField grossSemiMonthlyField, hourlyRateField;

    public NewEmployeeFrame(EmployeeManagementSystem parent) {
        this.parentFrame = parent;
        setTitle("MotorPH - Add New Employee");
        setSize(500, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initializeComponents();
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
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabeledField(JPanel panel, String label, JTextField field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private void saveEmployee() {
        try {
            // Parse double values
            double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
            double riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
            double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
            double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
            double grossSemiMonthly = Double.parseDouble(grossSemiMonthlyField.getText().trim());
            double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());

            Employee newEmployee = new Employee(
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

            parentFrame.addEmployee(newEmployee);
            dispose();
            JOptionPane.showMessageDialog(parentFrame, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for salary fields.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
