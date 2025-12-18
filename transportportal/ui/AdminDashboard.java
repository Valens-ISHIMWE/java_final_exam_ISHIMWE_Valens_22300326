package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {

    private TransportService service;
    private User currentUser;

    public AdminDashboard(User admin, TransportService svc) {
        this.currentUser = admin;
        this.service = svc;

        // Initialize Look and Feel for proper styling
        initializeLookAndFeel();
        
        setTitle("Admin Dashboard - " + (admin.getFullName() == null ? admin.getUsername() : admin.getFullName()));
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialize();
    }

    private void initializeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
            // Continue with default look and feel if there's an error
        }
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Transport Portal Admin Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel userInfo = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.RIGHT);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfo.setForeground(Color.WHITE);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(userInfo, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with separate panels
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Create separate panel instances
        UserManagementPanel userPanel = new UserManagementPanel(service);
        DriverVehiclePanel driverVehiclePanel = new DriverVehiclePanel(service);
        RoutesTripsPanel routesTripsPanel = new RoutesTripsPanel(service);
        BookingsPaymentsPanel bookingsPanel = new BookingsPaymentsPanel(service);
        ReportsAuditPanel reportsPanel = new ReportsAuditPanel(service);

        tabs.addTab("👤 User Management", userPanel);
        tabs.addTab("🚗 Driver & Vehicle", driverVehiclePanel);
        tabs.addTab("🛣️ Routes & Trips", routesTripsPanel);
        tabs.addTab("📋 Bookings & Payments", bookingsPanel);
        tabs.addTab("📊 Reports & Audit", reportsPanel);
        

        add(tabs, BorderLayout.CENTER);

        // Footer with logout button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnManageTrips = new JButton("Manage Trips");
        styleManageTripsButton(btnManageTrips);
        JButton btnLogout = new JButton("Logout");
        styleLogoutButton(btnLogout);
        
        footerPanel.add(btnManageTrips);
        footerPanel.add(btnLogout);
        
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        
        btnManageTrips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TripManagementFrame(currentUser).setVisible(true);
            }
        });
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleManageTripsButton(final JButton btn) {
        btn.setBackground(new Color(40, 167, 69)); // GREEN
        btn.setForeground(Color.BLACK); // CHANGED FROM WHITE TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 136, 56));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON HOVER TOO
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON EXIT TOO
            }
        });
    }

    private void styleLogoutButton(final JButton btn) {
        btn.setBackground(new Color(220, 53, 69)); // RED
        btn.setForeground(Color.BLACK); // CHANGED FROM WHITE TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 35, 51));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON HOVER TOO
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 53, 69));
                btn.setForeground(Color.BLACK); // KEEP BLACK ON EXIT TOO
            }
        });
    }


    public static void main(String[] args) {
        final User dummyUser = new User();
        dummyUser.setUsername("admin");
        dummyUser.setFullName("System Administrator");

        final TransportService dummyService = new TransportService();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard(dummyUser, dummyService).setVisible(true);
            }
        });
    }
}