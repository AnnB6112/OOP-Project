package com.mycompany.motorph;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LeaveRequestFrame extends JFrame {
    private static final Path LEAVE_REQUESTS_CSV = Paths.get("src/main/resources/leave_requests.csv");

    private final JTextField employeeNumberField = new JTextField();
    private final JTextField employeeNameField = new JTextField();
    private final JTextField employeePositionField = new JTextField();
    private final JComboBox<String> leaveTypeCombo = new JComboBox<>(new String[]{"Vacation", "Sick", "Emergency", "LWOP"});
    private final JTextField startDateField = new JTextField("YYYY-MM-DD");
    private final JTextField endDateField = new JTextField("YYYY-MM-DD");
    private final JTextArea reasonArea = new JTextArea(4, 20);
    private final JLabel statusLabel = new JLabel("Status: Not Submitted");

    public LeaveRequestFrame() {
        this(null);
    }

    public LeaveRequestFrame(EmployeeRecord selectedEmployee) {
        super("Employee Leave Request");
        setSize(560, 470);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 5, 15));

        form.add(new JLabel("Employee Number:"));
        form.add(employeeNumberField);
        form.add(new JLabel("Employee Name:"));
        form.add(employeeNameField);
        form.add(new JLabel("Position:"));
        form.add(employeePositionField);
        form.add(new JLabel("Leave Type:"));
        form.add(leaveTypeCombo);
        form.add(new JLabel("Date From:"));
        form.add(startDateField);
        form.add(new JLabel("Date To:"));
        form.add(endDateField);

        JPanel reasonPanel = new JPanel(new BorderLayout());
        reasonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reason"));
        reasonPanel.add(new JScrollPane(reasonArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        statusLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JPanel buttons = new JPanel();
        JButton submitButton = new JButton("Submit Request");
        JButton cancelButton = new JButton("Cancel");
        submitButton.addActionListener(e -> submitRequest());
        cancelButton.addActionListener(e -> dispose());
        buttons.add(submitButton);
        buttons.add(cancelButton);

        bottom.add(statusLabel, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        add(form, BorderLayout.NORTH);
        add(reasonPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        if (selectedEmployee != null) {
            employeeNumberField.setText(selectedEmployee.employeeNumber);
            employeeNameField.setText(selectedEmployee.firstName + " " + selectedEmployee.lastName);
            employeePositionField.setText(selectedEmployee.position);
            employeeNumberField.setEditable(false);
            employeeNameField.setEditable(false);
            employeePositionField.setEditable(false);
        }
    }

    private void submitRequest() {
        String employeeNumber = employeeNumberField.getText().trim();
        String employeeName = employeeNameField.getText().trim();
        String employeePosition = employeePositionField.getText().trim();
        String leaveType = (String) leaveTypeCombo.getSelectedItem();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (employeeNumber.isBlank() || employeeName.isBlank() || reason.isBlank()) {
            JOptionPane.showMessageDialog(this, "Employee Number, Name, and Reason are required.");
            return;
        }

        try {
            Integer.parseInt(employeeNumber);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee Number must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(startDate);
            LocalDate.parse(endDate);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dates must use YYYY-MM-DD format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ensureHeader();
            try (BufferedWriter bw = Files.newBufferedWriter(LEAVE_REQUESTS_CSV, StandardOpenOption.APPEND)) {
                bw.newLine();
                bw.write(toCsv(employeeNumber, employeeName, employeePosition, leaveType, startDate, endDate, reason, "Submitted"));
            }
            statusLabel.setText("Status: Submitted");
            JOptionPane.showMessageDialog(this, "Leave request submitted.");
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save leave request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ensureHeader() throws IOException {
        if (Files.notExists(LEAVE_REQUESTS_CSV) || Files.size(LEAVE_REQUESTS_CSV) == 0) {
            Files.createDirectories(LEAVE_REQUESTS_CSV.getParent());
            Files.writeString(LEAVE_REQUESTS_CSV, "employee_number,employee_name,employee_position,leave_type,date_from,date_to,reason,status");
        }
    }

    private String toCsv(String... fields) {
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
