package com.transportportal.ui;

import com.transportportal.model.TransportData.User;
import com.transportportal.service.TransportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistoryPanel extends JPanel {
    private TransportService service;
    private User passenger;
    private DefaultTableModel historyModel;
    private JTable table;

    public HistoryPanel(TransportService service, User passenger) {
        this.service = service;
        this.passenger = passenger;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Trip History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table
        historyModel = new DefaultTableModel(new String[]{
            "Trip", "Vehicle", "Date", "Price", "Status"
        }, 0);
        
        table = new JTable(historyModel);
        JScrollPane scroll = new JScrollPane(table);

        add(headerPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadHistory();
    }

    private void loadHistory() {
        // This would load from database
        // For now, sample data
        historyModel.addRow(new Object[]{
            "Kigali - Huye", "RAA 123A", "2024-01-15", "5000 RWF", "Completed"
        });
        historyModel.addRow(new Object[]{
            "Kigali - Musanze", "RAB 456B", "2024-01-10", "7000 RWF", "Completed"
        });
    }
}