package com.mycompany.motorph;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final AccountAuthService authService = new AccountAuthService();

    public LoginFrame() {
        super("MotorPH Login");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> tryLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void tryLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        AccountAuthService.AuthResult result = authService.authenticate(username, password);
        if (result.authenticated) {
            if ("ADMIN".equalsIgnoreCase(result.role)) {
                LeaveApprovalFrame adminFrame = new LeaveApprovalFrame();
                adminFrame.setVisible(true);
            } else {
                EmployeePayrollGUI app = new EmployeePayrollGUI();
                app.setVisible(true);
            }
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(
            this,
            "Invalid username or password. Access denied.",
            "Login Failed",
            JOptionPane.ERROR_MESSAGE
        );
        passwordField.setText("");
    }
}
