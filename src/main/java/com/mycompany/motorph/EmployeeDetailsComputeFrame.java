package com.mycompany.motorph;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.time.YearMonth;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EmployeeDetailsComputeFrame extends JFrame {
    private final EmployeeRecord employee;
    private final JComboBox<String> monthCombo;
    private final JTextArea outputArea;

    public EmployeeDetailsComputeFrame(EmployeeRecord employee) {
        super("Employee Details");
        this.employee = employee;
        setSize(760, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthCombo = new JComboBox<>(new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"});
        JButton compute = new JButton("Compute");
        top.add(new JLabel("Month:"));
        top.add(monthCombo);
        top.add(compute);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        compute.addActionListener(e -> computeAndDisplay());
        computeAndDisplay();
    }

    private void computeAndDisplay() {
        int monthIndex = monthCombo.getSelectedIndex() + 1;
        int year = java.time.LocalDate.now().getYear();

        double basic = parseMoney(employee.basicSalary);
        double rice = parseMoney(employee.riceSubsidy);
        double phone = parseMoney(employee.phoneAllowance);
        double clothing = parseMoney(employee.clothingAllowance);

        int workingDays = countWorkingDays(year, monthIndex);
        double monthlyBasic = (basic / 22.0) * workingDays;
        double gross = monthlyBasic + rice + phone + clothing;
        double sss = gross * 0.045;
        double philHealth = gross * 0.015;
        double pagibig = Math.min(100, gross * 0.02);
        double net = gross - sss - philHealth - pagibig;

        DecimalFormat df = new DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("EMPLOYEE DETAILS\n");
        sb.append("==============================\n");
        sb.append("Employee Number: ").append(employee.employeeNumber).append("\n");
        sb.append("Name: ").append(employee.firstName).append(" ").append(employee.lastName).append("\n");
        sb.append("Position: ").append(employee.position).append("\n");
        sb.append("Status: ").append(employee.status).append("\n");
        sb.append("Supervisor: ").append(employee.supervisor).append("\n");
        sb.append("\nCOMPUTED SALARY (" + monthCombo.getSelectedItem() + " " + year + ")\n");
        sb.append("==============================\n");
        sb.append("Working Days: ").append(workingDays).append("\n");
        sb.append("Monthly Basic: ₱").append(df.format(monthlyBasic)).append("\n");
        sb.append("Rice Subsidy: ₱").append(df.format(rice)).append("\n");
        sb.append("Phone Allowance: ₱").append(df.format(phone)).append("\n");
        sb.append("Clothing Allowance: ₱").append(df.format(clothing)).append("\n");
        sb.append("Gross Pay: ₱").append(df.format(gross)).append("\n");
        sb.append("SSS: ₱").append(df.format(sss)).append("\n");
        sb.append("PhilHealth: ₱").append(df.format(philHealth)).append("\n");
        sb.append("Pag-IBIG: ₱").append(df.format(pagibig)).append("\n");
        sb.append("Net Pay: ₱").append(df.format(net)).append("\n");

        outputArea.setText(sb.toString());
    }

    private int countWorkingDays(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int days = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            java.time.DayOfWeek dow = java.time.LocalDate.of(year, month, d).getDayOfWeek();
            if (dow != java.time.DayOfWeek.SATURDAY && dow != java.time.DayOfWeek.SUNDAY) {
                days++;
            }
        }
        return days;
    }

    private double parseMoney(String value) {
        String cleaned = value == null ? "" : value.replaceAll("[^\\d.]", "");
        return cleaned.isBlank() ? 0 : Double.parseDouble(cleaned);
    }
}
