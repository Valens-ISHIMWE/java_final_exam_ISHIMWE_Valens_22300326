package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.model.TransportData.Vehicle;
import com.transportportal.model.Trip;
import com.transportportal.service.TransportService;
import com.transportportal.config.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Driver Dashboard – Refactored to properly show Vehicle information.
 * Uses Anonymous Inner Classes (No Lambdas).
 */
public class DriverDashboard extends JFrame {

    private TransportService service;
    private User driver;
    private int driverId;

    private JTable tripTable;
    private DefaultTableModel tripModel;
    private JLabel lblAssignedVehicle;
    private JTabbedPane tabs;
    
    // UI components for the Vehicle Panel
    private JPanel vehicleInfoPanel;
    private JLabel lblVehModel, lblVehPlate, lblVehCapacity, lblVehType;

    public DriverDashboard(User driver, TransportService service) {
        this.driver = driver;
        this.service = service;
        this.driverId = findDriverIdByUserId(driver.getId());
        
        setTitle("Driver Dashboard - " + driver.getFullName());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 240, 240));
        
        initialize();
        setupMenuBar();
        loadAssignedVehicle();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { handleLogout(); }
        });
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        });

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem profileItem = new JMenuItem("My Profile");
        JMenuItem vehicleItem = new JMenuItem("My Vehicle");

        profileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { tabs.setSelectedIndex(0); }
        });
        vehicleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { tabs.setSelectedIndex(1); }
        });

        editMenu.add(profileItem);
        editMenu.add(vehicleItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    private int findDriverIdByUserId(int userId) {
        String sql = "SELECT id FROM drivers WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return -1;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("My Profile", createProfilePanel());
        tabs.addTab("My Vehicle", createVehiclePanel());
        tabs.addTab("My Trips", createTripPanel());
        tabs.addTab("Trip Bookings", createTripBookingPanel());
        tabs.addTab("History", createHistoryPanel());

        add(tabs, BorderLayout.CENTER);
        setupFooter();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 60, 60));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Driver Dashboard - " + driver.getFullName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        lblAssignedVehicle = new JLabel("Loading vehicle...");
        lblAssignedVehicle.setForeground(Color.YELLOW);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(lblAssignedVehicle, BorderLayout.EAST);
        return headerPanel;
    }

    // --- UPDATED VEHICLE PANEL ---
    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        vehicleInfoPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        vehicleInfoPanel.setBackground(Color.WHITE);
        vehicleInfoPanel.setBorder(BorderFactory.createTitledBorder("Assigned Vehicle Details"));

        lblVehPlate = new JLabel("---");
        lblVehModel = new JLabel("---");
        lblVehType = new JLabel("---");
        lblVehCapacity = new JLabel("---");

        vehicleInfoPanel.add(new JLabel("Plate Number:")); vehicleInfoPanel.add(lblVehPlate);
        vehicleInfoPanel.add(new JLabel("Model:"));        vehicleInfoPanel.add(lblVehModel);
        vehicleInfoPanel.add(new JLabel("Vehicle Type:"));  vehicleInfoPanel.add(lblVehType);
        vehicleInfoPanel.add(new JLabel("Capacity:"));      vehicleInfoPanel.add(lblVehCapacity);

        panel.add(vehicleInfoPanel, BorderLayout.NORTH);
        
        JButton btnRefresh = new JButton("🔄 Refresh Vehicle Data");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { loadAssignedVehicle(); }
        });
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAssignedVehicle() {
        try {
            Vehicle v = service.getAssignedVehicleForDriver(driverId);
            if (v != null) {
                lblAssignedVehicle.setText("Vehicle: " + v.getIdentifier());
                lblAssignedVehicle.setForeground(Color.GREEN);
                
                // Update the labels in the "My Vehicle" tab
                lblVehPlate.setText(v.getIdentifier());
                lblVehModel.setText(v.getModel()); // Assuming getModel() exists
                lblVehType.setText(v.getType());   // Assuming getType() exists
                lblVehCapacity.setText(String.valueOf(v.getCapacity())); // Assuming getCapacity() exists
            } else {
                lblAssignedVehicle.setText("No vehicle assigned");
                lblAssignedVehicle.setForeground(Color.YELLOW);
                lblVehPlate.setText("None");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnMyTrips = new JButton("📋 My Trips");
        styleButton(btnMyTrips, new Color(173, 216, 230));
        
        btnMyTrips.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DriverTripViewFrame(driver).setVisible(true);
            }
        });
        
        footerPanel.add(btnMyTrips);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Profile Settings Panel"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTripPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Trip ID", "Route", "Date", "Status"};
        tripModel = new DefaultTableModel(cols, 0);
        tripTable = new JTable(tripModel);
        panel.add(new JScrollPane(tripTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTripBookingPanel() { return new JPanel(); }
    private JPanel createHistoryPanel() { return new JPanel(); }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}