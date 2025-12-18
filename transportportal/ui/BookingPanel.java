package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingPanel extends JPanel {
    private TransportService service;
    private User passenger;
    private DefaultTableModel bookingModel;
    private JTable table;
    private List<com.transportportal.model.Booking> myBookings;

    // UI Components
    private JButton btnCancel, btnRefresh;

    public BookingPanel(TransportService service, User passenger) {
        this.service = service;
        this.passenger = passenger;
        this.myBookings = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // REFRESH BUTTON - LIGHT BLUE with BLACK TEXT
        btnRefresh = new JButton("🔄 Refresh");
        styleRefreshButton(btnRefresh);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Table
        bookingModel = new DefaultTableModel(new String[]{
            "Booking ID", "Route", "Vehicle", "Seats", "Price", "Status", "Booked On"
        }, 0);
        
        table = new JTable(bookingModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(70, 130, 180)); // Steel blue selection
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Action buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 245, 245));
        
        // CANCEL BUTTON - LIGHT RED with BLACK TEXT
        btnCancel = new JButton("❌ Cancel Booking");
        styleCancelButton(btnCancel);
        actionPanel.add(btnCancel);

        // Layout
        add(headerPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Event listeners
        setupEventListeners();
        
        // Load initial bookings
        refreshBookings();
    }

    private void setupEventListeners() {
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshBookings();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
            }
        });
    }

    public void refreshBookings() {
        // This would normally load from database using service.findBookingsByUser(passenger.getId())
        // For now, using sample data
        
        myBookings.clear();
        
        // Sample bookings
        com.transportportal.model.Booking booking1 = new com.transportportal.model.Booking();
        booking1.setId(1);
        booking1.setTripId(101);
        booking1.setSeatCount(2);
        booking1.setStatus("CONFIRMED");
        myBookings.add(booking1);

        com.transportportal.model.Booking booking2 = new com.transportportal.model.Booking();
        booking2.setId(2);
        booking2.setTripId(102);
        booking2.setSeatCount(1);
        booking2.setStatus("CONFIRMED");
        myBookings.add(booking2);

        refreshTable();
    }

    private void refreshTable() {
        bookingModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        
        for (com.transportportal.model.Booking booking : myBookings) {
            // In real implementation, you'd join with trips table to get trip details
            String routeName = "Kigali - Huye"; // This would come from joined query
            String vehiclePlate = "RAA 123A"; // This would come from joined query
            double price = 5000.0; // This would come from joined query
            
            bookingModel.addRow(new Object[]{
                booking.getId(),
                routeName,
                vehiclePlate,
                booking.getSeatCount(),
                String.format("%,.0f", price * booking.getSeatCount()) + " RWF",
                booking.getStatus(),
                sdf.format(new java.util.Date())
            });
        }

        if (bookingModel.getRowCount() == 0) {
            bookingModel.addRow(new Object[]{
                "No bookings found", "", "", "", "", "", ""
            });
        }
    }

    private void cancelBooking() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < myBookings.size()) {
            com.transportportal.model.Booking booking = myBookings.get(row);
            
            if (!"CONFIRMED".equals(booking.getStatus())) {
                JOptionPane.showMessageDialog(this, 
                    "Only confirmed bookings can be cancelled.", 
                    "Cancellation Not Allowed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking #" + booking.getId() + "?\n" +
                "This action cannot be undone.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // This would call your service method
                    // service.cancelBooking(booking.getId());
                    
                    booking.setStatus("CANCELLED");
                    refreshTable();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Booking #" + booking.getId() + " cancelled successfully!",
                        "Cancellation Confirmed", JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error cancelling booking: " + ex.getMessage(),
                        "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel.", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    // SPECIFIC BUTTON STYLING METHODS - ALL WITH BLACK TEXT
    private void styleRefreshButton(final JButton btn) {
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

    private void styleCancelButton(final JButton btn) {
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

    // Keep the original styleButton method for compatibility (if needed elsewhere)
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}