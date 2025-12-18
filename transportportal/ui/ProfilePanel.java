package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;


public class ProfilePanel extends JPanel {

    public static interface BackHandler {
        void onBack();
    }

    private final TransportService service;
    private User user;
    private BackHandler backHandler;

    private JTextField txtUsername;
    private JTextField txtFullName;
    private JPasswordField txtPasswordPreview; // won't show real password, just placeholder
    private JButton btnSave;
    private JButton btnChangePassword;
    private JButton btnBack;

    public ProfilePanel(User currentUser, TransportService svc, BackHandler backHandler) {
        if (currentUser == null) throw new IllegalArgumentException("currentUser cannot be null");
        if (svc == null) throw new IllegalArgumentException("service cannot be null");

        this.user = currentUser;
        this.service = svc;
        this.backHandler = backHandler;

        initComponents();
        loadProfileToFields();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel("My Profile", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        center.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtUsername = new JTextField(24);
        center.add(txtUsername, gbc);

        // Full name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        center.add(new JLabel("Full name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtFullName = new JTextField(24);
        center.add(txtFullName, gbc);

        // Password preview (readonly) and change password button
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        center.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        JPanel pwPanel = new JPanel(new BorderLayout(6, 0));
        txtPasswordPreview = new JPasswordField(24);
        txtPasswordPreview.setEditable(false);
        txtPasswordPreview.setEchoChar('*');
        pwPanel.add(txtPasswordPreview, BorderLayout.CENTER);
        btnChangePassword = new JButton("Change Password");
        pwPanel.add(btnChangePassword, BorderLayout.EAST);
        center.add(pwPanel, gbc);

        add(center, BorderLayout.CENTER);

        // Buttons bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnSave = new JButton("Save");
        btnBack = new JButton("Back");
        bottom.add(btnBack);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);

        // listeners
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        btnChangePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onChangePassword();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (backHandler != null) {
                    backHandler.onBack();
                }
            }
        });
    }

    private void loadProfileToFields() {
        txtUsername.setText(user.getUsername() == null ? "" : user.getUsername());
        txtFullName.setText(user.getFullName() == null ? "" : user.getFullName());
        // don't show actual password; show placeholder
        txtPasswordPreview.setText("********");
    }

    private void onSave() {
        String newUsername = txtUsername.getText().trim();
        String newFull = txtFullName.getText().trim();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // update in-memory user and push to DB via service
        user.setUsername(newUsername);
        user.setFullName(newFull);

        try {
            boolean ok = service.updateUser(user); // TransportService.updateUser(User) -> boolean
            if (ok) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. No rows changed.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while updating profile:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onChangePassword() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        JPasswordField pf1 = new JPasswordField();
        JPasswordField pf2 = new JPasswordField();
        panel.add(new JLabel("New password:"));
        panel.add(pf1);
        panel.add(new JLabel("Confirm password:"));
        panel.add(pf2);

        int res = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String a = new String(pf1.getPassword());
        String b = new String(pf2.getPassword());

        if (a.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!a.equals(b)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // apply change
        user.setPassword(a); // plain-text per your current DB design
        try {
            boolean ok = service.updateUser(user);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Password changed successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                txtPasswordPreview.setText("********");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while changing password:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
