package com.transportportal.ui;

import com.transportportal.model.TransportData.Booking;
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

public class BookingsPaymentsPanel extends JPanel {
    private TransportService service;
    private DefaultTableModel bookingsModel;
    private DefaultTableModel paymentsModel;
    private JTable bookingsTable;
    private JTable paymentsTable;
    
    private List<Booking> bookings = new ArrayList<Booking>();
    private List<Payment> payments = new ArrayList<Payment>();

    public BookingsPaymentsPanel(TransportService service) {
        this.service = service;
        initialize();
        loadSampleData();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Bookings & Payments Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        // Main content panel with tabs
        JTabbedPane contentTabs = new JTabbedPane();
        contentTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Bookings Tab
        JPanel bookingsPanel = createBookingsPanel();
        contentTabs.addTab("📋 Bookings", bookingsPanel);

        // Payments Tab
        JPanel paymentsPanel = createPaymentsPanel();
        contentTabs.addTab("💰 Payments", paymentsPanel);

        add(contentTabs, BorderLayout.CENTER);
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Bookings table
        bookingsModel = new DefaultTableModel(new String[]{
            "ID", "Passenger", "Trip", "Seats", "Total Price", "Status", "Booked Date"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(bookingsModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setRowHeight(30);
        JScrollPane bookingsScroll = new JScrollPane(bookingsTable);
        bookingsScroll.setBorder(BorderFactory.createTitledBorder("All Bookings"));
        panel.add(bookingsScroll, BorderLayout.CENTER);

        // Bookings buttons
        JPanel bookingsButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bookingsButtonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnViewDetails = new JButton("👁️ View Details");
        JButton btnCancelBooking = new JButton("❌ Cancel Booking");
        JButton btnUpdateStatus = new JButton("🔄 Update Status");
        JButton btnRefreshBookings = new JButton("🔄 Refresh");
        
        styleEditButton(btnViewDetails);
        styleDeleteButton(btnCancelBooking);
        styleEditButton(btnUpdateStatus);
        styleRefreshButton(btnRefreshBookings);

        bookingsButtonPanel.add(btnViewDetails);
        bookingsButtonPanel.add(btnCancelBooking);
        bookingsButtonPanel.add(btnUpdateStatus);
        bookingsButtonPanel.add(btnRefreshBookings);

        panel.add(bookingsButtonPanel, BorderLayout.SOUTH);

        // Add action listeners for bookings
        btnViewDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewBookingDetails();
            }
        });

        btnCancelBooking.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelSelectedBooking();
            }
        });

        btnUpdateStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBookingStatus();
            }
        });

        btnRefreshBookings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshBookingsData();
            }
        });

        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Payments table
        paymentsModel = new DefaultTableModel(new String[]{
            "ID", "Booking ID", "Amount", "Method", "Status", "Transaction Date"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentsTable = new JTable(paymentsModel);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentsTable.setRowHeight(30);
        JScrollPane paymentsScroll = new JScrollPane(paymentsTable);
        paymentsScroll.setBorder(BorderFactory.createTitledBorder("Payment History"));
        panel.add(paymentsScroll, BorderLayout.CENTER);

        // Payments buttons
        JPanel paymentsButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paymentsButtonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnViewPayment = new JButton("👁️ View Details");
        JButton btnProcessRefund = new JButton("💸 Process Refund");
        JButton btnUpdatePayment = new JButton("🔄 Update Status");
        JButton btnRefreshPayments = new JButton("🔄 Refresh");
        
        styleEditButton(btnViewPayment);
        styleEditButton(btnProcessRefund);
        styleEditButton(btnUpdatePayment);
        styleRefreshButton(btnRefreshPayments);

        paymentsButtonPanel.add(btnViewPayment);
        paymentsButtonPanel.add(btnProcessRefund);
        paymentsButtonPanel.add(btnUpdatePayment);
        paymentsButtonPanel.add(btnRefreshPayments);

        panel.add(paymentsButtonPanel, BorderLayout.SOUTH);

        // Add action listeners for payments
        btnViewPayment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPaymentDetails();
            }
        });

        btnProcessRefund.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processRefund();
            }
        });

        btnUpdatePayment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePaymentStatus();
            }
        });

        btnRefreshPayments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshPaymentsData();
            }
        });

        return panel;
    }

    private void loadSampleData() {
        // Load sample bookings
        bookings.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        
        bookings.add(createSampleBooking(1, "John Passenger", "Kigali - Huye", 2, 10000, "CONFIRMED"));
        bookings.add(createSampleBooking(2, "Jane Traveler", "Kigali - Musanze", 1, 7000, "CONFIRMED"));
        bookings.add(createSampleBooking(3, "Mike Explorer", "Kigali - Rubavu", 3, 24000, "PENDING"));
        bookings.add(createSampleBooking(4, "Sarah Tourist", "Huye - Kigali", 2, 10000, "COMPLETED"));
        bookings.add(createSampleBooking(5, "David Visitor", "Musanze - Kigali", 1, 7000, "CANCELLED"));

        // Load sample payments
        payments.clear();
        payments.add(createSamplePayment(1, 1, 10000, "Credit Card", "COMPLETED"));
        payments.add(createSamplePayment(2, 2, 7000, "Mobile Money", "COMPLETED"));
        payments.add(createSamplePayment(3, 3, 24000, "Credit Card", "PENDING"));
        payments.add(createSamplePayment(4, 4, 10000, "Cash", "COMPLETED"));
        payments.add(createSamplePayment(5, 5, 7000, "Credit Card", "REFUNDED"));

        refreshBookingsTable();
        refreshPaymentsTable();
    }

    private Booking createSampleBooking(int id, String passenger, String trip, int seats, double totalPrice, String status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setPassengerName(passenger);
        booking.setRouteName(trip);
        booking.setSeats(seats);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(status);
        booking.setBookedAt(new Timestamp(System.currentTimeMillis() - (id * 86400000L))); // Different dates
        return booking;
    }

    private Payment createSamplePayment(int id, int bookingId, double amount, String method, String status) {
        Payment payment = new Payment();
        payment.id = id;
        payment.bookingId = bookingId;
        payment.amount = amount;
        payment.method = method;
        payment.status = status;
        payment.transactionDate = new Timestamp(System.currentTimeMillis() - (id * 86400000L));
        return payment;
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        
        for (Booking booking : bookings) {
            bookingsModel.addRow(new Object[]{
                booking.getId(),
                booking.getPassengerName(),
                booking.getRouteName(),
                booking.getSeats(),
                String.format("%,.0f", booking.getTotalPrice()) + " RWF",
                booking.getStatus(),
                sdf.format(booking.getBookedAt())
            });
        }
    }

    private void refreshPaymentsTable() {
        paymentsModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        for (Payment payment : payments) {
            paymentsModel.addRow(new Object[]{
                payment.id,
                payment.bookingId,
                String.format("%,.0f", payment.amount) + " RWF",
                payment.method,
                payment.status,
                sdf.format(payment.transactionDate)
            });
        }
    }

    private void viewBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (Integer) bookingsModel.getValueAt(selectedRow, 0);
        Booking booking = findBookingById(bookingId);
        
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create detailed view dialog
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Booking Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        
        detailsPanel.add(new JLabel("Booking ID:"));
        detailsPanel.add(new JLabel(String.valueOf(booking.getId())));
        detailsPanel.add(new JLabel("Passenger:"));
        detailsPanel.add(new JLabel(booking.getPassengerName()));
        detailsPanel.add(new JLabel("Trip:"));
        detailsPanel.add(new JLabel(booking.getRouteName()));
        detailsPanel.add(new JLabel("Seats:"));
        detailsPanel.add(new JLabel(String.valueOf(booking.getSeats())));
        detailsPanel.add(new JLabel("Total Price:"));
        detailsPanel.add(new JLabel(String.format("%,.0f RWF", booking.getTotalPrice())));
        detailsPanel.add(new JLabel("Status:"));
        detailsPanel.add(new JLabel(booking.getStatus()));
        detailsPanel.add(new JLabel("Booked Date:"));
        detailsPanel.add(new JLabel(sdf.format(booking.getBookedAt())));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close");
        styleCancelButton(btnClose);
        buttonPanel.add(btnClose);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (Integer) bookingsModel.getValueAt(selectedRow, 0);
        Booking booking = findBookingById(bookingId);
        
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("CANCELLED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            JOptionPane.showMessageDialog(this, 
                "Cannot cancel a booking that is already " + booking.getStatus().toLowerCase() + ".", 
                "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel booking #" + bookingId + "?\n" +
            "Passenger: " + booking.getPassengerName() + "\n" +
            "Trip: " + booking.getRouteName() + "\n" +
            "This may trigger a refund if payment was made.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            booking.setStatus("CANCELLED");
            
            // Also update associated payment status if exists
            Payment payment = findPaymentByBookingId(bookingId);
            if (payment != null && "COMPLETED".equals(payment.status)) {
                payment.status = "REFUNDED";
            }
            
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
            refreshBookingsTable();
            refreshPaymentsTable();
        }
    }

    private void updateBookingStatus() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (Integer) bookingsModel.getValueAt(selectedRow, 0);
        Booking booking = findBookingById(bookingId);
        
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] statusOptions = {"PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for booking #" + bookingId + ":",
            "Update Booking Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            booking.getStatus());

        if (newStatus != null && !newStatus.equals(booking.getStatus())) {
            booking.setStatus(newStatus);
            JOptionPane.showMessageDialog(this, "Booking status updated to: " + newStatus);
            refreshBookingsTable();
        }
    }

    private void refreshBookingsData() {
        loadSampleData();
        JOptionPane.showMessageDialog(this, "Bookings data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewPaymentDetails() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (Integer) paymentsModel.getValueAt(selectedRow, 0);
        Payment payment = findPaymentById(paymentId);
        
        if (payment == null) {
            JOptionPane.showMessageDialog(this, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create detailed view dialog
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        
        detailsPanel.add(new JLabel("Payment ID:"));
        detailsPanel.add(new JLabel(String.valueOf(payment.id)));
        detailsPanel.add(new JLabel("Booking ID:"));
        detailsPanel.add(new JLabel(String.valueOf(payment.bookingId)));
        detailsPanel.add(new JLabel("Amount:"));
        detailsPanel.add(new JLabel(String.format("%,.0f RWF", payment.amount)));
        detailsPanel.add(new JLabel("Method:"));
        detailsPanel.add(new JLabel(payment.method));
        detailsPanel.add(new JLabel("Status:"));
        detailsPanel.add(new JLabel(payment.status));
        detailsPanel.add(new JLabel("Transaction Date:"));
        detailsPanel.add(new JLabel(sdf.format(payment.transactionDate)));
        detailsPanel.add(new JLabel("Transaction ID:"));
        detailsPanel.add(new JLabel("TRX" + String.format("%06d", payment.id)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close");
        styleCancelButton(btnClose);
        buttonPanel.add(btnClose);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void processRefund() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment to process refund.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (Integer) paymentsModel.getValueAt(selectedRow, 0);
        Payment payment = findPaymentById(paymentId);
        
        if (payment == null) {
            JOptionPane.showMessageDialog(this, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"COMPLETED".equals(payment.status)) {
            JOptionPane.showMessageDialog(this, 
                "Refund can only be processed for completed payments. Current status: " + payment.status, 
                "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Process refund for payment #" + paymentId + "?\n" +
            "Amount: " + String.format("%,.0f RWF", payment.amount) + "\n" +
            "Method: " + payment.method + "\n" +
            "This action cannot be undone.",
            "Confirm Refund", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            payment.status = "REFUNDED";
            
            // Also update associated booking status if exists
            Booking booking = findBookingById(payment.bookingId);
            if (booking != null) {
                booking.setStatus("CANCELLED");
            }
            
            JOptionPane.showMessageDialog(this, 
                "Refund processed successfully!\n" +
                "Amount: " + String.format("%,.0f RWF", payment.amount) + "\n" +
                "Transaction ID: REF" + String.format("%06d", paymentId),
                "Refund Complete", JOptionPane.INFORMATION_MESSAGE);
                
            refreshPaymentsTable();
            refreshBookingsTable();
        }
    }

    private void updatePaymentStatus() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a payment to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (Integer) paymentsModel.getValueAt(selectedRow, 0);
        Payment payment = findPaymentById(paymentId);
        
        if (payment == null) {
            JOptionPane.showMessageDialog(this, "Payment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] statusOptions = {"PENDING", "COMPLETED", "FAILED", "REFUNDED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for payment #" + paymentId + ":",
            "Update Payment Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            payment.status);

        if (newStatus != null && !newStatus.equals(payment.status)) {
            payment.status = newStatus;
            JOptionPane.showMessageDialog(this, "Payment status updated to: " + newStatus);
            refreshPaymentsTable();
        }
    }

    private void refreshPaymentsData() {
        loadSampleData();
        JOptionPane.showMessageDialog(this, "Payments data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper methods
    private Booking findBookingById(int id) {
        for (Booking booking : bookings) {
            if (booking.getId() == id) {
                return booking;
            }
        }
        return null;
    }

    private Payment findPaymentById(int id) {
        for (Payment payment : payments) {
            if (payment.id == id) {
                return payment;
            }
        }
        return null;
    }

    private Payment findPaymentByBookingId(int bookingId) {
        for (Payment payment : payments) {
            if (payment.bookingId == bookingId) {
                return payment;
            }
        }
        return null;
    }

    // Payment inner class (since we don't have a proper Payment model)
    private class Payment {
        int id;
        int bookingId;
        double amount;
        String method;
        String status;
        Timestamp transactionDate;
    }

    // BUTTON STYLING METHODS - ALL WITH BLACK TEXT
    private void styleAddButton(final JButton btn) {
        btn.setBackground(new Color(40, 167, 69)); // GREEN
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 136, 56));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 167, 69));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleEditButton(final JButton btn) {
        btn.setBackground(new Color(0, 123, 255)); // BLUE
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleDeleteButton(final JButton btn) {
        btn.setBackground(new Color(220, 53, 69)); // RED
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 35, 51));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 53, 69));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleRefreshButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }

    private void styleCancelButton(final JButton btn) {
        btn.setBackground(new Color(108, 117, 125)); // GRAY
        btn.setForeground(Color.BLACK); // CHANGED TO BLACK
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 98, 104));
                btn.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(108, 117, 125));
                btn.setForeground(Color.BLACK); // Keep black on exit
            }
        });
    }
}