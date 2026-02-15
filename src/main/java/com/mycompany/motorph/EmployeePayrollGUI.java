package com.mycompany.motorph;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class EmployeePayrollGUI extends JFrame {

    private static final Color PRIMARY = new Color(45, 108, 223);
    private static final Color BG = new Color(245, 247, 252);

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private final JTextField employeeNumberField = new JTextField(12);
    private final JTextField employeeNameField = new JTextField(18);
    private final JTextArea employeeInfoArea = new JTextArea(10, 55);

    private final JTextField payrollEmployeeNumberField = new JTextField(12);
    private final JComboBox<String> payCoverageComboBox = new JComboBox<>();
    private final JTextArea payrollOutputArea = new JTextArea(12, 55);

    private Employee validatedEmployee;

    public EmployeePayrollGUI() {
        super("MotorPH Payroll System");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createTopNav(), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createEmployeesPanel(), "Employees");
        contentPanel.add(createPayrollPanel(), "Payroll");
        contentPanel.add(createReportsPanel(), "Reports");
        contentPanel.add(createSettingsPanel(), "Settings");

        contentLayout.show(contentPanel, "Dashboard");
        setLocationRelativeTo(null);
    }

    private JPanel createTopNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        nav.setBackground(Color.WHITE);

        JLabel brand = new JLabel("MotorPH PayrollProPH");
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        brand.setForeground(new Color(40, 40, 60));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        buttons.add(createNavButton("Dashboard", "Dashboard"));
        buttons.add(createNavButton("Employees", "Employees"));
        buttons.add(createNavButton("Payroll", "Payroll"));
        buttons.add(createNavButton("Reports", "Reports"));
        buttons.add(createNavButton("Settings", "Settings"));

        nav.add(brand, BorderLayout.WEST);
        nav.add(buttons, BorderLayout.EAST);
        return nav;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> contentLayout.show(contentPanel, cardName));
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = basePanel();

        JLabel title = new JLabel("Welcome to MotorPH Payroll Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JPanel statGrid = new JPanel(new GridLayout(1, 4, 12, 12));
        statGrid.setOpaque(false);
        statGrid.add(statCard("Total Employees", "256"));
        statGrid.add(statCard("Pending Tasks", "28"));
        statGrid.add(statCard("Pending Approvals", "12"));
        statGrid.add(statCard("Next Payroll", "Feb 14"));

        JPanel actionGrid = new JPanel(new GridLayout(1, 3, 12, 12));
        actionGrid.setOpaque(false);
        actionGrid.add(actionCard("Process Payroll", "Start processing payroll"));
        actionGrid.add(actionCard("Manage Employees", "View and update employee data"));
        actionGrid.add(actionCard("View Reports", "Access and download reports"));

        panel.add(title);
        panel.add(Box.createVerticalStrut(16));
        panel.add(statGrid);
        panel.add(Box.createVerticalStrut(16));
        panel.add(actionGrid);

        return wrapPanel(panel);
    }

    private JPanel createEmployeesPanel() {
        JPanel panel = basePanel();

        JLabel title = new JLabel("Employee Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setOpaque(false);

        JButton validateButton = new JButton("Validate Employee");
        validateButton.setBackground(PRIMARY);
        validateButton.setForeground(Color.WHITE);
        validateButton.addActionListener(e -> validateEmployee());

        JButton manageButton = new JButton("Open Employee Management");
        manageButton.setBackground(new Color(236, 120, 54));
        manageButton.setForeground(Color.WHITE);
        manageButton.addActionListener(e -> new EmployeeManagementFrame().setVisible(true));

        JButton leaveRequestButton = new JButton("Request Leave");
        leaveRequestButton.setBackground(new Color(75, 153, 89));
        leaveRequestButton.setForeground(Color.WHITE);
        leaveRequestButton.addActionListener(e -> new LeaveRequestFrame().setVisible(true));

        form.add(new JLabel("Employee Number:"));
        form.add(employeeNumberField);
        form.add(new JLabel("Employee Name:"));
        form.add(employeeNameField);
        form.add(validateButton);
        form.add(manageButton);
        form.add(leaveRequestButton);

        employeeInfoArea.setEditable(false);
        employeeInfoArea.setLineWrap(true);
        employeeInfoArea.setWrapStyleWord(true);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(form);
        panel.add(new JScrollPane(employeeInfoArea));

        return wrapPanel(panel);
    }

    private JPanel createPayrollPanel() {
        JPanel panel = basePanel();

        JLabel title = new JLabel("Payroll Processing");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.setOpaque(false);

        payCoverageComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
            "Select Pay Coverage", "Week 1", "Week 2", "Week 3", "Week 4"
        }));

        JButton processButton = new JButton("Process Payroll");
        processButton.setBackground(PRIMARY);
        processButton.setForeground(Color.WHITE);
        processButton.addActionListener(e -> processPayroll());

        controls.add(new JLabel("Employee Number:"));
        controls.add(payrollEmployeeNumberField);
        controls.add(new JLabel("Pay Coverage:"));
        controls.add(payCoverageComboBox);
        controls.add(processButton);

        payrollOutputArea.setEditable(false);
        payrollOutputArea.setLineWrap(true);
        payrollOutputArea.setWrapStyleWord(true);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(controls);
        panel.add(new JScrollPane(payrollOutputArea));

        return wrapPanel(panel);
    }

    private JPanel createReportsPanel() {
        JPanel panel = basePanel();
        JLabel title = new JLabel("MONTHLY PAYROLL SUMMARY REPORT");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        DefaultTableModel model = createMonthlyPayrollSummaryModel();
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JButton exportCsv = new JButton("Export to CSV");
        JButton exportPdf = new JButton("Export to PDF");

        JPanel exportButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportButtons.setOpaque(false);
        exportButtons.add(exportCsv);
        exportButtons.add(exportPdf);

        panel.add(title);
        panel.add(exportButtons);
        panel.add(new JScrollPane(table));

        return wrapPanel(panel);
    }

    private DefaultTableModel createMonthlyPayrollSummaryModel() {
        String[] cols = {
            "Employee No", "Employee Full Name", "Position", "Department", "Gross Income",
            "Social Security No.", "Social Security Contribution",
            "Philhealth No.", "Philhealth Contribution",
            "Pag-ibig No.", "Pag-ibig Contribution",
            "TIN", "Withholding Tax", "Net Pay"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        List<Employee> employees = MotorPH.loadEmployees();
        java.util.Map<Integer, Employee> employeeByNumber = new java.util.HashMap<>();
        for (Employee employee : employees) {
            employeeByNumber.put(employee.getEmpNumber(), employee);
        }

        double totalGross = 0;
        double totalSss = 0;
        double totalPhilhealth = 0;
        double totalPagibig = 0;
        double totalTax = 0;
        double totalNet = 0;

        java.text.DecimalFormat money = new java.text.DecimalFormat("#,##0.00");

        try (java.io.InputStream is = getClass().getResourceAsStream("/motorph_employee_data.csv")) {
            if (is == null) {
                throw new IOException("Resource not found: /motorph_employee_data.csv");
            }

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                br.readLine();
                String line;
                int added = 0;
                while ((line = br.readLine()) != null && added < 12) {
                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (data.length < 19) {
                        continue;
                    }

                    int empNo = Integer.parseInt(data[0].trim());
                    String fullName = data[1].trim() + ", " + data[2].trim();
                    String position = data[11].trim();
                    String department = deriveDepartment(position);
                    String sssNo = data[6].trim();
                    String philhealthNo = data[7].trim();
                    String tin = data[8].trim();
                    String pagibigNo = data[9].trim();

                    Employee employee = employeeByNumber.get(empNo);
                    if (employee == null) {
                        continue;
                    }

                    double grossIncome = employee.getBasicSalary();
                    double sssContribution = employee.calculateSSS();
                    double philhealthContribution = employee.calculatePhilhealth();
                    double pagibigContribution = employee.calculatePagibig();
                    double withholdingTax = employee.calculateTax(grossIncome);
                    double netPay = grossIncome - sssContribution - philhealthContribution - pagibigContribution - withholdingTax;

                    totalGross += grossIncome;
                    totalSss += sssContribution;
                    totalPhilhealth += philhealthContribution;
                    totalPagibig += pagibigContribution;
                    totalTax += withholdingTax;
                    totalNet += netPay;

                    model.addRow(new Object[]{
                        empNo,
                        fullName,
                        position,
                        department,
                        "₱" + money.format(grossIncome),
                        sssNo,
                        "₱" + money.format(sssContribution),
                        philhealthNo,
                        "₱" + money.format(philhealthContribution),
                        pagibigNo,
                        "₱" + money.format(pagibigContribution),
                        tin,
                        "₱" + money.format(withholdingTax),
                        "₱" + money.format(netPay)
                    });
                    added++;
                }
            }
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Unable to build monthly payroll report: " + ex.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }

        model.addRow(new Object[]{
            "TOTAL", "", "", "",
            "₱" + money.format(totalGross),
            "", "₱" + money.format(totalSss),
            "", "₱" + money.format(totalPhilhealth),
            "", "₱" + money.format(totalPagibig),
            "", "₱" + money.format(totalTax),
            "₱" + money.format(totalNet)
        });

        return model;
    }

    private String deriveDepartment(String position) {
        String normalized = position.toLowerCase();
        if (normalized.contains("account") || normalized.contains("finance") || normalized.contains("payroll")) {
            return "Accounting";
        }
        if (normalized.contains("hr")) {
            return "Human Resources";
        }
        if (normalized.contains("it")) {
            return "IT";
        }
        if (normalized.contains("marketing")) {
            return "Marketing";
        }
        return "Operations";
    }

    private JPanel createSettingsPanel() {
        JPanel panel = basePanel();
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel note = new JLabel("User roles, permissions, and system configurations can be managed here.");
        note.setHorizontalAlignment(SwingConstants.LEFT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(note);

        return wrapPanel(panel);
    }

    private JPanel basePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel wrapPanel(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel statCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel labelText = new JLabel(label);
        labelText.setForeground(new Color(95, 95, 115));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueText.setForeground(new Color(35, 35, 55));

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueText, BorderLayout.CENTER);
        return card;
    }

    private JPanel actionCard(String heading, String subtext) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PRIMARY);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel textLabel = new JLabel(subtext);
        textLabel.setForeground(new Color(230, 236, 255));

        card.add(headingLabel, BorderLayout.NORTH);
        card.add(textLabel, BorderLayout.CENTER);
        return card;
    }

    private void validateEmployee() {
        String employeeNumberText = employeeNumberField.getText().trim();
        String employeeName = employeeNameField.getText().trim();

        if (employeeNumberText.isBlank() || employeeName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter Employee Number and Employee Name.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int employeeNumber = Integer.parseInt(employeeNumberText);
            List<Employee> employees = MotorPH.loadEmployees();
            Employee employee = MotorPH.findEmployee(employees, employeeNumber);

            if (employee == null || !employee.getName().equalsIgnoreCase(employeeName)) {
                validatedEmployee = null;
                throw new IllegalArgumentException("Employee not found or name mismatch.");
            }

            validatedEmployee = employee;
            payrollEmployeeNumberField.setText(String.valueOf(employee.getEmpNumber()));
            employeeInfoArea.setText(buildEmployeeDetails(employee));
            JOptionPane.showMessageDialog(this, "Employee validated successfully.");
        } catch (NumberFormatException ex) {
            validatedEmployee = null;
            JOptionPane.showMessageDialog(this,
                "Invalid Employee Number. Please enter digits only.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void processPayroll() {
        try {
            int employeeNumber = parseEmployeeNumberForPayroll();
            int week = parseSelectedPayCoverage();

            List<Employee> employees = MotorPH.loadEmployees();
            Employee employee = MotorPH.findEmployee(employees, employeeNumber);
            if (employee == null) {
                throw new IllegalArgumentException("Employee Number not found for payroll processing.");
            }

            AttendanceCalculator calculator = new AttendanceCalculator();
            calculator.loadAttendanceData("/motorph_attendance_records.csv");

            LocalDate now = LocalDate.now();
            AttendanceCalculator.AttendanceReport report = calculator.generateReport(employee.getEmpNumber(), now.getYear(), now.getMonthValue());

            double regularHours = report != null ? Math.min(report.getTotalWorkedHours() / 4, 40) : 40;
            double overtimeHours = report != null ? Math.max(0, report.getTotalWorkedHours() / 4 - 40) : 0;
            double lateMinutes = report != null ? (report.getLateHours() * 60) / 4 : 0;

            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter(writer);
            String timecardOutput = calculator.generateTimecard(employee, now.getYear(), now.getMonthValue());
            out.println(timecardOutput);
            out.println();
            employee.printPayrollSummary(out, week, regularHours, overtimeHours, lateMinutes);
            out.flush();
            payrollOutputArea.setText(writer.toString());
            contentLayout.show(contentPanel, "Payroll");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid Employee Number. Please enter digits only.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Unable to load attendance data: " + ex.getMessage(),
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseEmployeeNumberForPayroll() {
        String employeeNumberText = payrollEmployeeNumberField.getText().trim();
        if (employeeNumberText.isBlank()) {
            throw new IllegalArgumentException("Please provide Employee Number before processing payroll.");
        }
        return Integer.parseInt(employeeNumberText);
    }

    private int parseSelectedPayCoverage() {
        String selected = (String) payCoverageComboBox.getSelectedItem();
        if (selected == null || selected.startsWith("Select")) {
            throw new IllegalArgumentException("Please select a valid Pay Coverage.");
        }

        try {
            return Integer.parseInt(selected.replace("Week", "").trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid Pay Coverage selected.");
        }
    }

    private String buildEmployeeDetails(Employee employee) {
        StringBuilder sb = new StringBuilder();
        sb.append("EMPLOYEE DETAILS\n");
        sb.append("====================\n");
        sb.append("Employee #: ").append(employee.getEmpNumber()).append("\n");
        sb.append("Name: ").append(employee.getName()).append("\n");
        sb.append("Birthday: ").append(employee.getBirthday()).append("\n");
        sb.append("Address: ").append(employee.getAddress()).append("\n");
        sb.append("Phone: ").append(employee.getPhoneNumber()).append("\n");
        sb.append("Status: ").append(employee.getStatus()).append("\n");
        sb.append("Position: ").append(employee.getPosition()).append("\n");
        sb.append("Supervisor: ").append(employee.getSupervisor()).append("\n");
        sb.append("Basic Salary: ").append(String.format("%.2f", employee.getBasicSalary())).append("\n");
        sb.append("Rice Subsidy: ").append(String.format("%.2f", employee.getRiceSubsidy())).append("\n");
        sb.append("Phone Allowance: ").append(String.format("%.2f", employee.getPhoneAllowance())).append("\n");
        sb.append("Clothing Allowance: ").append(String.format("%.2f", employee.getClothingAllowance())).append("\n");
        sb.append("Hourly Rate: ").append(String.format("%.2f", employee.getHourlyRate())).append("\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // no-op fallback to default look and feel
        }
        SwingUtilities.invokeLater(() -> new EmployeePayrollGUI().setVisible(true));
    }
}
