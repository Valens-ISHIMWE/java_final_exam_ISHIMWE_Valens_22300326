package com.transportportal.ui;

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
import java.util.ArrayList;
import java.util.List;

public class DriverVehiclePanel extends JPanel {
    private TransportService service;
    private DefaultTableModel driverModel;
    private DefaultTableModel vehicleModel;
    private JTable driverTable;
    private JTable vehicleTable;
    
    // Remove sample data lists - we'll use database directly
    // private List<Driver> drivers = new ArrayList<Driver>();
    // private List<Vehicle> vehicles = new ArrayList<Vehicle>();

    public DriverVehiclePanel(TransportService service) {
        this.service = service;
        initialize();
        loadDataFromDatabase();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Driver & Vehicle Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        // Tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setBackground(new Color(245, 245, 245));

        // Drivers table
        driverModel = new DefaultTableModel(new String[]{"ID", "Name", "License No", "Contact", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        driverTable = new JTable(driverModel);
        driverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driverTable.setRowHeight(30);
        JScrollPane driverScroll = new JScrollPane(driverTable);
        driverScroll.setBorder(BorderFactory.createTitledBorder("Drivers List"));
        tablesPanel.add(driverScroll);

        // Vehicles table
        vehicleModel = new DefaultTableModel(new String[]{"ID", "Plate No", "Model", "Capacity", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehicleTable = new JTable(vehicleModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setRowHeight(30);
        JScrollPane vehicleScroll = new JScrollPane(vehicleTable);
        vehicleScroll.setBorder(BorderFactory.createTitledBorder("Vehicles List"));
        tablesPanel.add(vehicleScroll);

        add(tablesPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnAddDriver = new JButton("➕ Add Driver");
        JButton btnEditDriver = new JButton("✏️ Edit Driver");
        JButton btnDeleteDriver = new JButton("🗑️ Delete Driver");
        
        JButton btnAddVehicle = new JButton("➕ Add Vehicle");
        JButton btnEditVehicle = new JButton("✏️ Edit Vehicle");
        JButton btnDeleteVehicle = new JButton("🗑️ Delete Vehicle");
        
        JButton btnAssign = new JButton("🔗 Assign Driver to Vehicle");
        JButton btnRefresh = new JButton("🔄 Refresh");

        // Style buttons with BLACK TEXT
        styleAddButton(btnAddDriver);
        styleEditButton(btnEditDriver);
        styleDeleteButton(btnDeleteDriver);
        
        styleAddButton(btnAddVehicle);
        styleEditButton(btnEditVehicle);
        styleDeleteButton(btnDeleteVehicle);
        
        styleEditButton(btnAssign);
        styleRefreshButton(btnRefresh);

        // Add buttons to panel
        buttonPanel.add(btnAddDriver);
        buttonPanel.add(btnEditDriver);
        buttonPanel.add(btnDeleteDriver);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(btnAddVehicle);
        buttonPanel.add(btnEditVehicle);
        buttonPanel.add(btnDeleteVehicle);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(btnAssign);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        btnAddDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddDriverDialog();
            }
        });

        btnEditDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedDriver();
            }
        });

        btnDeleteDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedDriver();
            }
        });

        btnAddVehicle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddVehicleDialog();
            }
        });

        btnEditVehicle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedVehicle();
            }
        });

        btnDeleteVehicle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedVehicle();
            }
        });

        btnAssign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignDriverToVehicle();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }

    private void loadDataFromDatabase() {
        try {
            // Load drivers from database
            List<com.transportportal.model.TransportData.Driver> drivers = service.getAllDrivers();
            
            // Load vehicles from database  
            List<com.transportportal.model.TransportData.Vehicle> vehicles = service.getAllVehicles();
            
            refreshTables(drivers, vehicles);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage() + 
                "\nPlease check database connection and tables.", 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data from database: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshTables(List<Driver> drivers, List<Vehicle> vehicles) {
        // Refresh drivers table
        driverModel.setRowCount(0);
        for (Driver driver : drivers) {
            driverModel.addRow(new Object[]{
                driver.getDriverID(),
                driver.getName(),
                driver.getLicenseNumber(),
                driver.getContact(),
                driver.getStatus()
            });
        }

        // Refresh vehicles table
        vehicleModel.setRowCount(0);
        for (Vehicle vehicle : vehicles) {
            vehicleModel.addRow(new Object[]{
                vehicle.getVehicleID(),
                vehicle.getIdentifier(),
                vehicle.getName(),
                vehicle.getCapacity() + " seats",
                vehicle.getStatus()
            });
        }
    }

    private void refreshData() {
        loadDataFromDatabase();
        JOptionPane.showMessageDialog(this, "Data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddDriverDialog() {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Driver", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtName = new JTextField();
        final JTextField txtLicense = new JTextField();
        final JTextField txtContact = new JTextField();
        final JTextField txtExperience = new JTextField();

        formPanel.add(new JLabel("Driver Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("License Number:"));
        formPanel.add(txtLicense);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Experience (years):"));
        formPanel.add(txtExperience);

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
                String name = txtName.getText().trim();
                String license = txtLicense.getText().trim();
                String contact = txtContact.getText().trim();
                String experienceStr = txtExperience.getText().trim();

                if (name.isEmpty() || license.isEmpty() || contact.isEmpty() || experienceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int experience = Integer.parseInt(experienceStr);
                    
                    // Create new driver object
                    com.transportportal.model.TransportData.Driver newDriver = new com.transportportal.model.TransportData.Driver();
                    newDriver.setName(name);
                    newDriver.setLicenseNumber(license);
                    newDriver.setContact(contact);
                    newDriver.setExperienceYears(experience);
                    newDriver.setStatus("ACTIVE");
                    
                    // Save to database using the new method
                    int driverId = service.addDriverWithUser(newDriver);
                    
                    if (driverId > 0) {
                        JOptionPane.showMessageDialog(dialog, "Driver added successfully with ID: " + driverId);
                        dialog.dispose();
                        loadDataFromDatabase(); // Reload from database
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add driver to database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Experience must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Database error: " + ex.getMessage() + 
                        "\nPlease check if database tables exist.", 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving driver: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void editSelectedDriver() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a driver to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int driverId = (Integer) driverModel.getValueAt(selectedRow, 0);
        
        try {
            List<Driver> drivers = service.getAllDrivers();
            final Driver driver = findDriverById(drivers, driverId);
            
            if (driver == null) {
                JOptionPane.showMessageDialog(this, "Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Driver", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            final JTextField txtName = new JTextField(driver.getName());
            final JTextField txtLicense = new JTextField(driver.getLicenseNumber());
            final JTextField txtContact = new JTextField(driver.getContact());
            final JTextField txtExperience = new JTextField(String.valueOf(driver.getExperienceYears()));

            formPanel.add(new JLabel("Driver Name:"));
            formPanel.add(txtName);
            formPanel.add(new JLabel("License Number:"));
            formPanel.add(txtLicense);
            formPanel.add(new JLabel("Contact:"));
            formPanel.add(txtContact);
            formPanel.add(new JLabel("Experience (years):"));
            formPanel.add(txtExperience);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton btnUpdate = new JButton("Update");
            JButton btnCancel = new JButton("Cancel");
            
            styleEditButton(btnUpdate);
            styleCancelButton(btnCancel);

            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnCancel);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            btnUpdate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String name = txtName.getText().trim();
                    String license = txtLicense.getText().trim();
                    String contact = txtContact.getText().trim();
                    String experienceStr = txtExperience.getText().trim();

                    if (name.isEmpty() || license.isEmpty() || contact.isEmpty() || experienceStr.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try {
                        int experience = Integer.parseInt(experienceStr);
                        
                        // Update driver
                        driver.setName(name);
                        driver.setLicenseNumber(license);
                        driver.setContact(contact);
                        driver.setExperienceYears(experience);
                        
                        // Save to database
                        Driver updatedDriver = service.saveDriver(driver);
                        
                        JOptionPane.showMessageDialog(dialog, "Driver updated successfully!");
                        dialog.dispose();
                        loadDataFromDatabase(); // Reload from database
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Experience must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Error updating driver: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading driver: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedDriver() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a driver to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int driverId = (Integer) driverModel.getValueAt(selectedRow, 0);
        String driverName = (String) driverModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete driver: " + driverName + "?\nThis action cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteDriver(driverId);
                JOptionPane.showMessageDialog(this, "Driver deleted successfully!");
                loadDataFromDatabase(); // Reload from database
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting driver: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showAddVehicleDialog() {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Vehicle", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        final JTextField txtPlateNo = new JTextField();
        final JTextField txtModel = new JTextField();
        final JTextField txtCapacity = new JTextField();

        formPanel.add(new JLabel("Plate Number:"));
        formPanel.add(txtPlateNo);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(txtModel);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(txtCapacity);

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
                String plateNo = txtPlateNo.getText().trim();
                String model = txtModel.getText().trim();
                String capacityStr = txtCapacity.getText().trim();

                if (plateNo.isEmpty() || model.isEmpty() || capacityStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int capacity = Integer.parseInt(capacityStr);
                    
                    // Create new vehicle object
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setIdentifier(plateNo);
                    newVehicle.setName(model);
                    newVehicle.setCapacity(capacity);
                    newVehicle.setStatus("Available");
                    
                    // Save to database
                    Vehicle savedVehicle = service.saveVehicle(newVehicle);
                    
                    if (savedVehicle.getVehicleID() > 0) {
                        JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!");
                        dialog.dispose();
                        loadDataFromDatabase(); // Reload from database
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to add vehicle to database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Capacity must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving vehicle: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void editSelectedVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int vehicleId = (Integer) vehicleModel.getValueAt(selectedRow, 0);
        
        try {
            List<Vehicle> vehicles = service.getAllVehicles();
            final Vehicle vehicle = findVehicleById(vehicles, vehicleId);
            
            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Vehicle not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Vehicle", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            final JTextField txtPlateNo = new JTextField(vehicle.getIdentifier());
            final JTextField txtModel = new JTextField(vehicle.getName());
            final JTextField txtCapacity = new JTextField(String.valueOf(vehicle.getCapacity()));

            formPanel.add(new JLabel("Plate Number:"));
            formPanel.add(txtPlateNo);
            formPanel.add(new JLabel("Model:"));
            formPanel.add(txtModel);
            formPanel.add(new JLabel("Capacity:"));
            formPanel.add(txtCapacity);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton btnUpdate = new JButton("Update");
            JButton btnCancel = new JButton("Cancel");
            
            styleEditButton(btnUpdate);
            styleCancelButton(btnCancel);

            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnCancel);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            btnUpdate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String plateNo = txtPlateNo.getText().trim();
                    String model = txtModel.getText().trim();
                    String capacityStr = txtCapacity.getText().trim();

                    if (plateNo.isEmpty() || model.isEmpty() || capacityStr.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try {
                        int capacity = Integer.parseInt(capacityStr);
                        
                        // Update vehicle
                        vehicle.setIdentifier(plateNo);
                        vehicle.setName(model);
                        vehicle.setCapacity(capacity);
                        
                        // Save to database
                        Vehicle updatedVehicle = service.saveVehicle(vehicle);
                        
                        JOptionPane.showMessageDialog(dialog, "Vehicle updated successfully!");
                        dialog.dispose();
                        loadDataFromDatabase(); // Reload from database
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Capacity must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Error updating vehicle: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int vehicleId = (Integer) vehicleModel.getValueAt(selectedRow, 0);
        String vehiclePlate = (String) vehicleModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete vehicle: " + vehiclePlate + "?\nThis action cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteVehicle(vehicleId);
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
                loadDataFromDatabase(); // Reload from database
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting vehicle: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

   private void assignDriverToVehicle() {
        int driverRow = driverTable.getSelectedRow();
        int vehicleRow = vehicleTable.getSelectedRow();
        
        if (driverRow < 0 || vehicleRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select both a driver and a vehicle.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int driverId = (Integer) driverModel.getValueAt(driverRow, 0);
        int vehicleId = (Integer) vehicleModel.getValueAt(vehicleRow, 0);
        
        String driverName = (String) driverModel.getValueAt(driverRow, 1);
        String vehiclePlate = (String) vehicleModel.getValueAt(vehicleRow, 1);
        String vehicleModelName = (String) vehicleModel.getValueAt(vehicleRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Assign driver " + driverName + " to vehicle " + vehiclePlate + " (" + vehicleModelName + ")?\n\n" +
            "This will automatically unassign any previous assignments for this driver and vehicle.",
            "Confirm Assignment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = service.assignDriverToVehicle(driverId, vehicleId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Successfully assigned " + driverName + " to vehicle " + vehiclePlate + "!\n" +
                        "Assignment has been saved to database.",
                        "Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Show current assignments
                    showCurrentAssignments();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to assign driver to vehicle.", 
                        "Assignment Failed", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Database error during assignment: " + e.getMessage(), 
                    "Assignment Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error during assignment: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showCurrentAssignments() {
        try {
            List<Object[]> assignments = service.getCurrentAssignments();
            
            if (assignments.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active driver-vehicle assignments found.", 
                    "Current Assignments", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Current Active Assignments:\n\n");
            
            for (Object[] assignment : assignments) {
                int driverId = (Integer) assignment[0];
                String driverName = (String) assignment[1];
                int vehicleId = (Integer) assignment[2];
                String plateNumber = (String) assignment[3];
                String model = (String) assignment[4];
                Timestamp assignedDate = (Timestamp) assignment[5];
                
                sb.append("• Driver: ").append(driverName)
                  .append(" (ID: ").append(driverId).append(")\n")
                  .append("  Vehicle: ").append(plateNumber)
                  .append(" - ").append(model)
                  .append(" (ID: ").append(vehicleId).append(")\n")
                  .append("  Assigned: ").append(assignedDate).append("\n\n");
            }
            
            JOptionPane.showMessageDialog(this, 
                sb.toString(), 
                "Current Assignments", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading assignments: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Helper methods
    private Driver findDriverById(List<Driver> drivers, int id) {
        for (Driver driver : drivers) {
            if (driver.getDriverID() == id) {
                return driver;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(List<Vehicle> vehicles, int id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleID() == id) {
                return vehicle;
            }
        }
        return null;
    }

    // BUTTON STYLING METHODS - ALL WITH BLACK TEXT
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
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.BLACK);
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
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
                btn.setForeground(Color.BLACK);
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
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 53, 69));
                btn.setForeground(Color.BLACK);
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
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK);
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
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK);
            }
        });
    }
}