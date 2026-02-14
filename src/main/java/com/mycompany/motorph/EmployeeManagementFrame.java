package com.mycompany.motorph;

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
