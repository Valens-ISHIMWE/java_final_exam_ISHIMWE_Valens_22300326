package com.transportportal.ui;

import com.transportportal.model.TransportData.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentDialog extends JDialog {
    private com.transportportal.model.Trip trip;
    private User passenger;
    private boolean paymentSuccessful = false;
    
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JTextField nameField;
    private JComboBox<String> paymentMethodCombo;
    private JButton payButton;
    private JButton cancelButton;

    public PaymentDialog(Frame parent, com.transportportal.model.Trip trip, User passenger) {
        super(parent, "Payment Processing", true);
        this.trip = trip;
        this.passenger = passenger;
        initialize();
    }

    private void initialize() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 143, 235));
        headerPanel.setPreferredSize(new Dimension(450, 60));
        JLabel headerLabel = new JLabel("Complete Your Payment");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Trip details
        JPanel tripPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        tripPanel.setBorder(BorderFactory.createTitledBorder("Trip Details"));
        tripPanel.add(new JLabel("Passenger:"));
        tripPanel.add(new JLabel(passenger.getFullName()));
        tripPanel.add(new JLabel("Route:"));
        tripPanel.add(new JLabel(trip.getRouteName()));
        tripPanel.add(new JLabel("Vehicle:"));
        tripPanel.add(new JLabel(trip.getVehiclePlate()));
        tripPanel.add(new JLabel("Amount:"));
        tripPanel.add(new JLabel(trip.getPrice() + " RWF"));

        // Payment method
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        methodPanel.add(new JLabel("Method:"));
        paymentMethodCombo = new JComboBox<>(new String[]{"Credit Card", "Mobile Money", "Cash"});
        methodPanel.add(paymentMethodCombo);

        // Payment form (credit card details)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Information"));

        formPanel.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        formPanel.add(cardNumberField);

        formPanel.add(new JLabel("Expiry Date (MM/YY):"));
        expiryField = new JTextField();
        formPanel.add(expiryField);

        formPanel.add(new JLabel("CVV:"));
        cvvField = new JTextField();
        formPanel.add(cvvField);

        formPanel.add(new JLabel("Cardholder Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        contentPanel.add(tripPanel);
        contentPanel.add(methodPanel);
        contentPanel.add(formPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        payButton = new JButton("Pay Now");
        cancelButton = new JButton("Cancel");

        styleButton(payButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(cancelButton, new Color(108, 117, 125), Color.WHITE);

        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paymentSuccessful = false;
                dispose();
            }
        });

        // Update form based on payment method
        paymentMethodCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePaymentForm();
            }
        });

        updatePaymentForm();
    }

    private void updatePaymentForm() {
        String method = (String) paymentMethodCombo.getSelectedItem();
        boolean isCard = "Credit Card".equals(method);
        
        cardNumberField.setEnabled(isCard);
        expiryField.setEnabled(isCard);
        cvvField.setEnabled(isCard);
        nameField.setEnabled(isCard);
        
        if (!isCard) {
            cardNumberField.setText("");
            expiryField.setText("");
            cvvField.setText("");
            nameField.setText("");
        }
    }

    private void processPayment() {
        final String method = (String) paymentMethodCombo.getSelectedItem();
        
        if ("Credit Card".equals(method) && !validateCardPayment()) {
            return;
        }

        // Simulate payment processing
        try {
            payButton.setEnabled(false);
            payButton.setText("Processing...");
            
            // Simulate API call delay
            Timer timer = new Timer(2000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    paymentSuccessful = true;
                    dispose();
                    JOptionPane.showMessageDialog(PaymentDialog.this, 
                        "Payment processed successfully!\n" +
                        "Method: " + method + "\n" +
                        "Amount: " + trip.getPrice() + " RWF\n" +
                        "Transaction ID: TRX" + System.currentTimeMillis(),
                        "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            timer.setRepeats(false);
            timer.start();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Payment failed: " + ex.getMessage(),
                "Payment Error", JOptionPane.ERROR_MESSAGE);
            payButton.setEnabled(true);
            payButton.setText("Pay Now");
        }
    }

    private boolean validateCardPayment() {
        if (cardNumberField.getText().trim().length() != 16) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number");
            return false;
        }
        if (expiryField.getText().trim().length() != 5) {
            JOptionPane.showMessageDialog(this, "Please enter expiry date in MM/YY format");
            return false;
        }
        if (cvvField.getText().trim().length() != 3) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 3-digit CVV");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter cardholder name");
            return false;
        }
        return true;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }
}