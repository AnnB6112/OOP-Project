import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SalaryComputationFrame extends JFrame {
    private Employee employee;
    private JComboBox<String> monthComboBox;
    private JTextArea salaryDetailsArea;
    private JButton computeButton;
    private JButton closeButton;

    public SalaryComputationFrame(Employee emp) {
        this.employee = emp;
        
        setTitle("Salary Computation - " + emp.getFirstName() + " " + emp.getLastName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Employee details panel
        JPanel detailsPanel = createEmployeeDetailsPanel();
        mainPanel.add(detailsPanel, BorderLayout.NORTH);

        // Month selection panel
        JPanel monthPanel = createMonthSelectionPanel();
        mainPanel.add(monthPanel, BorderLayout.CENTER);

        // Salary details panel
        JPanel salaryPanel = createSalaryDetailsPanel();
        mainPanel.add(salaryPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createEmployeeDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        // Employee information fields
        panel.add(new JLabel("Employee Number:"));
        panel.add(new JLabel(employee.getEmployeeNumber()));

        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(employee.getFirstName() + " " + employee.getLastName()));

        panel.add(new JLabel("Position:"));
        panel.add(new JLabel(employee.getPosition()));

        panel.add(new JLabel("Status:"));
        panel.add(new JLabel(employee.getStatus()));

        panel.add(new JLabel("Basic Salary:"));
        panel.add(new JLabel("₱" + String.format("%,.2f", employee.getBasicSalary())));

        panel.add(new JLabel("Hourly Rate:"));
        panel.add(new JLabel("₱" + String.format("%,.2f", employee.getHourlyRate())));

        return panel;
    }

    private JPanel createMonthSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Select Month for Salary Computation"));

        JLabel monthLabel = new JLabel("Month:");
        monthComboBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });

        // Set current month as default
        Calendar cal = Calendar.getInstance();
        monthComboBox.setSelectedIndex(cal.get(Calendar.MONTH));

        panel.add(monthLabel);
        panel.add(monthComboBox);

        return panel;
    }

    private JPanel createSalaryDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Salary Computation Results"));

        // Salary details text area
        salaryDetailsArea = new JTextArea(10, 40);
        salaryDetailsArea.setEditable(false);
        salaryDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(salaryDetailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        computeButton = new JButton("Compute Salary");
        closeButton = new JButton("Close");

        computeButton.addActionListener(e -> computeSalary());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(computeButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void computeSalary() {
        try {
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            int monthIndex = monthComboBox.getSelectedIndex();
            
            // Get current year
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            
            // Calculate working days in the month
            int workingDays = getWorkingDaysInMonth(year, monthIndex);
            
            // Calculate salary components
            double basicSalary = employee.getBasicSalary();
            double riceSubsidy = employee.getRiceSubsidy();
            double phoneAllowance = employee.getPhoneAllowance();
            double clothingAllowance = employee.getClothingAllowance();
            double hourlyRate = employee.getHourlyRate();
            
            // Calculate daily rate (assuming 8 hours per day)
            double dailyRate = hourlyRate * 8;
            
            // Calculate monthly salary based on working days
            double monthlyBasicSalary = (basicSalary / 22) * workingDays; // Assuming 22 working days per month
            double monthlyRiceSubsidy = riceSubsidy;
            double monthlyPhoneAllowance = phoneAllowance;
            double monthlyClothingAllowance = clothingAllowance;
            
            // Calculate total monthly salary
            double totalMonthlySalary = monthlyBasicSalary + monthlyRiceSubsidy + 
                                      monthlyPhoneAllowance + monthlyClothingAllowance;
            
            // Calculate deductions (SSS, PhilHealth, Pag-IBIG)
            double sssContribution = calculateSSSContribution(monthlyBasicSalary);
            double philHealthContribution = calculatePhilHealthContribution(monthlyBasicSalary);
            double pagIbigContribution = calculatePagIbigContribution(monthlyBasicSalary);
            
            // Calculate net salary
            double netSalary = totalMonthlySalary - sssContribution - philHealthContribution - pagIbigContribution;
            
            // Display results
            displaySalaryResults(selectedMonth, year, workingDays, monthlyBasicSalary, 
                               monthlyRiceSubsidy, monthlyPhoneAllowance, monthlyClothingAllowance,
                               totalMonthlySalary, sssContribution, philHealthContribution, 
                               pagIbigContribution, netSalary);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error computing salary: " + e.getMessage(), 
                                        "Computation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getWorkingDaysInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int workingDays = 0;
        
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(year, month, day);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            // Exclude weekends (Saturday = 7, Sunday = 1)
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                workingDays++;
            }
        }
        
        return workingDays;
    }

    private double calculateSSSContribution(double monthlySalary) {
        // Simplified SSS calculation (actual rates may vary)
        if (monthlySalary <= 3250) {
            return 135.00;
        } else if (monthlySalary <= 3750) {
            return 157.50;
        } else if (monthlySalary <= 4250) {
            return 180.00;
        } else if (monthlySalary <= 4750) {
            return 202.50;
        } else if (monthlySalary <= 5250) {
            return 225.00;
        } else if (monthlySalary <= 5750) {
            return 247.50;
        } else if (monthlySalary <= 6250) {
            return 270.00;
        } else if (monthlySalary <= 6750) {
            return 292.50;
        } else if (monthlySalary <= 7250) {
            return 315.00;
        } else if (monthlySalary <= 7750) {
            return 337.50;
        } else if (monthlySalary <= 8250) {
            return 360.00;
        } else if (monthlySalary <= 8750) {
            return 382.50;
        } else if (monthlySalary <= 9250) {
            return 405.00;
        } else if (monthlySalary <= 9750) {
            return 427.50;
        } else if (monthlySalary <= 10250) {
            return 450.00;
        } else if (monthlySalary <= 10750) {
            return 472.50;
        } else if (monthlySalary <= 11250) {
            return 495.00;
        } else if (monthlySalary <= 11750) {
            return 517.50;
        } else if (monthlySalary <= 12250) {
            return 540.00;
        } else if (monthlySalary <= 12750) {
            return 562.50;
        } else if (monthlySalary <= 13250) {
            return 585.00;
        } else if (monthlySalary <= 13750) {
            return 607.50;
        } else if (monthlySalary <= 14250) {
            return 630.00;
        } else if (monthlySalary <= 14750) {
            return 652.50;
        } else if (monthlySalary <= 15250) {
            return 675.00;
        } else if (monthlySalary <= 15750) {
            return 697.50;
        } else if (monthlySalary <= 16250) {
            return 720.00;
        } else if (monthlySalary <= 16750) {
            return 742.50;
        } else if (monthlySalary <= 17250) {
            return 765.00;
        } else if (monthlySalary <= 17750) {
            return 787.50;
        } else if (monthlySalary <= 18250) {
            return 810.00;
        } else if (monthlySalary <= 18750) {
            return 832.50;
        } else if (monthlySalary <= 19250) {
            return 855.00;
        } else if (monthlySalary <= 19750) {
            return 877.50;
        } else if (monthlySalary <= 20250) {
            return 900.00;
        } else if (monthlySalary <= 20750) {
            return 922.50;
        } else if (monthlySalary <= 21250) {
            return 945.00;
        } else if (monthlySalary <= 21750) {
            return 967.50;
        } else if (monthlySalary <= 22250) {
            return 990.00;
        } else if (monthlySalary <= 22750) {
            return 1012.50;
        } else if (monthlySalary <= 23250) {
            return 1035.00;
        } else if (monthlySalary <= 23750) {
            return 1057.50;
        } else if (monthlySalary <= 24250) {
            return 1080.00;
        } else if (monthlySalary <= 24750) {
            return 1102.50;
        } else {
            return 1125.00;
        }
    }

    private double calculatePhilHealthContribution(double monthlySalary) {
        // Simplified PhilHealth calculation (actual rates may vary)
        if (monthlySalary <= 10000) {
            return 150.00;
        } else if (monthlySalary <= 59999.99) {
            return monthlySalary * 0.03; // 3% of monthly salary
        } else {
            return 1800.00; // Maximum contribution
        }
    }

    private double calculatePagIbigContribution(double monthlySalary) {
        // Simplified Pag-IBIG calculation (actual rates may vary)
        return Math.min(monthlySalary * 0.02, 100.00); // 2% of monthly salary, max ₱100
    }

    private void displaySalaryResults(String month, int year, int workingDays, 
                                    double basicSalary, double riceSubsidy, double phoneAllowance, 
                                    double clothingAllowance, double totalSalary, 
                                    double sssContribution, double philHealthContribution, 
                                    double pagIbigContribution, double netSalary) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("SALARY COMPUTATION FOR ").append(month.toUpperCase()).append(" ").append(year).append("\n");
        sb.append("=".repeat(50)).append("\n\n");
        
        sb.append("EMPLOYEE INFORMATION:\n");
        sb.append("Name: ").append(employee.getFirstName()).append(" ").append(employee.getLastName()).append("\n");
        sb.append("Employee Number: ").append(employee.getEmployeeNumber()).append("\n");
        sb.append("Position: ").append(employee.getPosition()).append("\n");
        sb.append("Working Days: ").append(workingDays).append(" days\n\n");
        
        sb.append("SALARY BREAKDOWN:\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("Basic Salary:        ₱%,.2f\n", basicSalary));
        sb.append(String.format("Rice Subsidy:        ₱%,.2f\n", riceSubsidy));
        sb.append(String.format("Phone Allowance:     ₱%,.2f\n", phoneAllowance));
        sb.append(String.format("Clothing Allowance:  ₱%,.2f\n", clothingAllowance));
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("Total Gross Salary:  ₱%,.2f\n\n", totalSalary));
        
        sb.append("DEDUCTIONS:\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("SSS Contribution:    ₱%,.2f\n", sssContribution));
        sb.append(String.format("PhilHealth:          ₱%,.2f\n", philHealthContribution));
        sb.append(String.format("Pag-IBIG:            ₱%,.2f\n", pagIbigContribution));
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("Total Deductions:    ₱%,.2f\n\n", 
                               sssContribution + philHealthContribution + pagIbigContribution));
        
        sb.append("NET SALARY:\n");
        sb.append("=".repeat(30)).append("\n");
        sb.append(String.format("Net Take Home:       ₱%,.2f\n", netSalary));
        sb.append("=".repeat(30)).append("\n");
        
        salaryDetailsArea.setText(sb.toString());
    }
} 