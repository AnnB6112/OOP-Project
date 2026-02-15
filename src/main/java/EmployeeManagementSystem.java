import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EmployeeManagementSystem extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private List<Employee> employees;
    private static final String CSV_FILE = "motorph_employee_data.csv";

    private JButton viewButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton newButton;
    private JTextField searchTextField;

    public EmployeeManagementSystem() {
        super("MotorPH Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        employees = new ArrayList<>();

        initializeTable();
        loadEmployees();    // Load from current working directory
        initializeUI();

        // (Removed: open subframe for first employee)

        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();
        });
    }

    private void initializeTable() {
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
    }

    private void initializeUI() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search: ");
        searchTextField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);
        
        // Add search panel to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Add table to center
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        viewButton = new JButton("View Employee");
        newButton = new JButton("New Employee");

        viewButton.setEnabled(false);

        buttonPanel.add(viewButton);
        buttonPanel.add(newButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        viewButton.addActionListener(e -> viewEmployee());
        newButton.addActionListener(e -> new NewEmployeeFrame(this));
        searchButton.addActionListener(e -> searchEmployees());

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = employeeTable.getSelectedRow() != -1;
            viewButton.setEnabled(selected);
        });
    }

    private void loadEmployees() {
        employees.clear(); // Clear list to avoid duplicates

        File file = new File(CSV_FILE);
        if (!file.exists()) {
            showError("CSV file not found: " + CSV_FILE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                try {
                    Employee emp = Employee.fromCSV(line);
                    employees.add(emp);
                    addEmployeeToTable(emp);
                } catch (Exception ex) {
                    System.err.println("Invalid CSV line: " + line);
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            showError("Error loading employees: " + e.getMessage());
        }
    }

    private void addEmployeeToTable(Employee emp) {
        Vector<String> row = new Vector<>();
        row.add(emp.getEmployeeNumber());
        row.add(emp.getLastName());
        row.add(emp.getFirstName());
        row.add(emp.getSssNumber());
        row.add(emp.getPhilHealthNumber());
        row.add(emp.getTinNumber());
        row.add(emp.getPagIbigNumber());
        tableModel.addRow(row);
    }

    public void addEmployee(Employee emp) {
        employees.add(emp);
        addEmployeeToTable(emp);
        saveEmployees();
    }

    public void updateEmployeeInList(Employee updatedEmp, int index) {
        employees.set(index, updatedEmp);
        refreshTable();
        saveEmployees();
    }

    public void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this employee?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                employees.remove(selectedRow);
                refreshTable();
                saveEmployees();
            }
        }
    }

    public void deleteEmployeeAtIndex(int index) {
        if (index >= 0 && index < employees.size()) {
            employees.remove(index);
            refreshTable();
            saveEmployees();
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            addEmployeeToTable(emp);
        }
    }

    private void saveEmployees() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            writer.write(Employee.getCSVHeader());
            writer.newLine();
            for (Employee emp : employees) {
                writer.write(emp.toCSV());
                writer.newLine();
            }
            writer.flush();
            System.out.println("CSV file saved: " + CSV_FILE);
        } catch (IOException e) {
            showError("Error saving employees: " + e.getMessage());
        }
    }

    private void viewEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row != -1) {
            new EmployeeDetailsFrame(this, employees.get(row), row);
        }
    }

    private void updateEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row != -1) {
            new UpdateEmployeeFrame(this, employees.get(row), row);
        }
    }

    private void searchEmployees() {
        String searchTerm = searchTextField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            if (emp.getEmployeeNumber().toLowerCase().contains(searchTerm) ||
                emp.getFirstName().toLowerCase().contains(searchTerm) ||
                emp.getLastName().toLowerCase().contains(searchTerm) ||
                emp.getSssNumber().toLowerCase().contains(searchTerm) ||
                emp.getPhilHealthNumber().toLowerCase().contains(searchTerm) ||
                emp.getTinNumber().toLowerCase().contains(searchTerm) ||
                emp.getPagIbigNumber().toLowerCase().contains(searchTerm)) {
                addEmployeeToTable(emp);
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("employees.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading login file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            boolean authenticated = false;
            while (!authenticated) {
                JPanel panel = new JPanel(new GridLayout(3, 2));
                JLabel titleLabel = new JLabel("MotorPH Employee Management System", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                panel.add(titleLabel);
                panel.add(new JLabel("")); // Empty cell for spacing
                JTextField userField = new JTextField();
                JPasswordField passField = new JPasswordField();
                panel.add(new JLabel("Username:"));
                panel.add(userField);
                panel.add(new JLabel("Password:"));
                panel.add(passField);
                int result = JOptionPane.showConfirmDialog(null, panel, "MotorPH Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String username = userField.getText().trim();
                    String password = new String(passField.getPassword());
                    if (authenticate(username, password)) {
                        authenticated = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.exit(0);
                }
            }
            EmployeeManagementSystem app = new EmployeeManagementSystem();
            app.setVisible(true);
        });
    }
}
