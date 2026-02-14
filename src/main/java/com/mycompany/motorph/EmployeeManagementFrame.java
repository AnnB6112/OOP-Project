package com.mycompany.motorph;

import com.mycompany.motorph.dao.CSVEmployeeDAO;
import com.mycompany.motorph.service.PayrollService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class EmployeeManagementFrame extends JFrame {
    private final PayrollService payrollService = new PayrollService(new CSVEmployeeDAO());

    private final DefaultTableModel model;
    private final JTable table;

    private List<EmployeeRecord> employees = new ArrayList<>();
    private List<EmployeeRecord> filteredEmployees = new ArrayList<>();

    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Status", "Regular", "Probationary"});
    private final JComboBox<String> departmentFilter = new JComboBox<>(new String[]{"All Department", "HR", "Finance", "IT", "Admin", "Other"});
    private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{"All Role", "Manager", "Team Leader", "Rank and File", "Officer", "Other"});

    private final JTextArea detailsArea = new JTextArea();

    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");
    private final JButton computeSalaryButton = new JButton("Compute Salary");
    private final JButton leaveRequestButton = new JButton("Leave Request");

    public EmployeeManagementFrame() {
        super("MotorPH Employee Management");
        setSize(1280, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildTopBar(), BorderLayout.NORTH);

        String[] cols = {"Employee No", "Last Name", "First Name", "SSS", "PhilHealth", "TIN", "Pag-IBIG"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelection());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Employee Records"));
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel rightPanel = buildDetailsPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.63);
        add(splitPane, BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildTopBar() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("MotorPH Employee Management", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 12, 5, 0));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(e -> applyFilters());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            departmentFilter.setSelectedIndex(0);
            roleFilter.setSelectedIndex(0);
            applyFilters();
        });

        controls.add(new JLabel("Search:"));
        controls.add(searchField);
        controls.add(searchButton);
        controls.add(new JLabel("Status:"));
        controls.add(statusFilter);
        controls.add(new JLabel("Department:"));
        controls.add(departmentFilter);
        controls.add(new JLabel("Role:"));
        controls.add(roleFilter);
        controls.add(clearButton);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(controls, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        updateButton.setBackground(new Color(56, 132, 255));
        updateButton.setForeground(Color.WHITE);

        deleteButton.setBackground(new Color(215, 58, 73));
        deleteButton.setForeground(Color.WHITE);

        computeSalaryButton.setBackground(new Color(46, 160, 67));
        computeSalaryButton.setForeground(Color.WHITE);

        leaveRequestButton.setBackground(new Color(120, 92, 245));
        leaveRequestButton.setForeground(Color.WHITE);

        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(computeSalaryButton);
        actions.add(leaveRequestButton);

        updateButton.addActionListener(e -> openUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelected());
        computeSalaryButton.addActionListener(e -> openComputeSalary());
        leaveRequestButton.addActionListener(e -> openLeaveRequest());

        setActionsEnabled(false);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        footer.add(actions, BorderLayout.CENTER);

        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void setActionsEnabled(boolean enabled) {
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        computeSalaryButton.setEnabled(enabled);
        leaveRequestButton.setEnabled(enabled);
    }

    private void refreshTable() {
        try {
            employees = payrollService.getAllEmployees();
            filteredEmployees = new ArrayList<>(employees);
            refreshTableModel(filteredEmployees);
            detailsArea.setText("Select an employee row to view details.");
            setActionsEnabled(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employees: " + ex.getMessage());
        }
    }

    private void refreshTableModel(List<EmployeeRecord> records) {
        model.setRowCount(0);
        for (EmployeeRecord e : records) {
            model.addRow(new Object[]{e.employeeNumber, e.lastName, e.firstName, e.sssNumber, e.philHealthNumber, e.tinNumber, e.pagIbigNumber});
        }
    }

    private void applyFilters() {
        String q = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();
        String department = (String) departmentFilter.getSelectedItem();
        String role = (String) roleFilter.getSelectedItem();

        filteredEmployees = new ArrayList<>();
        for (EmployeeRecord e : employees) {
            if (!q.isEmpty()) {
                String hay = (e.employeeNumber + " " + e.lastName + " " + e.firstName).toLowerCase();
                if (!hay.contains(q)) continue;
            }

            if (!"All Status".equals(status) && !e.status.equalsIgnoreCase(status)) continue;

            if (!"All Department".equals(department)) {
                String pos = e.position.toLowerCase();
                boolean match = switch (department) {
                    case "HR" -> pos.contains("hr");
                    case "Finance" -> pos.contains("finance") || pos.contains("payroll") || pos.contains("account");
                    case "IT" -> pos.contains("it");
                    case "Admin" -> pos.contains("chief") || pos.contains("admin") || pos.contains("manager");
                    default -> !(pos.contains("hr") || pos.contains("finance") || pos.contains("payroll") || pos.contains("account") || pos.contains("it") || pos.contains("chief") || pos.contains("admin") || pos.contains("manager"));
                };
                if (!match) continue;
            }

            if (!"All Role".equals(role)) {
                String pos = e.position.toLowerCase();
                boolean match = switch (role) {
                    case "Manager" -> pos.contains("manager") || pos.contains("chief") || pos.contains("head");
                    case "Team Leader" -> pos.contains("team leader");
                    case "Rank and File" -> pos.contains("rank and file");
                    case "Officer" -> pos.contains("officer");
                    default -> true;
                };
                if (!match) continue;
            }

            filteredEmployees.add(e);
        }

        refreshTableModel(filteredEmployees);
        detailsArea.setText("Select an employee row to view details.");
        setActionsEnabled(false);
    }

    private EmployeeRecord getSelectedRecord() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= filteredEmployees.size()) return null;
        return filteredEmployees.get(row);
    }

    private void onRowSelection() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) {
            detailsArea.setText("Select an employee row to view details.");
            setActionsEnabled(false);
            return;
        }

        String text = "EMPLOYEE DETAILS\n"
            + "==============================\n"
            + "Employee No: " + selected.employeeNumber + "\n"
            + "Full Name: " + selected.firstName + " " + selected.lastName + "\n"
            + "Birthday: " + selected.birthday + "\n"
            + "Address: " + selected.address + "\n"
            + "Phone: " + selected.phoneNumber + "\n\n"
            + "Position: " + selected.position + "\n"
            + "Status: " + selected.status + "\n\n"
            + "SSS: " + selected.sssNumber + "\n"
            + "PhilHealth: " + selected.philHealthNumber + "\n"
            + "TIN: " + selected.tinNumber + "\n"
            + "Pag-IBIG: " + selected.pagIbigNumber + "\n";

        detailsArea.setText(text);
        setActionsEnabled(true);
    }

    private void openComputeSalary() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) return;
        new EmployeeDetailsComputeFrame(selected).setVisible(true);
    }

    private void openLeaveRequest() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) return;
        new LeaveRequestFrame(selected).setVisible(true);
    }

    private void openUpdateDialog() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) return;

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] formFields = new JTextField[19];
        String[] labels = {"Employee Number","Last Name","First Name","Birthday","Address","Phone Number","SSS #","PhilHealth #","TIN #","Pag-IBIG #","Status","Position","Immediate Supervisor","Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"};
        String[] values = selected.toCsvRow();

        for (int i = 0; i < labels.length; i++) {
            formFields[i] = new JTextField(values[i]);
            panel.add(new JLabel(labels[i]));
            panel.add(formFields[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        EmployeeRecord updated = new EmployeeRecord();
        updated.employeeNumber = formFields[0].getText().trim();
        updated.lastName = formFields[1].getText().trim();
        updated.firstName = formFields[2].getText().trim();
        updated.birthday = formFields[3].getText().trim();
        updated.address = formFields[4].getText().trim();
        updated.phoneNumber = formFields[5].getText().trim();
        updated.sssNumber = formFields[6].getText().trim();
        updated.philHealthNumber = formFields[7].getText().trim();
        updated.tinNumber = formFields[8].getText().trim();
        updated.pagIbigNumber = formFields[9].getText().trim();
        updated.status = formFields[10].getText().trim();
        updated.position = formFields[11].getText().trim();
        updated.supervisor = formFields[12].getText().trim();
        updated.basicSalary = formFields[13].getText().trim();
        updated.riceSubsidy = formFields[14].getText().trim();
        updated.phoneAllowance = formFields[15].getText().trim();
        updated.clothingAllowance = formFields[16].getText().trim();
        updated.grossSemiMonthly = formFields[17].getText().trim();
        updated.hourlyRate = formFields[18].getText().trim();

        try {
            payrollService.updateEmployee(selected.employeeNumber, updated);
            refreshTable();
            applyFilters();
            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update employee: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete employee " + selected.employeeNumber + " - " + selected.firstName + " " + selected.lastName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            payrollService.deleteEmployee(selected.employeeNumber);
            refreshTable();
            applyFilters();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete employee: " + ex.getMessage());
        }
    }
}
