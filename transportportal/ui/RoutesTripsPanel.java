package com.transportportal.ui;

import com.transportportal.model.TransportData.Route;
import com.transportportal.model.TransportData.Trip;
import com.transportportal.model.TransportData.Driver;
import com.transportportal.model.TransportData.Vehicle;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RoutesTripsPanel extends JPanel {
    private TransportService service;
    private DefaultTableModel routesModel;
    private DefaultTableModel tripsModel;
    private JTable routesTable;
    private JTable tripsTable;
    
    private List<com.transportportal.model.Route> routes;
    private List<com.transportportal.model.Trip> trips;
    private List<Driver> drivers;
    private List<Vehicle> vehicles;

    public RoutesTripsPanel(TransportService service) {
        this.service = service;
        this.routes = new ArrayList<com.transportportal.model.Route>();
        this.trips = new ArrayList<com.transportportal.model.Trip>();
        this.drivers = new ArrayList<Driver>();
        this.vehicles = new ArrayList<Vehicle>();
        
        initialize();
        loadDataFromDatabase();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Routes & Trips Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        // Main content panel with tabs
        JTabbedPane contentTabs = new JTabbedPane();
        contentTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Routes Tab
        JPanel routesPanel = createRoutesPanel();
        contentTabs.addTab("🛣️ Routes", routesPanel);

        // Trips Tab
        JPanel tripsPanel = createTripsPanel();
        contentTabs.addTab("🚌 Trips", tripsPanel);

        add(contentTabs, BorderLayout.CENTER);
    }

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Routes table
        routesModel = new DefaultTableModel(new String[]{
            "ID", "Origin", "Destination", "Distance (km)", "Status"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        routesTable = new JTable(routesModel);
        routesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routesTable.setRowHeight(30);
        JScrollPane routesScroll = new JScrollPane(routesTable);
        routesScroll.setBorder(BorderFactory.createTitledBorder("Routes List"));
        panel.add(routesScroll, BorderLayout.CENTER);

        // Routes buttons
        JPanel routesButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        routesButtonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnAddRoute = new JButton("➕ Add Route");
        JButton btnEditRoute = new JButton("✏️ Edit Route");
        JButton btnDeleteRoute = new JButton("🗑️ Delete Route");
        JButton btnViewTrips = new JButton("👁️ View Route Trips");
        JButton btnRefreshRoutes = new JButton("🔄 Refresh");
        
        styleAddButton(btnAddRoute);
        styleEditButton(btnEditRoute);
        styleDeleteButton(btnDeleteRoute);
        styleEditButton(btnViewTrips);
        styleRefreshButton(btnRefreshRoutes);

        routesButtonPanel.add(btnAddRoute);
        routesButtonPanel.add(btnEditRoute);
        routesButtonPanel.add(btnDeleteRoute);
        routesButtonPanel.add(btnViewTrips);
        routesButtonPanel.add(btnRefreshRoutes);

        panel.add(routesButtonPanel, BorderLayout.SOUTH);

        // Add action listeners for routes
        btnAddRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddRouteDialog();
            }
        });

        btnEditRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedRoute();
            }
        });

        btnDeleteRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRoute();
            }
        });

        btnViewTrips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewRouteTrips();
            }
        });

        btnRefreshRoutes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadRoutesData();
            }
        });

        return panel;
    }

    private JPanel createTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Trips table
        tripsModel = new DefaultTableModel(new String[]{
            "ID", "Route", "Driver", "Vehicle", "Departure", "Price (RWF)", "Available Seats", "Status"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tripsTable = new JTable(tripsModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.setRowHeight(30);
        JScrollPane tripsScroll = new JScrollPane(tripsTable);
        tripsScroll.setBorder(BorderFactory.createTitledBorder("Trips Schedule"));
        panel.add(tripsScroll, BorderLayout.CENTER);

        // Trips buttons
        JPanel tripsButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        tripsButtonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnAddTrip = new JButton("➕ Add Trip");
        JButton btnEditTrip = new JButton("✏️ Edit Trip");
        JButton btnDeleteTrip = new JButton("🗑️ Delete Trip");
        JButton btnUpdateStatus = new JButton("🔄 Update Status");
        JButton btnAssignDriver = new JButton("👤 Assign Driver");
        JButton btnRefreshTrips = new JButton("🔄 Refresh");
        
        styleAddButton(btnAddTrip);
        styleEditButton(btnEditTrip);
        styleDeleteButton(btnDeleteTrip);
        styleEditButton(btnUpdateStatus);
        styleEditButton(btnAssignDriver);
        styleRefreshButton(btnRefreshTrips);

        tripsButtonPanel.add(btnAddTrip);
        tripsButtonPanel.add(btnEditTrip);
        tripsButtonPanel.add(btnDeleteTrip);
        tripsButtonPanel.add(btnUpdateStatus);
        tripsButtonPanel.add(btnAssignDriver);
        tripsButtonPanel.add(btnRefreshTrips);

        panel.add(tripsButtonPanel, BorderLayout.SOUTH);

        // Add action listeners for trips
        btnAddTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddTripDialog();
            }
        });

        btnEditTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedTrip();
            }
        });

        btnDeleteTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTrip();
            }
        });

        btnUpdateStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTripStatus();
            }
        });

        btnAssignDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignDriverToTrip();
            }
        });

        btnRefreshTrips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTripsData();
            }
        });

        return panel;
    }

    private void loadDataFromDatabase() {
        loadRoutesData();
        loadDriversAndVehicles(); // Load drivers and vehicles first
        loadTripsData(); // Then load trips
    }

    private void loadRoutesData() {
        try {
            routes = service.findAllRoutes();
            refreshRoutesTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading routes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTripsData() {
        try {
            trips = service.getAllTrips();
            refreshTripsTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadDriversAndVehicles() {
        try {
            drivers = service.getAllDrivers();
            vehicles = service.getAllVehicles();
            
            // Debug: Check if data is loaded
            System.out.println("Loaded " + (drivers != null ? drivers.size() : 0) + " drivers");
            System.out.println("Loaded " + (vehicles != null ? vehicles.size() : 0) + " vehicles");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading drivers/vehicles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Initialize empty lists to avoid null pointers
            if (drivers == null) drivers = new ArrayList<Driver>();
            if (vehicles == null) vehicles = new ArrayList<Vehicle>();
        }
    }

    private void refreshRoutesTable() {
        routesModel.setRowCount(0);
        if (routes != null) {
            for (com.transportportal.model.Route route : routes) {
                routesModel.addRow(new Object[]{
                    route.getId(),
                    route.getOrigin(),
                    route.getDestination(),
                    route.getDistanceKm(),
                    "Active"
                });
            }
        }
    }

    private void refreshTripsTable() {
        tripsModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        if (trips != null) {
            for (com.transportportal.model.Trip trip : trips) {
                String driverName = "Not Assigned";
                String vehicleInfo = "Not Assigned";
                
                // Get driver name - with null safety
                if (trip.getDriverId() > 0 && drivers != null) {
                    Driver driver = findDriverById(trip.getDriverId());
                    if (driver != null) {
                        driverName = driver.getName();
                    }
                }
                
                // Get vehicle info - with null safety
                if (trip.getVehicleId() > 0 && vehicles != null) {
                    Vehicle vehicle = findVehicleById(trip.getVehicleId());
                    if (vehicle != null) {
                        vehicleInfo = vehicle.getIdentifier() + " (" + vehicle.getName() + ")";
                    }
                }
                
                String routeName = "Route " + trip.getRouteId(); // You might want to get actual route name
                
                tripsModel.addRow(new Object[]{
                    trip.getId(),
                    routeName,
                    driverName,
                    vehicleInfo,
                    sdf.format(trip.getDepartureTime()),
                    String.format("%,.0f", trip.getPrice()),
                    trip.getAvailableSeats(),
                    trip.getStatus()
                });
            }
        }
    }

    private void showAddRouteDialog() {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Route", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtOrigin = new JTextField();
        final JTextField txtDestination = new JTextField();
        final JTextField txtDistance = new JTextField();

        formPanel.add(new JLabel("Origin:"));
        formPanel.add(txtOrigin);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(txtDestination);
        formPanel.add(new JLabel("Distance (km):"));
        formPanel.add(txtDistance);

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
                String origin = txtOrigin.getText().trim();
                String destination = txtDestination.getText().trim();
                String distanceStr = txtDistance.getText().trim();

                if (origin.isEmpty() || destination.isEmpty() || distanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double distance = Double.parseDouble(distanceStr);
                    if (distance <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Distance must be greater than 0.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Create new route and save to database
                    com.transportportal.model.Route newRoute = new com.transportportal.model.Route();
                    newRoute.setOrigin(origin);
                    newRoute.setDestination(destination);
                    newRoute.setDistanceKm(distance);

                    int routeId = service.addRoute(newRoute);
                    if (routeId > 0) {
                        JOptionPane.showMessageDialog(dialog, "Route added successfully! ID: " + routeId);
                        dialog.dispose();
                        loadRoutesData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add route.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid number for distance.", "Validation", JOptionPane.WARNING_MESSAGE);
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

    private void editSelectedRoute() {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a route to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = (Integer) routesModel.getValueAt(selectedRow, 0);
        final com.transportportal.model.Route route = findRouteById(routeId);
        
        if (route == null) {
            JOptionPane.showMessageDialog(this, "Route not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit route functionality would require additional database methods.", "Edit Route", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedRoute() {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a route to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = (Integer) routesModel.getValueAt(selectedRow, 0);
        String routeInfo = routesModel.getValueAt(selectedRow, 1) + " - " + routesModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete route: " + routeInfo + "?\nThis will also delete all associated trips.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // You would need to implement deleteRoute method in TransportService
                JOptionPane.showMessageDialog(this, "Route deletion would require additional database methods.", "Delete Route", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting route: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewRouteTrips() {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a route to view trips.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = (Integer) routesModel.getValueAt(selectedRow, 0);
        String routeInfo = routesModel.getValueAt(selectedRow, 1) + " - " + routesModel.getValueAt(selectedRow, 2);
        
        try {
            List<com.transportportal.model.Trip> routeTrips = service.findTripsByRoute(routeId);
            
            if (routeTrips.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No trips found for route: " + routeInfo, "No Trips", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Show trips in a dialog
            StringBuilder tripsInfo = new StringBuilder();
            tripsInfo.append("Trips for Route: ").append(routeInfo).append("\n\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            for (com.transportportal.model.Trip trip : routeTrips) {
                String driverName = "Not Assigned";
                if (trip.getDriverId() > 0 && drivers != null) {
                    Driver driver = findDriverById(trip.getDriverId());
                    if (driver != null) {
                        driverName = driver.getName();
                    }
                }
                
                tripsInfo.append("• ").append(sdf.format(trip.getDepartureTime()))
                         .append(" - Driver: ").append(driverName)
                         .append(" - ").append(String.format("%,.0f", trip.getPrice())).append(" RWF")
                         .append(" - ").append(trip.getStatus()).append("\n");
            }

            JTextArea textArea = new JTextArea(tripsInfo.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Route Trips", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading route trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddTripDialog() {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Trip", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JComboBox<String> cmbRoute = new JComboBox<String>();
        final JComboBox<String> cmbDriver = new JComboBox<String>();
        final JComboBox<String> cmbVehicle = new JComboBox<String>();
        final JTextField txtDepartureDate = new JTextField();
        final JTextField txtDepartureTime = new JTextField();
        final JTextField txtPrice = new JTextField();
        final JTextField txtSeats = new JTextField();

        // Populate comboboxes with null safety
        if (routes != null) {
            for (com.transportportal.model.Route route : routes) {
                cmbRoute.addItem(route.getOrigin() + " - " + route.getDestination() + " (ID: " + route.getId() + ")");
            }
        }
        
        cmbDriver.addItem("Not Assigned");
        if (drivers != null) {
            for (Driver driver : drivers) {
                if ("ACTIVE".equals(driver.getStatus())) {
                    cmbDriver.addItem(driver.getName() + " (License: " + driver.getLicenseNumber() + ")");
                }
            }
        }
        
        cmbVehicle.addItem("Not Assigned");
        if (vehicles != null) {
            for (Vehicle vehicle : vehicles) {
                cmbVehicle.addItem(vehicle.getIdentifier() + " - " + vehicle.getName() + " (" + vehicle.getCapacity() + " seats)");
            }
        }

        // Set default values
        txtDepartureDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtDepartureTime.setText(new SimpleDateFormat("HH:mm").format(new java.util.Date()));
        txtPrice.setText("5000");
        txtSeats.setText("15");

        formPanel.add(new JLabel("Route:"));
        formPanel.add(cmbRoute);
        formPanel.add(new JLabel("Driver:"));
        formPanel.add(cmbDriver);
        formPanel.add(new JLabel("Vehicle:"));
        formPanel.add(cmbVehicle);
        formPanel.add(new JLabel("Departure Date (yyyy-mm-dd):"));
        formPanel.add(txtDepartureDate);
        formPanel.add(new JLabel("Departure Time (HH:mm):"));
        formPanel.add(txtDepartureTime);
        formPanel.add(new JLabel("Price (RWF):"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Available Seats:"));
        formPanel.add(txtSeats);

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
                try {
                    // Parse route ID
                    String routeSelection = (String) cmbRoute.getSelectedItem();
                    int routeId = Integer.parseInt(routeSelection.substring(routeSelection.lastIndexOf("ID: ") + 4, routeSelection.length() - 1));

                    // Parse driver ID
                    int driverId = 0;
                    if (cmbDriver.getSelectedIndex() > 0 && drivers != null) {
                        String driverSelection = (String) cmbDriver.getSelectedItem();
                        // Extract driver ID from selection (you might need to store IDs differently)
                        Driver selectedDriver = findDriverByName(driverSelection.substring(0, driverSelection.indexOf(" (License:")));
                        if (selectedDriver != null) {
                            driverId = selectedDriver.getDriverID();
                        }
                    }

                    // Parse vehicle ID
                    int vehicleId = 0;
                    if (cmbVehicle.getSelectedIndex() > 0 && vehicles != null) {
                        String vehicleSelection = (String) cmbVehicle.getSelectedItem();
                        // Extract vehicle ID from selection
                        Vehicle selectedVehicle = findVehicleByIdentifier(vehicleSelection.substring(0, vehicleSelection.indexOf(" - ")));
                        if (selectedVehicle != null) {
                            vehicleId = selectedVehicle.getVehicleID();
                        }
                    }

                    String date = txtDepartureDate.getText().trim();
                    String time = txtDepartureTime.getText().trim();
                    String priceStr = txtPrice.getText().trim();
                    String seatsStr = txtSeats.getText().trim();

                    if (routeId <= 0 || date.isEmpty() || time.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Route, date, time, price and seats are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    int seats = Integer.parseInt(seatsStr);
                    
                    if (price <= 0 || seats <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Price and seats must be greater than 0.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Create departure timestamp
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    java.util.Date departureDate = dateFormat.parse(date + " " + time);
                    Timestamp departureTime = new Timestamp(departureDate.getTime());

                    // Create trip object using the correct Trip class
                    com.transportportal.model.Trip newTrip = new com.transportportal.model.Trip();
                    newTrip.setRouteId(routeId);
                    newTrip.setDriverId(driverId);
                    newTrip.setVehicleId(vehicleId);
                    newTrip.setDepartureTime(departureTime);
                    newTrip.setPrice(price);
                    newTrip.setAvailableSeats(seats);
                    newTrip.setStatus("SCHEDULED");

                    // Save to database
                    int tripId = service.addTrip(newTrip);
                    if (tripId > 0) {
                        JOptionPane.showMessageDialog(dialog, "Trip added successfully! Trip ID: " + tripId);
                        dialog.dispose();
                        loadTripsData();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add trip.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void assignDriverToTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trip to assign driver.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tripId = (Integer) tripsModel.getValueAt(selectedRow, 0);
        com.transportportal.model.Trip trip = findTripById(tripId);
        
        if (trip == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show driver selection dialog
        JComboBox<String> driverCombo = new JComboBox<String>();
        driverCombo.addItem("Not Assigned");
        if (drivers != null) {
            for (Driver driver : drivers) {
                if ("ACTIVE".equals(driver.getStatus())) {
                    driverCombo.addItem(driver.getName() + " (License: " + driver.getLicenseNumber() + ")");
                }
            }
        }

        int result = JOptionPane.showConfirmDialog(this,
            new Object[]{"Select driver for trip:", driverCombo},
            "Assign Driver to Trip",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int driverId = 0;
                if (driverCombo.getSelectedIndex() > 0 && drivers != null) {
                    String driverSelection = (String) driverCombo.getSelectedItem();
                    Driver selectedDriver = findDriverByName(driverSelection.substring(0, driverSelection.indexOf(" (License:")));
                    if (selectedDriver != null) {
                        driverId = selectedDriver.getDriverID();
                    }
                }

                // Update trip with driver assignment
                trip.setDriverId(driverId);
                // You would need to implement updateTrip method in TransportService
                JOptionPane.showMessageDialog(this, "Driver assignment would require trip update method.", "Assign Driver", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error assigning driver: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trip to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tripId = (Integer) tripsModel.getValueAt(selectedRow, 0);
        com.transportportal.model.Trip trip = findTripById(tripId);
        
        if (trip == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit trip functionality would require additional database methods.", "Edit Trip", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trip to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tripId = (Integer) tripsModel.getValueAt(selectedRow, 0);
        String tripInfo = tripsModel.getValueAt(selectedRow, 1) + " - " + tripsModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete trip: " + tripInfo + "?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // You would need to implement deleteTrip method in TransportService
                JOptionPane.showMessageDialog(this, "Trip deletion would require additional database methods.", "Delete Trip", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTripStatus() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trip to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tripId = (Integer) tripsModel.getValueAt(selectedRow, 0);
        com.transportportal.model.Trip trip = findTripById(tripId);
        
        if (trip == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] statusOptions = {"SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for trip:",
            "Update Trip Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            trip.getStatus());

        if (newStatus != null && !newStatus.equals(trip.getStatus())) {
            try {
                service.updateTripStatus(tripId, newStatus);
                JOptionPane.showMessageDialog(this, "Trip status updated to: " + newStatus);
                loadTripsData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating trip status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper methods with null safety
    private com.transportportal.model.Route findRouteById(int id) {
        if (routes != null) {
            for (com.transportportal.model.Route route : routes) {
                if (route.getId() == id) {
                    return route;
                }
            }
        }
        return null;
    }

    private com.transportportal.model.Trip findTripById(int id) {
        if (trips != null) {
            for (com.transportportal.model.Trip trip : trips) {
                if (trip.getId() == id) {
                    return trip;
                }
            }
        }
        return null;
    }

    private Driver findDriverById(int id) {
        if (drivers != null) {
            for (Driver driver : drivers) {
                if (driver.getDriverID() == id) {
                    return driver;
                }
            }
        }
        return null;
    }

    private Driver findDriverByName(String name) {
        if (drivers != null) {
            for (Driver driver : drivers) {
                if (driver.getName().equals(name)) {
                    return driver;
                }
            }
        }
        return null;
    }

    private Vehicle findVehicleById(int id) {
        if (vehicles != null) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getVehicleID() == id) {
                    return vehicle;
                }
            }
        }
        return null;
    }

    private Vehicle findVehicleByIdentifier(String identifier) {
        if (vehicles != null) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getIdentifier().equals(identifier)) {
                    return vehicle;
                }
            }
        }
        return null;
    }

    // BUTTON STYLING METHODS (keep the same as before)
    private void styleAddButton(final JButton btn) {
        btn.setBackground(new Color(40, 167, 69));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 136, 56));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.BLACK);
            }
        });
    }

    private void styleEditButton(final JButton btn) {
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
                btn.setForeground(Color.BLACK);
            }
        });
    }

    private void styleDeleteButton(final JButton btn) {
        btn.setBackground(new Color(220, 53, 69));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 35, 51));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 53, 69));
                btn.setForeground(Color.BLACK);
            }
        });
    }

    private void styleRefreshButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK);
            }
        });
    }

    private void styleCancelButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK);
            }
        });
    }
}