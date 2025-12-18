package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class PassengerDashboard extends JFrame {
    private JPanel mainContentPanel;
    private JPanel profilePanel;
    private JButton logoutBtn, updateProfileBtn;
    private JLabel lblStatus;
    
    // Navigation buttons
    private JButton btnProfile, btnSearchTrips, btnMyBookings, btnHistory;

    private User passenger;
    private TransportService service;
    
    // Panel instances
    private SearchPanel searchPanel;
    private BookingPanel bookingPanel;
    private HistoryPanel historyPanel;

    public PassengerDashboard(User passenger, TransportService service) {
        this.passenger = passenger;
        this.service = service;
        
        setTitle("Passenger Dashboard - " + passenger.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set overall background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // Initialize Menu Bar
        setupMenuBar();

        initializeComponents();
        setupEventListeners();
        showProfilePanel(); // Show profile by default
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu (Logout, Exit)
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem profileItem = new JMenuItem("Update Profile");
        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProfilePanel();
            }
        });
        editMenu.add(profileItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(PassengerDashboard.this, 
                    "Transport Portal v1.0\nPassenger Dashboard System", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // Helper method to centralize logout logic
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(PassengerDashboard.this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void initializeComponents() {
        // Header with Navigation
        JPanel headerPanel = createHeaderWithNavigation();
        headerPanel.setPreferredSize(new Dimension(950, 100));
        add(headerPanel, BorderLayout.NORTH);

        // Initialize panels
        historyPanel = new HistoryPanel(service, passenger);
        bookingPanel = new BookingPanel(service, passenger);
        searchPanel = new SearchPanel(service, passenger, bookingPanel);
        profilePanel = createProfilePanel();

        // Main content area
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 240, 240));
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainContentPanel, BorderLayout.CENTER);

        // Footer
        setupFooter();
    }

    private JPanel createHeaderWithNavigation() {
        JPanel headerPanel = new GradientHeaderPanel(passenger.getFullName(), passenger.getRole());
        headerPanel.setLayout(new BorderLayout());

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        navPanel.setOpaque(false); 
        
        btnProfile = createNavButton("Profile", new Color(173, 216, 230)); 
        btnSearchTrips = createNavButton("Search Trips", new Color(144, 238, 144)); 
        btnMyBookings = createNavButton("My Bookings", new Color(255, 218, 185)); 
        btnHistory = createNavButton("History", new Color(216, 191, 216)); 

        navPanel.add(btnProfile);
        navPanel.add(btnSearchTrips);
        navPanel.add(btnMyBookings);
        navPanel.add(btnHistory);

        headerPanel.add(navPanel, BorderLayout.SOUTH);
        return headerPanel;
    }

    private JButton createNavButton(String text, final Color baseColor) {
        final JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.BLACK); 
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
        });
        
        return button;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Edit Profile",
            0, 0, 
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(70, 130, 180)
        ));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblFullName = new JLabel("Full Name:");
        JTextField txtFullName = new JTextField(20);
        txtFullName.setText(passenger.getFullName());

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblFullName, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtFullName, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void setupFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        footerPanel.setBackground(new Color(220, 220, 220));

        updateProfileBtn = new JButton("🔄 Update Profile");
        styleUpdateProfileButton(updateProfileBtn);
        
        logoutBtn = new JButton("🚪 Logout");
        styleLogoutButton(logoutBtn);

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(0, 102, 0));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(220, 220, 220));
        btnPanel.add(updateProfileBtn);
        btnPanel.add(logoutBtn);

        footerPanel.add(lblStatus, BorderLayout.CENTER);
        footerPanel.add(btnPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProfilePanel();
            }
        });

        btnSearchTrips.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchPanel();
            }
        });

        btnMyBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookingsPanel();
            }
        });

        btnHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistoryPanel();
            }
        });

        updateProfileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblStatus.setText("Profile updated successfully! - " + java.time.LocalTime.now().toString());
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }

    private void showProfilePanel() {
        mainContentPanel.removeAll();
        mainContentPanel.add(profilePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setActiveButton(btnProfile);
    }

    private void showSearchPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.add(searchPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setActiveButton(btnSearchTrips);
    }

    private void showBookingsPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.add(bookingPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setActiveButton(btnMyBookings);
    }

    private void showHistoryPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.add(historyPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setActiveButton(btnHistory);
    }

    private void setActiveButton(JButton activeButton) {
        btnProfile.setBackground(new Color(173, 216, 230));
        btnSearchTrips.setBackground(new Color(144, 238, 144));
        btnMyBookings.setBackground(new Color(255, 218, 185));
        btnHistory.setBackground(new Color(216, 191, 216));
        
        activeButton.setBackground(activeButton.getBackground().darker());
        activeButton.setForeground(Color.BLACK);
    }

    private void styleUpdateProfileButton(final JButton btn) {
        btn.setBackground(new Color(173, 216, 230));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleLogoutButton(final JButton btn) {
        btn.setBackground(new Color(255, 182, 193));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    class GradientHeaderPanel extends JPanel {
        private String name, role, date;
        public GradientHeaderPanel(String name, String role) {
            this.name = name;
            this.role = role;
            this.date = LocalDate.now().toString();
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(60, 60, 60), getWidth(), getHeight(), new Color(100, 100, 100));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.drawString("Welcome, " + name + " (" + role + ")", 20, 30);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString(date, getWidth() - 120, 30);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User testUser = new User();
                testUser.setFullName("John Passenger");
                testUser.setRole("PASSENGER");
                new PassengerDashboard(testUser, new TransportService()).setVisible(true);
            }
        });
    }
}