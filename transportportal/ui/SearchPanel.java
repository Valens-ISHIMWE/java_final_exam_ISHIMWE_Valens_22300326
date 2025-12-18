package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchPanel extends JPanel {
    private TransportService service;
    private User passenger;
    private DefaultTableModel searchModel;
    private JTable table;
    private List<com.transportportal.model.Trip> availableTrips;
    private BookingPanel bookingPanel;

    // UI Components
    private JTextField txtFrom, txtTo;
    private JButton btnSearch, btnBook, btnClear;

    public SearchPanel(TransportService service, User passenger, BookingPanel bookingPanel) {
        this.service = service;
        this.passenger = passenger;
        this.bookingPanel = bookingPanel;
        this.availableTrips = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245)); // Very light gray background

        // Search criteria panel
        JPanel searchCriteriaPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchCriteriaPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        searchCriteriaPanel.setBackground(Color.WHITE);

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.setBackground(Color.WHITE);
        
        // Make labels more prominent
        JLabel lblFrom = new JLabel("From:");
        lblFrom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblTo = new JLabel("To:");
        lblTo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        locationPanel.add(lblFrom);
        txtFrom = new JTextField(15);
        txtFrom.setBackground(new Color(255, 255, 255));
        txtFrom.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        locationPanel.add(txtFrom);
        locationPanel.add(lblTo);
        txtTo = new JTextField(15);
        txtTo.setBackground(new Color(255, 255, 255));
        txtTo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        locationPanel.add(txtTo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        // SEARCH BUTTON - LIGHT BLUE with BLACK TEXT
        btnSearch = new JButton("🔍 Search Trips");
        styleSearchButton(btnSearch);
        
        // CLEAR BUTTON - LIGHT RED with BLACK TEXT
        btnClear = new JButton("🗑️ Clear Fields");
        styleClearButton(btnClear);

        buttonPanel.add(btnSearch);
        buttonPanel.add(btnClear);

        searchCriteriaPanel.add(locationPanel);
        searchCriteriaPanel.add(buttonPanel);

        // Results table
        searchModel = new DefaultTableModel(new String[]{
            "Route", "Vehicle", "Price (RWF)", "Departure", "Available Seats"
        }, 0);
        
        table = new JTable(searchModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(70, 130, 180)); // Steel blue selection
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Book button panel
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 245, 245));
        
        // BOOK BUTTON - LIGHT GREEN with BLACK TEXT
        btnBook = new JButton("✓ Book Selected Trip");
        styleBookButton(btnBook);
        
        // Make the book button larger and more prominent
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.setPreferredSize(new Dimension(200, 40));
        
        actionPanel.add(btnBook);

        // Layout
        add(searchCriteriaPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Event listeners
        setupEventListeners();
        
        // Load initial trips
        loadAllTrips();
    }

    private void setupEventListeners() {
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchTrips();
            }
        });

        btnBook.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookSelectedTrip();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSearch();
            }
        });
    }

    private void searchTrips() {
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();

        if (from.isEmpty() && to.isEmpty()) {
            loadAllTrips();
            return;
        }

        // For now, we'll filter locally. You can implement database search later
        searchModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        for (com.transportportal.model.Trip trip : availableTrips) {
            String routeName = trip.getRouteName();
            if (routeName != null) {
                boolean matchesFrom = from.isEmpty() || routeName.toLowerCase().contains(from.toLowerCase());
                boolean matchesTo = to.isEmpty() || routeName.toLowerCase().contains(to.toLowerCase());
                
                if (matchesFrom && matchesTo) {
                    searchModel.addRow(new Object[]{
                        trip.getRouteName(),
                        trip.getVehiclePlate(),
                        String.format("%,.0f", trip.getPrice()), // Format price with commas
                        sdf.format(trip.getDepartureTime()),
                        trip.getAvailableSeats()
                    });
                }
            }
        }

        if (searchModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No trips found for the specified criteria.", 
                "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearSearch() {
        txtFrom.setText("");
        txtTo.setText("");
        loadAllTrips();
        JOptionPane.showMessageDialog(this, 
            "Search fields cleared successfully!", 
            "Clear Search", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadAllTrips() {
        // This would normally come from database
        // For now, using sample data that matches your model
        availableTrips.clear();
        
        // Sample trips that match your Trip model structure
        availableTrips.add(new com.transportportal.model.Trip());
        availableTrips.get(0).setRouteName("Kigali - Huye");
        availableTrips.get(0).setVehiclePlate("RAA 123A");
        availableTrips.get(0).setPrice(5000.0);
        availableTrips.get(0).setDepartureTime(new Timestamp(System.currentTimeMillis() + 3600000));
        availableTrips.get(0).setAvailableSeats(15);

        availableTrips.add(new com.transportportal.model.Trip());
        availableTrips.get(1).setRouteName("Kigali - Musanze");
        availableTrips.get(1).setVehiclePlate("RAB 456B");
        availableTrips.get(1).setPrice(7000.0);
        availableTrips.get(1).setDepartureTime(new Timestamp(System.currentTimeMillis() + 7200000));
        availableTrips.get(1).setAvailableSeats(8);

        availableTrips.add(new com.transportportal.model.Trip());
        availableTrips.get(2).setRouteName("Kigali - Rubavu");
        availableTrips.get(2).setVehiclePlate("RAC 789C");
        availableTrips.get(2).setPrice(8000.0);
        availableTrips.get(2).setDepartureTime(new Timestamp(System.currentTimeMillis() + 10800000));
        availableTrips.get(2).setAvailableSeats(12);

        refreshTable();
    }

    private void refreshTable() {
        searchModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        for (com.transportportal.model.Trip trip : availableTrips) {
            searchModel.addRow(new Object[]{
                trip.getRouteName(),
                trip.getVehiclePlate(),
                String.format("%,.0f", trip.getPrice()), // Format price with commas
                sdf.format(trip.getDepartureTime()),
                trip.getAvailableSeats()
            });
        }
    }

    private void bookSelectedTrip() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < availableTrips.size()) {
            com.transportportal.model.Trip selectedTrip = availableTrips.get(row);
            
            if (selectedTrip.getAvailableSeats() <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "No seats available for this trip.", 
                    "Booking Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Show payment dialog
            PaymentDialog paymentDialog = new PaymentDialog(
                (Frame)SwingUtilities.getWindowAncestor(this), 
                selectedTrip, passenger
            );
            paymentDialog.setVisible(true);
            
            if (paymentDialog.isPaymentSuccessful()) {
                // Create booking in database
                try {
                    com.transportportal.model.Booking booking = new com.transportportal.model.Booking();
                    booking.setUserId(passenger.getId());
                    booking.setTripId(selectedTrip.getId());
                    booking.setSeatCount(1); // Default 1 seat
                    booking.setStatus("CONFIRMED");
                    
                    // This would call your service method
                    // service.createBooking(booking);
                    
                    // Update available seats
                    selectedTrip.setAvailableSeats(selectedTrip.getAvailableSeats() - 1);
                    
                    // Refresh tables
                    refreshTable();
                    bookingPanel.refreshBookings();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Trip booked successfully!\n" +
                        "Route: " + selectedTrip.getRouteName() + "\n" +
                        "Vehicle: " + selectedTrip.getVehiclePlate() + "\n" +
                        "Amount: " + String.format("%,.0f", selectedTrip.getPrice()) + " RWF",
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error creating booking: " + ex.getMessage(),
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a trip to book.", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    // SPECIFIC BUTTON STYLING METHODS - ALL WITH BLACK TEXT
    private void styleSearchButton(final JButton btn) {
        btn.setBackground(new Color(173, 216, 230)); // LIGHT BLUE
        btn.setForeground(Color.BLACK); // BLACK TEXT
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(135, 206, 250)); // Brighter blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(173, 216, 230)); // Original light blue
            }
        });
    }

    private void styleBookButton(final JButton btn) {
        btn.setBackground(new Color(144, 238, 144)); // LIGHT GREEN
        btn.setForeground(Color.BLACK); // BLACK TEXT
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 205, 50), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(124, 252, 0)); // Brighter green
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(144, 238, 144)); // Original light green
            }
        });
    }

    private void styleClearButton(final JButton btn) {
        btn.setBackground(new Color(255, 182, 193)); // LIGHT RED/PINK
        btn.setForeground(Color.BLACK); // BLACK TEXT
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 105, 97), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 160, 122)); // Brighter coral
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 182, 193)); // Original light red
            }
        });
    }

    public void refreshTrips() {
        loadAllTrips();
    }
}