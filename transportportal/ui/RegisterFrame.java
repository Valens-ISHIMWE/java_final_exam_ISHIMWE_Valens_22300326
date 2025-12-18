package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;


public class RegisterFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtContact;
    private JTextField txtEmail;
    private JTextField txtLicense;
    private JTextField txtExperience;
    private JComboBox<String> comboRole;
    private JButton btnRegister, btnClear, btnBack;
    private TransportService service;
    private JPanel driverFieldsPanel;

    public RegisterFrame() {
        setTitle("User Registration - Transport Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        service = new TransportService();
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(80, 20, 280, 30);
        panel.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(60, 70, 100, 25);
        panel.add(lblUsername);
        txtUsername = new JTextField();
        txtUsername.setBounds(160, 70, 200, 25);
        panel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(60, 105, 100, 25);
        panel.add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 105, 200, 25);
        panel.add(txtPassword);

        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setBounds(60, 140, 100, 25);
        panel.add(lblFullName);
        txtFullName = new JTextField();
        txtFullName.setBounds(160, 140, 200, 25);
        panel.add(txtFullName);

        JLabel lblContact = new JLabel("Contact:");
        lblContact.setBounds(60, 175, 100, 25);
        panel.add(lblContact);
        txtContact = new JTextField();
        txtContact.setBounds(160, 175, 200, 25);
        panel.add(txtContact);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(60, 210, 100, 25);
        panel.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(160, 210, 200, 25);
        panel.add(txtEmail);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(60, 245, 100, 25);
        panel.add(lblRole);
        comboRole = new JComboBox<String>(new String[]{"DRIVER", "PASSENGER"});
        comboRole.setBounds(160, 245, 200, 25);
        panel.add(comboRole);

        // Driver-specific fields panel (initially hidden)
        driverFieldsPanel = new JPanel();
        driverFieldsPanel.setLayout(null);
        driverFieldsPanel.setBounds(50, 280, 350, 80);
        driverFieldsPanel.setBackground(new Color(230, 240, 255));
        driverFieldsPanel.setBorder(BorderFactory.createTitledBorder("Driver Information"));
        driverFieldsPanel.setVisible(false);

        JLabel lblLicense = new JLabel("License No:");
        lblLicense.setBounds(20, 25, 80, 25);
        driverFieldsPanel.add(lblLicense);
        txtLicense = new JTextField();
        txtLicense.setBounds(100, 25, 120, 25);
        driverFieldsPanel.add(txtLicense);

        JLabel lblExperience = new JLabel("Experience:");
        lblExperience.setBounds(230, 25, 70, 25);
        driverFieldsPanel.add(lblExperience);
        txtExperience = new JTextField("0");
        txtExperience.setBounds(300, 25, 40, 25);
        driverFieldsPanel.add(txtExperience);

        panel.add(driverFieldsPanel);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(60, 380, 100, 30);
        styleRegisterButton(btnRegister);
        panel.add(btnRegister);

        btnClear = new JButton("Clear");
        btnClear.setBounds(170, 380, 100, 30);
        styleClearButton(btnClear);
        panel.add(btnClear);

        btnBack = new JButton("Back");
        btnBack.setBounds(280, 380, 100, 30);
        styleBackButton(btnBack);
        panel.add(btnBack);

        // ====== ACTION LISTENERS ======

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // close the registration window
            }
        });

        // Show/hide driver fields based on role selection
        comboRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDriverFields();
            }
        });

        getContentPane().add(panel);
    }

    private void toggleDriverFields() {
        String selectedRole = comboRole.getSelectedItem().toString();
        if ("DRIVER".equals(selectedRole)) {
            driverFieldsPanel.setVisible(true);
            setSize(450, 500); // Expand window for driver fields
        } else {
            driverFieldsPanel.setVisible(false);
            setSize(450, 450); // Shrink window for passenger
        }
        // Center the window after resizing
        setLocationRelativeTo(null);
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();
        String role = comboRole.getSelectedItem().toString();
        String license = txtLicense.getText().trim();
        String experienceStr = txtExperience.getText().trim();

        // Basic validation for all users
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Password, and Full Name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Driver-specific validation - ONLY when role is DRIVER
        if ("DRIVER".equals(role)) {
            if (license.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "License number is required for drivers.\nPlease enter a license number (e.g., 1357).", 
                    "Driver Information Required", JOptionPane.WARNING_MESSAGE);
                txtLicense.requestFocus();
                return;
            }
            
            if (experienceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Experience years are required for drivers.\nPlease enter 0 if you are a new driver.", 
                    "Driver Information Required", JOptionPane.WARNING_MESSAGE);
                txtExperience.requestFocus();
                return;
            }
            
            try {
                int experience = Integer.parseInt(experienceStr);
                if (experience < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Experience years cannot be negative.\nPlease enter a positive number or 0.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    txtExperience.requestFocus();
                    return;
                }
                
                // Validate license number format (should be numeric)
                if (!license.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, 
                        "License number should contain only numbers.\nExample: 1357", 
                        "Invalid License Format", JOptionPane.WARNING_MESSAGE);
                    txtLicense.requestFocus();
                    return;
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Experience must be a valid number.\nPlease enter whole years (e.g., 0, 1, 5).", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                txtExperience.requestFocus();
                return;
            }
        }

        try {
            // Create user object
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setRole(role);
            user.setPhone(contact);
            user.setEmail(email);

            int newId;
            
            if ("DRIVER".equals(role)) {
                // Use the enhanced registration that creates both user and driver records
                int experience = Integer.parseInt(experienceStr);
                newId = service.registerDriverWithDetails(user, license, experience);
                
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "🎉 Driver Registration Successful! 🎉\n\n" +
                            "✅ Account Created Successfully!\n" +
                            "👤 User ID: " + newId + "\n" +
                            "📝 License Number: " + license + "\n" +
                            "📅 Experience: " + experience + " years\n" +
                            "👨‍✈️ Role: Driver\n\n" +
                            "📋 Status: Pending Approval\n" +
                            "ℹ️ Please wait for admin approval before accessing driver features.",
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Registration Failed\n\n" +
                            "Failed to register driver account.\n" +
                            "Please try again or contact support.",
                            "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Regular passenger registration
                newId = service.register(user);
                
                if (newId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "🎉 Registration Successful! 🎉\n\n" +
                            "✅ Account Created Successfully!\n" +
                            "👤 User ID: " + newId + "\n" +
                            "👤 Username: " + username + "\n" +
                            "👥 Role: Passenger\n\n" +
                            "🚌 You can now login and book trips!",
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Registration Failed\n\n" +
                            "Failed to create account.\n" +
                            "Please try again.",
                            "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("username")) {
                JOptionPane.showMessageDialog(this,
                        "❌ Username Already Exists\n\n" +
                        "The username '" + username + "' is already taken.\n" +
                        "Please choose a different username.",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
                txtUsername.requestFocus();
                txtUsername.selectAll();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Database Error\n\n" +
                        "Error: " + ex.getMessage() + "\n" +
                        "Please try again or contact support.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "❌ Unexpected Error\n\n" +
                    "Error: " + ex.getMessage() + "\n" +
                    "Please try again or contact support.",
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtLicense.setText("");
        txtExperience.setText("0");
        comboRole.setSelectedIndex(0);
        driverFieldsPanel.setVisible(false);
        setSize(450, 450);
        setLocationRelativeTo(null);
        txtUsername.requestFocus(); // Set focus back to username field
    }

    // ====== BUTTON STYLING METHODS ======

    private void styleRegisterButton(final JButton btn) {
        btn.setBackground(new Color(40, 167, 69)); // GREEN
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK FOR BETTER READABILITY
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 136, 56));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON HOVER
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON EXIT
            }
        });
    }

    private void styleClearButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK FOR BETTER READABILITY
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON HOVER
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON EXIT
            }
        });
    }

    private void styleBackButton(final JButton btn) {
        btn.setBackground(new Color(255, 193, 7)); // AMBER
        btn.setForeground(Color.BLACK); // ALREADY BLACK - KEEP AS IS
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 160, 0));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON HOVER
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 193, 7));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON EXIT
            }
        });
    }

    // For direct testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegisterFrame().setVisible(true);
            }
        });
    }
}