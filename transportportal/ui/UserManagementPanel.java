package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private TransportService service;
    private DefaultTableModel userModel;
    private JTable userTable;
    private List<User> users;
    private List<User> filteredUsers; // For search results
    
    // Search components
    private JTextField txtSearch;
    private JComboBox<String> cmbSearchType;
    private JButton btnSearch;
    private JButton btnClearSearch;

    public UserManagementPanel(TransportService service) {
        this.service = service;
        this.users = new ArrayList<User>();
        this.filteredUsers = new ArrayList<User>();
        initialize();
        loadUserData();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("User Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // User table
        userModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Role", "Contact", "Email"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(30);
        JScrollPane userScroll = new JScrollPane(userTable);
        userScroll.setBorder(BorderFactory.createTitledBorder("Users List"));
        add(userScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Search Users", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(33, 150, 243)
        ));

        // Search type dropdown
        cmbSearchType = new JComboBox<String>(new String[]{
            "All Fields", "User ID", "Username", "Full Name", "Role", "Contact", "Email"
        });
        cmbSearchType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Search text field
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Search button
        btnSearch = new JButton("🔍 Search");
        styleSearchButton(btnSearch);
        
        // Clear search button
        btnClearSearch = new JButton("❌ Clear");
        styleClearButton(btnClearSearch);

        // Add components to search panel
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(cmbSearchType);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);

        // Add action listeners for search
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        btnClearSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSearch();
            }
        });

        // Add Enter key listener to search field
        txtSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        return searchPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnAddUser = new JButton("➕ Add User");
        JButton btnEditUser = new JButton("✏️ Edit User");
        JButton btnDeleteUser = new JButton("🗑️ Delete User");
        JButton btnConvertToDriver = new JButton("👨‍✈️ Convert to Driver");
        JButton btnRefresh = new JButton("🔄 Refresh");

        // Style buttons
        styleAddButton(btnAddUser);
        styleEditButton(btnEditUser);
        styleDeleteButton(btnDeleteUser);
        styleConvertButton(btnConvertToDriver);
        styleRefreshButton(btnRefresh);

        // Add buttons to panel
        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnEditUser);
        buttonPanel.add(btnDeleteUser);
        buttonPanel.add(btnConvertToDriver);
        buttonPanel.add(btnRefresh);

        // Add action listeners
        btnAddUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddUserDialog();
            }
        });

        btnEditUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedUser();
            }
        });

        btnDeleteUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedUser();
            }
        });

        btnConvertToDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                convertSelectedUserToDriver();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUserData();
            }
        });

        return buttonPanel;
    }

    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        String searchType = (String) cmbSearchType.getSelectedItem();
        
        if (searchText.isEmpty()) {
            clearSearch();
            return;
        }

        filteredUsers.clear();
        
        for (User user : users) {
            boolean matches = false;
            
            switch (searchType) {
                case "All Fields":
                    matches = matchesAllFields(user, searchText);
                    break;
                case "User ID":
                    matches = String.valueOf(user.getId()).contains(searchText);
                    break;
                case "Username":
                    matches = user.getUsername() != null && 
                             user.getUsername().toLowerCase().contains(searchText);
                    break;
                case "Full Name":
                    matches = user.getFullName() != null && 
                             user.getFullName().toLowerCase().contains(searchText);
                    break;
                case "Role":
                    matches = user.getRole() != null && 
                             user.getRole().toLowerCase().contains(searchText);
                    break;
                case "Contact":
                    matches = user.getPhone() != null && 
                             user.getPhone().toLowerCase().contains(searchText);
                    break;
                case "Email":
                    matches = user.getEmail() != null && 
                             user.getEmail().toLowerCase().contains(searchText);
                    break;
            }
            
            if (matches) {
                filteredUsers.add(user);
            }
        }
        
        refreshUserTable();
        
        // Show search results count
        if (!searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Found " + filteredUsers.size() + " user(s) matching your search criteria.", 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean matchesAllFields(User user, String searchText) {
        return (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchText)) ||
               (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchText)) ||
               (user.getRole() != null && user.getRole().toLowerCase().contains(searchText)) ||
               (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchText)) ||
               (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText)) ||
               String.valueOf(user.getId()).contains(searchText);
    }

    private void clearSearch() {
        txtSearch.setText("");
        filteredUsers.clear();
        refreshUserTable();
    }

    private void loadUserData() {
        try {
            users = service.loadUsersFromDatabase();
            clearSearch(); // This will refresh the table with all users
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshUserTable() {
        userModel.setRowCount(0);
        
        List<User> usersToShow = filteredUsers.isEmpty() ? users : filteredUsers;
        
        for (User user : usersToShow) {
            userModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getPhone() != null ? user.getPhone() : "",
                user.getEmail() != null ? user.getEmail() : ""
            });
        }
        
        // Update table header to show search status
        JViewport viewport = (JViewport) userTable.getParent();
        if (viewport.getParent() instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) viewport.getParent();
            String title = filteredUsers.isEmpty() ? 
                "Users List (" + users.size() + " users)" : 
                "Search Results (" + filteredUsers.size() + " of " + users.size() + " users)";
            ((TitledBorder) scrollPane.getBorder()).setTitle(title);
            scrollPane.repaint();
        }
    }

    // ========== IMPLEMENTED BUTTON FUNCTIONALITY ==========

    private void showAddUserDialog() {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtUsername = new JTextField();
        final JPasswordField txtPassword = new JPasswordField();
        final JTextField txtFullName = new JTextField();
        final JTextField txtContact = new JTextField();
        final JTextField txtEmail = new JTextField();
        final JComboBox<String> cmbRole = new JComboBox<String>(new String[]{"PASSENGER", "DRIVER", "ADMIN"});

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        styleAddButton(btnSave);
        styleCancelButton(btnCancel);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String fullName = txtFullName.getText().trim();
                String contact = txtContact.getText().trim();
                String email = txtEmail.getText().trim();
                String role = (String) cmbRole.getSelectedItem();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username, Password, and Full Name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setFullName(fullName);
                    newUser.setPhone(contact);
                    newUser.setEmail(email);
                    newUser.setRole(role);

                    int userId = service.register(newUser);
                    if (userId > 0) {
                        JOptionPane.showMessageDialog(dialog, "User added successfully! ID: " + userId);
                        dialog.dispose();
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual user from the displayed list (considering search results)
        List<User> currentUsers = filteredUsers.isEmpty() ? users : filteredUsers;
        if (selectedRow >= currentUsers.size()) {
            JOptionPane.showMessageDialog(this, "Invalid selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final User selectedUser = currentUsers.get(selectedRow);
        
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtUsername = new JTextField(selectedUser.getUsername());
        final JPasswordField txtPassword = new JPasswordField();
        final JTextField txtFullName = new JTextField(selectedUser.getFullName());
        final JTextField txtContact = new JTextField(selectedUser.getPhone() != null ? selectedUser.getPhone() : "");
        final JTextField txtEmail = new JTextField(selectedUser.getEmail() != null ? selectedUser.getEmail() : "");
        final JComboBox<String> cmbRole = new JComboBox<String>(new String[]{"PASSENGER", "DRIVER", "ADMIN"});
        cmbRole.setSelectedItem(selectedUser.getRole());

        // Password field hint
        txtPassword.setToolTipText("Leave blank to keep current password");

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        
        styleEditButton(btnSave);
        styleCancelButton(btnCancel);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String fullName = txtFullName.getText().trim();
                String contact = txtContact.getText().trim();
                String email = txtEmail.getText().trim();
                String role = (String) cmbRole.getSelectedItem();

                if (username.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username and Full Name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    // Update user object
                    selectedUser.setUsername(username);
                    selectedUser.setFullName(fullName);
                    selectedUser.setPhone(contact);
                    selectedUser.setEmail(email);
                    selectedUser.setRole(role);
                    
                    // Only update password if provided
                    if (!password.isEmpty()) {
                        selectedUser.setPassword(password);
                    }

                    boolean success = service.updateUser(selectedUser);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "User updated successfully!");
                        dialog.dispose();
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual user from the displayed list (considering search results)
        List<User> currentUsers = filteredUsers.isEmpty() ? users : filteredUsers;
        if (selectedRow >= currentUsers.size()) {
            JOptionPane.showMessageDialog(this, "Invalid selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User selectedUser = currentUsers.get(selectedRow);
        int userId = selectedUser.getId();
        String userName = selectedUser.getFullName() != null ? selectedUser.getFullName() : selectedUser.getUsername();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + userName + "?\nThis action cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = service.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUserData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void convertSelectedUserToDriver() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to convert to driver.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual user from the displayed list (considering search results)
        List<User> currentUsers = filteredUsers.isEmpty() ? users : filteredUsers;
        if (selectedRow >= currentUsers.size()) {
            JOptionPane.showMessageDialog(this, "Invalid selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User selectedUser = currentUsers.get(selectedRow);
        int userId = selectedUser.getId();
        String userName = selectedUser.getFullName() != null ? selectedUser.getFullName() : selectedUser.getUsername();
        String userRole = selectedUser.getRole();

        // Check if user role is already DRIVER
        if ("DRIVER".equalsIgnoreCase(userRole)) {
            JOptionPane.showMessageDialog(this, 
                "Selected user is already a DRIVER.\nNo conversion needed.", 
                "Already Driver", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show dialog to collect driver information
        showConvertToDriverDialog(userId, userName);
    }

    private void showConvertToDriverDialog(final int userId, String userName) {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Convert User to Driver", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtLicense = new JTextField();
        final JTextField txtExperience = new JTextField("0");

        formPanel.add(new JLabel("User:"));
        formPanel.add(new JLabel(userName + " (ID: " + userId + ")"));
        formPanel.add(new JLabel("License Number:"));
        formPanel.add(txtLicense);
        formPanel.add(new JLabel("Experience (years):"));
        formPanel.add(txtExperience);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnConvert = new JButton("Convert");
        JButton btnCancel = new JButton("Cancel");
        
        styleConvertButton(btnConvert);
        styleCancelButton(btnCancel);

        buttonPanel.add(btnConvert);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Store reference to the outer class for the inner class to use
        final UserManagementPanel outerPanel = this;

        btnConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String license = txtLicense.getText().trim();
                String experienceStr = txtExperience.getText().trim();

                if (license.isEmpty() || experienceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "License number and experience are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int experience = Integer.parseInt(experienceStr);
                    
                    // Use the convertUserToDriver method
                    boolean success = service.convertUserToDriver(userId, license, experience);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                            "User successfully converted to driver!\n\n" +
                            "✅ User converted to DRIVER role\n" +
                            "📝 License: " + license + "\n" +
                            "📅 Experience: " + experience + " years\n" +
                            "👨‍✈️ Driver account is now active",
                            "Conversion Successful", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        outerPanel.loadUserData(); // Refresh user list using the outer class reference
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to convert user to driver.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Experience must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    // ========== BUTTON STYLING METHODS ==========

    private void styleSearchButton(final JButton btn) {
        btn.setBackground(new Color(0, 123, 255)); // BLUE
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
            }
        });
    }

    private void styleClearButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
            }
        });
    }

    private void styleAddButton(final JButton btn) {
        btn.setBackground(new Color(40, 167, 69)); // GREEN
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
            }
        });
    }

    private void styleEditButton(final JButton btn) {
        btn.setBackground(new Color(0, 123, 255)); // BLUE
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
            }
        });
    }

    private void styleDeleteButton(final JButton btn) {
        btn.setBackground(new Color(220, 53, 69)); // RED
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 53, 69));
            }
        });
    }

    private void styleConvertButton(final JButton btn) {
        btn.setBackground(new Color(255, 193, 7)); // AMBER
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 160, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 193, 7));
            }
        });
    }

    private void styleRefreshButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
            }
        });
    }

    private void styleCancelButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
            }
        });
    }
}