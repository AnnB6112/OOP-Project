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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;
import com.mycompany.motorph.dao.CSVEmployeeDAO;
import com.mycompany.motorph.service.PayrollService;
import javax.swing.JButton;
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

    private final JButton viewButton = new JButton("View Employee");
    private final JButton newEmployeeButton = new JButton("New Employee");
    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");
    private final JButton computeSalaryButton = new JButton("Compute Salary");
    private final JButton leaveRequestButton = new JButton("Leave Request");
    private final boolean adminMode;

    public EmployeeManagementFrame() {
        this(false);
    }

    public EmployeeManagementFrame(boolean adminMode) {
        super("MotorPH Employee Management");
        this.adminMode = adminMode;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class EmployeeManagementFrame extends JFrame {
    private final DefaultTableModel model;
    private final JTable table;
    private List<EmployeeRecord> employees;
    private final PayrollService payrollService = new PayrollService(new CSVEmployeeDAO());

    private final JTextField[] fields = new JTextField[19];
    private final JButton viewButton = new JButton("View Employee");
    private final JButton newButton = new JButton("New Employee");
    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");

    public EmployeeManagementFrame() {
        super("Employee Management");
        setSize(1200, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] cols = {"Employee Number", "Last Name", "First Name", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
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

        JPanel actions = new JPanel(new GridLayout(3, 2, 8, 8));
        updateButton.setBackground(new Color(56, 132, 255));
        updateButton.setForeground(Color.WHITE);

        deleteButton.setBackground(new Color(215, 58, 73));
        deleteButton.setForeground(Color.WHITE);

        computeSalaryButton.setBackground(new Color(46, 160, 67));
        computeSalaryButton.setForeground(Color.WHITE);

        leaveRequestButton.setBackground(new Color(120, 92, 245));
        leaveRequestButton.setForeground(Color.WHITE);

        viewButton.setBackground(new Color(108, 117, 125));
        viewButton.setForeground(Color.WHITE);

        newEmployeeButton.setBackground(new Color(13, 110, 253));
        newEmployeeButton.setForeground(Color.WHITE);

        actions.add(viewButton);
        actions.add(newEmployeeButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(computeSalaryButton);
        actions.add(leaveRequestButton);

        viewButton.addActionListener(e -> viewSelectedEmployee());
        newEmployeeButton.addActionListener(e -> openAddDialog());
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

    private void setActionsEnabled(boolean selected) {
        viewButton.setEnabled(selected);
        newEmployeeButton.setEnabled(adminMode);
        updateButton.setEnabled(selected && adminMode);
        deleteButton.setEnabled(selected && adminMode);
        computeSalaryButton.setEnabled(selected);
        leaveRequestButton.setEnabled(selected);
    }

    private void refreshTable() {
        try {
            employees = payrollService.getAllEmployees();
            filteredEmployees = new ArrayList<>(employees);
            refreshTableModel(filteredEmployees);
            detailsArea.setText("Select an employee row to view details.");
            setActionsEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(createEditorPanel(), BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(newButton);
        btnPanel.add(viewButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);
        add(btnPanel, BorderLayout.SOUTH);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);

        newButton.addActionListener(e -> openNewEmployeeDialog());
        viewButton.addActionListener(e -> openSelectedEmployee());
        updateButton.addActionListener(e -> updateSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        refreshTable();
    }

    private JPanel createEditorPanel() {
        JPanel editor = new JPanel(new GridLayout(0, 2, 8, 8));
        editor.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Employee"));

        String[] labels = {"Employee Number","Last Name","First Name","Birthday","Address","Phone Number","SSS #","PhilHealth #","TIN #","Pag-IBIG #","Status","Position","Immediate Supervisor","Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"};
        for (int i = 0; i < labels.length; i++) {
            fields[i] = new JTextField();
            editor.add(new JLabel(labels[i]));
            editor.add(fields[i]);
        }

        return editor;
    }

    void refreshTable() {
        try {
            employees = payrollService.getAllEmployees();
            model.setRowCount(0);
            for (EmployeeRecord e : employees) {
                model.addRow(new Object[]{e.employeeNumber, e.lastName, e.firstName, e.sssNumber, e.philHealthNumber, e.tinNumber, e.pagIbigNumber});
            }
            clearFields();
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            viewButton.setEnabled(false);
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

    private void viewSelectedEmployee() {
        EmployeeRecord selected = getSelectedRecord();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return;
        }

        JOptionPane.showMessageDialog(this, detailsArea.getText(), "Employee Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAddDialog() {
        if (!adminMode) {
            JOptionPane.showMessageDialog(this, "Only admin can add employee records.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] formFields = new JTextField[19];
        String[] labels = {"Employee Number","Last Name","First Name","Birthday","Address","Phone Number","SSS #","PhilHealth #","TIN #","Pag-IBIG #","Status","Position","Immediate Supervisor","Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"};

        for (int i = 0; i < labels.length; i++) {
            formFields[i] = new JTextField();
            panel.add(new JLabel(labels[i]));
            panel.add(formFields[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "New Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        EmployeeRecord created = new EmployeeRecord();
        created.employeeNumber = formFields[0].getText().trim();
        created.lastName = formFields[1].getText().trim();
        created.firstName = formFields[2].getText().trim();
        created.birthday = formFields[3].getText().trim();
        created.address = formFields[4].getText().trim();
        created.phoneNumber = formFields[5].getText().trim();
        created.sssNumber = formFields[6].getText().trim();
        created.philHealthNumber = formFields[7].getText().trim();
        created.tinNumber = formFields[8].getText().trim();
        created.pagIbigNumber = formFields[9].getText().trim();
        created.status = formFields[10].getText().trim();
        created.position = formFields[11].getText().trim();
        created.supervisor = formFields[12].getText().trim();
        created.basicSalary = formFields[13].getText().trim();
        created.riceSubsidy = formFields[14].getText().trim();
        created.phoneAllowance = formFields[15].getText().trim();
        created.clothingAllowance = formFields[16].getText().trim();
        created.grossSemiMonthly = formFields[17].getText().trim();
        created.hourlyRate = formFields[18].getText().trim();

        if (created.employeeNumber.isBlank() || created.lastName.isBlank() || created.firstName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Employee Number, Last Name, and First Name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
    private void onRowSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= employees.size()) {
            clearFields();
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            viewButton.setEnabled(false);
            return;
        }

        EmployeeRecord selected = employees.get(row);
        populateFields(selected);
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
        viewButton.setEnabled(true);
    }

    private void populateFields(EmployeeRecord r) {
        fields[0].setText(r.employeeNumber);
        fields[1].setText(r.lastName);
        fields[2].setText(r.firstName);
        fields[3].setText(r.birthday);
        fields[4].setText(r.address);
        fields[5].setText(r.phoneNumber);
        fields[6].setText(r.sssNumber);
        fields[7].setText(r.philHealthNumber);
        fields[8].setText(r.tinNumber);
        fields[9].setText(r.pagIbigNumber);
        fields[10].setText(r.status);
        fields[11].setText(r.position);
        fields[12].setText(r.supervisor);
        fields[13].setText(r.basicSalary);
        fields[14].setText(r.riceSubsidy);
        fields[15].setText(r.phoneAllowance);
        fields[16].setText(r.clothingAllowance);
        fields[17].setText(r.grossSemiMonthly);
        fields[18].setText(r.hourlyRate);
    }

    private EmployeeRecord buildFromFields() {
        EmployeeRecord r = new EmployeeRecord();
        r.employeeNumber = fields[0].getText().trim();
        r.lastName = fields[1].getText().trim();
        r.firstName = fields[2].getText().trim();
        r.birthday = fields[3].getText().trim();
        r.address = fields[4].getText().trim();
        r.phoneNumber = fields[5].getText().trim();
        r.sssNumber = fields[6].getText().trim();
        r.philHealthNumber = fields[7].getText().trim();
        r.tinNumber = fields[8].getText().trim();
        r.pagIbigNumber = fields[9].getText().trim();
        r.status = fields[10].getText().trim();
        r.position = fields[11].getText().trim();
        r.supervisor = fields[12].getText().trim();
        r.basicSalary = fields[13].getText().trim();
        r.riceSubsidy = fields[14].getText().trim();
        r.phoneAllowance = fields[15].getText().trim();
        r.clothingAllowance = fields[16].getText().trim();
        r.grossSemiMonthly = fields[17].getText().trim();
        r.hourlyRate = fields[18].getText().trim();
        return r;
    }

    private void clearFields() {
        for (JTextField f : fields) {
            f.setText("");
        }
    }

    private void openSelectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }
        EmployeeRecord selected = employees.get(row);
        new EmployeeDetailsComputeFrame(selected).setVisible(true);
    }

    private void updateSelectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        EmployeeRecord previous = employees.get(row);
        EmployeeRecord updated = buildFromFields();
        if (updated.employeeNumber.isBlank() || updated.lastName.isBlank() || updated.firstName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Employee Number, Last Name, and First Name are required.");
            return;
        }

        try {
            payrollService.addEmployee(created);
            refreshTable();
            applyFilters();
            JOptionPane.showMessageDialog(this, "Employee added successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add employee: " + ex.getMessage());
        }
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
        if (!adminMode) {
            JOptionPane.showMessageDialog(this, "Only admin can update employee records.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
        if (!adminMode) {
            JOptionPane.showMessageDialog(this, "Only admin can delete employee records.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
            payrollService.updateEmployee(previous.employeeNumber, updated);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update employee: " + ex.getMessage());
        }
    }

    private void deleteSelectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String employeeNumber = employees.get(row).employeeNumber;
        try {
            payrollService.deleteEmployee(employeeNumber);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete employee: " + ex.getMessage());
        }
    }

    private void openNewEmployeeDialog() {
        JPanel panel = new JPanel(new GridLayout(0,2,8,8));
        JTextField[] formFields = new JTextField[19];
        String[] labels = {"Employee Number","Last Name","First Name","Birthday","Address","Phone Number","SSS #","PhilHealth #","TIN #","Pag-IBIG #","Status","Position","Immediate Supervisor","Basic Salary","Rice Subsidy","Phone Allowance","Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"};
        for (int i=0;i<labels.length;i++) {
            formFields[i] = new JTextField();
            panel.add(new JLabel(labels[i]));
            panel.add(formFields[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, "New Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        EmployeeRecord r = new EmployeeRecord();
        r.employeeNumber = formFields[0].getText().trim();
        r.lastName = formFields[1].getText().trim();
        r.firstName = formFields[2].getText().trim();
        r.birthday = formFields[3].getText().trim();
        r.address = formFields[4].getText().trim();
        r.phoneNumber = formFields[5].getText().trim();
        r.sssNumber = formFields[6].getText().trim();
        r.philHealthNumber = formFields[7].getText().trim();
        r.tinNumber = formFields[8].getText().trim();
        r.pagIbigNumber = formFields[9].getText().trim();
        r.status = formFields[10].getText().trim();
        r.position = formFields[11].getText().trim();
        r.supervisor = formFields[12].getText().trim();
        r.basicSalary = formFields[13].getText().trim();
        r.riceSubsidy = formFields[14].getText().trim();
        r.phoneAllowance = formFields[15].getText().trim();
        r.clothingAllowance = formFields[16].getText().trim();
        r.grossSemiMonthly = formFields[17].getText().trim();
        r.hourlyRate = formFields[18].getText().trim();

        if (r.employeeNumber.isBlank() || r.lastName.isBlank() || r.firstName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Employee Number, Last Name, and First Name are required.");
            return;
        }

        try {
            payrollService.addEmployee(r);
            refreshTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to append employee: " + ex.getMessage());
        }
    }
}
