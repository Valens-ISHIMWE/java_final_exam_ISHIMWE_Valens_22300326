package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;
import com.transportportal.model.Trip;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * DriverTripViewFrame.java
 * * Shows assigned trips for a driver with manual status updates.
 * Strictly uses Anonymous Inner Classes (No Lambdas).
 */
public class DriverTripViewFrame extends JFrame {

    private TransportService service;
    private User currentUser;
    private int driverId;
    
    private JTable tripsTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnMarkCompleted;

    public DriverTripViewFrame(User user) {
        this.currentUser = user;
        this.service = new TransportService();
        
        setTitle("My Trips - Driver Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 450); // Increased width for better column visibility
        setLocationRelativeTo(null);
        
        // Map User ID to Driver ID using the service
        try {
            this.driverId = service.findDriverIdByUserId(user.getId());
            if (this.driverId == -1) {
                JOptionPane.showMessageDialog(this, "No Driver profile found for this user.", "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }
        
        initUI();
        loadDriverTrips();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header Label
        JLabel lblTitle = new JLabel("Assigned Operational Trips", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(20, 60, 120));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Trip ID", "Route Name", "Vehicle Plate", "Date", "Time", "Status", "Passengers"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent manual editing of cells
            }
        };
        
        tripsTable = new JTable(tableModel);
        tripsTable.setRowHeight(25);
        tripsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tripsTable);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        btnRefresh = new JButton("🔄 Refresh List");
        btnMarkCompleted = new JButton("✅ Mark as Completed");
        
        // Styling buttons
        btnMarkCompleted.setBackground(new Color(40, 167, 69));
        btnMarkCompleted.setForeground(Color.WHITE);
        btnMarkCompleted.setFocusPainted(false);

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnMarkCompleted);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners using Anonymous Inner Classes
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDriverTrips();
            }
        });

        btnMarkCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markTripCompleted();
            }
        });

        getContentPane().add(mainPanel);
    }

    private void loadDriverTrips() {
        try {
            tableModel.setRowCount(0); // Clear current table
            
            List<Trip> trips = service.getTripsByDriver(driverId);
            
            if (trips == null || trips.isEmpty()) {
                // Not showing dialog here to avoid annoyance on empty refresh
                return;
            }
            
            for (int i = 0; i < trips.size(); i++) {
                Trip trip = trips.get(i);
                
                // Get passenger count via service
                int passengerCount = 0;
                try {
                    passengerCount = service.getPassengerCountForTrip(trip.getId());
                } catch (Exception ex) { 
                    /* fallback if method not implemented */ 
                }
                
                tableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getRouteName(),
                    trip.getVehiclePlate(),
                    trip.getDate(),
                    trip.getDepartureTime(),
                    trip.getStatus(),
                    passengerCount
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void markTripCompleted() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get Trip ID from the first column of the selected row
        int tripId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String currentStatus = tableModel.getValueAt(selectedRow, 5).toString();

        if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This trip is already marked as completed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Mark Trip #" + tripId + " as completed?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.updateTripStatus(tripId, "COMPLETED");
                JOptionPane.showMessageDialog(this, "Status updated successfully.");
                loadDriverTrips(); // Reload table
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}