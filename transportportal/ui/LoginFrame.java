package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;


public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private TransportService service;

    public LoginFrame() {
        service = new TransportService();
        setTitle("Transport Portal Login");
        setSize(520, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        initialize();
    }

    private void initialize() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Transport Portal Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitle.setForeground(new Color(20, 60, 120));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        mainPanel.add(txtUsername, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        mainPanel.add(txtPassword, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<String>(new String[]{"ADMIN", "DRIVER", "PASSENGER"});
        mainPanel.add(cmbRole, gbc);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttons.setOpaque(false);
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        buttons.add(btnLogin);
        buttons.add(btnRegister);
        buttons.add(btnCancel);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(buttons, gbc);

        // Actions
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText("");
                txtPassword.setText("");
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
            }
        });
    }

    /**
     * Handles login and opens the correct dashboard by role.
     */
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = service.authenticate(username, password, role);
            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Welcome " + user.getFullName() + " (" + user.getRole() + ")",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                openDashboardForRole(user); // ✅ open correct dashboard
                dispose(); // close login frame
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username, password, or role.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the correct dashboard depending on the role.
     */
    private void openDashboardForRole(User user) {
        String role = user.getRole();
        if (role == null) {
            JOptionPane.showMessageDialog(this, "User role missing!");
            return;
        }

        try {
            if ("ADMIN".equalsIgnoreCase(role)) {
                new AdminDashboard(user, service).setVisible(true);
            } else if ("DRIVER".equalsIgnoreCase(role)) {
                new DriverDashboard(user, service).setVisible(true);
            } else if ("PASSENGER".equalsIgnoreCase(role)) {
                new PassengerDashboard(user, service).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Unknown role: " + role);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * For manual testing
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        new LoginFrame().setVisible(true);
    }
}
