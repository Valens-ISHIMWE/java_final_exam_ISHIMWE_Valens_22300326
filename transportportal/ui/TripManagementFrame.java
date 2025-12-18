package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;
import com.transportportal.model.Route;
import com.transportportal.model.Vehicle;
import com.transportportal.model.Trip;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TripManagementFrame extends JFrame {

    private TransportService service;
    private User currentUser;
    
    private JComboBox<String> cmbRoute;
    private JComboBox<String> cmbDriver;
    private JComboBox<String> cmbVehicle;
    private JTextField txtDepartureTime;
    private JTextField txtArrivalTime;
    private JTextField txtPrice;
    private JButton btnCreateTrip, btnViewTrips;
    private JTable tripsTable;
    private DefaultTableModel tableModel;

    public TripManagementFrame(User user) {
        this.currentUser = user;
        this.service = new TransportService();
        
        setTitle("Trip Management - Transport Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Top panel - Trip creation form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Trip"));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Route selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        cmbRoute = new JComboBox<>();
        formPanel.add(cmbRoute, gbc);

        // Driver selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Driver:"), gbc);
        gbc.gridx = 1;
        cmbDriver = new JComboBox<>();
        formPanel.add(cmbDriver, gbc);

        // Vehicle selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Vehicle:"), gbc);
        gbc.gridx = 1;
        cmbVehicle = new JComboBox<>();
        formPanel.add(cmbVehicle, gbc);

        // Departure time
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Departure Time:"), gbc);
        gbc.gridx = 1;
        txtDepartureTime = new JTextField();
        txtDepartureTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        formPanel.add(txtDepartureTime, gbc);

        // Arrival time
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Arrival Time:"), gbc);
        gbc.gridx = 1;
        txtArrivalTime = new JTextField();
        txtArrivalTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        formPanel.add(txtArrivalTime, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        txtPrice = new JTextField("50.00");
        formPanel.add(txtPrice, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCreateTrip = new JButton("Create Trip");
        btnViewTrips = new JButton("Refresh Trips");
        buttonPanel.add(btnCreateTrip);
        buttonPanel.add(btnViewTrips);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Bottom panel - Trips table
        String[] columns = {"Trip ID", "Route", "Driver", "Vehicle", "Departure", "Arrival", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        tripsTable = new JTable(tableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(tripsTable);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Action listeners
        btnCreateTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createTrip();
            }
        });

        btnViewTrips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTrips();
            }
        });

        getContentPane().add(mainPanel);
    }

    private void loadData() {
        try {
            // Load routes
            cmbRoute.removeAllItems();
            List<Route> routes = service.findAllRoutes();
            for (Route route : routes) {
                cmbRoute.addItem(route.getOrigin() + " - " + route.getDestination() + " (ID: " + route.getId() + ")");
            }

            // Load drivers
            cmbDriver.removeAllItems();
            List<com.transportportal.model.TransportData.Driver> drivers = service.getAllDrivers();
            for (com.transportportal.model.TransportData.Driver driver : drivers) {
                if ("ACTIVE".equals(driver.getStatus())) {
                    cmbDriver.addItem(driver.getName() + " (License: " + driver.getLicenseNumber() + ")");
                }
            }

            // Load vehicles
            cmbVehicle.removeAllItems();
            List<Vehicle> vehicles = service.findAllVehicles();
            for (Vehicle vehicle : vehicles) {
                cmbVehicle.addItem(vehicle.getPlateNumber() + " - " + vehicle.getModel() + " (" + vehicle.getCapacity() + " seats)");
            }

            // Load existing trips
            loadTrips();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTrips() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            // This would need a method to get all trips - let's add it to TransportService
            List<Trip> trips = service.getAllTrips();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (Trip trip : trips) {
                // Get route info
                String routeInfo = "Route " + trip.getRouteId();
                // Get driver info
                String driverInfo = "Driver " + trip.getDriverId();
                // Get vehicle info  
                String vehicleInfo = "Vehicle " + trip.getVehicleId();
                
                tableModel.addRow(new Object[]{
                    trip.getId(),
                    routeInfo,
                    driverInfo,
                    vehicleInfo,
                    dateFormat.format(trip.getDepartureTime()),
                    dateFormat.format(trip.getArrivalTime()),
                    String.format("$%.2f", trip.getPrice()),
                    "SCHEDULED"
                });
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void createTrip() {
        try {
            // Validate inputs
            if (cmbRoute.getSelectedIndex() == -1 || cmbDriver.getSelectedIndex() == -1 || 
                cmbVehicle.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select route, driver, and vehicle.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse route ID from selection
            String routeSelection = (String) cmbRoute.getSelectedItem();
            int routeId = Integer.parseInt(routeSelection.substring(routeSelection.lastIndexOf("ID: ") + 4, routeSelection.length() - 1));

            // Parse driver ID (this is a simplification - you'd need to store driver IDs properly)
            int driverId = cmbDriver.getSelectedIndex() + 1; // Temporary - you need proper driver ID mapping

            // Parse vehicle ID (this is a simplification - you'd need to store vehicle IDs properly)
            int vehicleId = cmbVehicle.getSelectedIndex() + 1; // Temporary - you need proper vehicle ID mapping

            // Parse dates and convert to Timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date departureDate = dateFormat.parse(txtDepartureTime.getText());
            Date arrivalDate = dateFormat.parse(txtArrivalTime.getText());
            
            // Convert Date to Timestamp
            Timestamp departureTime = new Timestamp(departureDate.getTime());
            Timestamp arrivalTime = new Timestamp(arrivalDate.getTime());

            // Parse price
            double price = Double.parseDouble(txtPrice.getText());

            // Create trip object
            Trip trip = new Trip();
            trip.setRouteId(routeId);
            trip.setDriverId(driverId);
            trip.setVehicleId(vehicleId);
            trip.setDepartureTime(departureTime);  // Now passing Timestamp instead of Date
            trip.setArrivalTime(arrivalTime);      // Now passing Timestamp instead of Date
            trip.setPrice(price);

            // Save trip
            int tripId = service.addTrip(trip);
            if (tripId > 0) {
                JOptionPane.showMessageDialog(this, "Trip created successfully! Trip ID: " + tripId, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTrips();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create trip.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtDepartureTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        txtArrivalTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        txtPrice.setText("50.00");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // For testing only
                User testUser = new User();
                testUser.setRole("ADMIN");
                new TripManagementFrame(testUser).setVisible(true);
            }
        });
    }
}