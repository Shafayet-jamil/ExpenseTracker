package view;

import controller.ExpenseController;
import model.Category;
import model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Main GUI class for the Expense Tracker application.
 */

public class ExpenseTrackerGUI extends JFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243); // Material Blue
    private static final Color SECONDARY_COLOR = new Color(100, 181, 246); // Lighter Blue
    private static final Color ACCENT_COLOR = new Color(255, 87, 34);  // Orange
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    private static final Color TEXT_COLOR = new Color(66, 66, 66); // Dark Gray
    private static final Color POSITIVE_COLOR = new Color(76, 175, 80); // Green
    private static final Color NEGATIVE_COLOR = new Color(244, 67, 54); // Red
    private final ExpenseController controller;
    private final JTable expenseTable;
    private final DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JComboBox<String> monthSelector;
    private JComboBox<Integer> yearSelector;
    private final JPanel reportPanel;
    private final JPanel chartPanel;

    private static final String[] TABLE_COLUMNS = {"ID", "Name", "Amount", "Date", "Category", "Description"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Remove the parameterized constructor or make it private if not needed
    // public ExpenseTrackerGUI(JLabel totalLabel, JComboBox<String> monthSelector, JComboBox<Integer> yearSelector) {/* implementation omitted for shortness */}

    public ExpenseTrackerGUI() {
        // Initialize the controller
        this.controller = new ExpenseController();

        // Initialize the table model
        this.tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Initialize the table
        this.expenseTable = new JTable(tableModel);

        // Initialize other components
        this.totalLabel = new JLabel("Total: $0.00");
        this.monthSelector = new JComboBox<>();
        this.yearSelector = new JComboBox<>();
        this.reportPanel = new JPanel();
        this.chartPanel = new JPanel();

        // Initialize the GUI
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Add components to the frame
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        // Initialize selectors
        initializeSelectors();

        // Style components
        styleTable(expenseTable);

        // Load initial data
        refreshExpenseTable();
        updateTotalLabel();

        // Set window properties
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        // Add your header components here
        // ... component initialization code ...
        return headerPanel; // Make sure this is not null
    }


    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create tabbed pane or split pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();

        // Expenses tab
        JPanel expensesPanel = new JPanel(new BorderLayout());
        expensesPanel.add(createControlPanel(), BorderLayout.NORTH);
        expensesPanel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        tabbedPane.addTab("Expenses", expensesPanel);

        // Reports tab
        tabbedPane.addTab("Reports", reportPanel);

        // Charts tab
        tabbedPane.addTab("Charts", chartPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private void initializeSelectors() {
        // Initialize month selector
        monthSelector.removeAllItems();
        for (Month month : Month.values()) {
            monthSelector.addItem(month.name());
        }
        monthSelector.setSelectedItem(LocalDate.now().getMonth().name());

        // Initialize year selector
        yearSelector.removeAllItems();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear + 1; year++) {
            yearSelector.addItem(year);
        }
        yearSelector.setSelectedItem(currentYear);

        // Add listeners
        monthSelector.addActionListener(e -> filterExpenses());
        yearSelector.addActionListener(e -> filterExpenses());
    }

    // Rest of your methods remain the same...

    /**
     * Creates the control panel with buttons and filters.
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addButton = createStyledButton("Add Expense", POSITIVE_COLOR);
        JButton editButton = createStyledButton("Edit", SECONDARY_COLOR);
        JButton deleteButton = createStyledButton("Delete", NEGATIVE_COLOR);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Create the filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearSelector = new JComboBox<>();
        styleComboBox(yearSelector);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthSelector = new JComboBox<>();
        styleComboBox(monthSelector);

        JButton filterButton = createStyledButton("Filter", PRIMARY_COLOR);

        // Populate year selector (current year and 5 years back)
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i <= 5; i++) {
            yearSelector.addItem(currentYear - i);
        }

        // Populate month selector
        for (Month month : Month.values()) {
            monthSelector.addItem(month.toString());
        }

        filterPanel.add(yearLabel);
        filterPanel.add(yearSelector);
        filterPanel.add(monthLabel);
        filterPanel.add(monthSelector);
        filterPanel.add(filterButton);

        controlPanel.add(buttonPanel, BorderLayout.WEST);
        controlPanel.add(filterPanel, BorderLayout.EAST);

        // Set up event handlers
        addButton.addActionListener(e -> showAddExpenseDialog());
        editButton.addActionListener(e -> editSelectedExpense());
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        filterButton.addActionListener(e -> filterExpenses());

        return controlPanel;
    }

    /**
     * Creates the status panel with totals and summary.
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("Total Expenses: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusPanel.add(totalLabel, BorderLayout.WEST);

        return statusPanel;
    }

    /**
     * Creates a styled button with the specified text and color.
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Styles a combo box with custom appearance.
     */
    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        comboBox.setPreferredSize(new Dimension(120, 30));
    }

    /**
     * Applies custom styling to the expense table.
     */
    private void styleTable(JTable table) {
        // Set font and row height
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));

        // Set selection colors
        table.setSelectionBackground(new Color(SECONDARY_COLOR.getRed(),
                SECONDARY_COLOR.getGreen(),
                SECONDARY_COLOR.getBlue(), 100));
        table.setSelectionForeground(TEXT_COLOR);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Category
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Description

        // Hide the ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Custom renderer for amount column (right-aligned with color)
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (column == 2 && value != null) { // Amount column
                    String amountStr = value.toString();
                    if (amountStr.startsWith("$")) {
                        try {
                            double amount = Double.parseDouble(amountStr.substring(1));
                            if (amount > 0) {
                                c.setForeground(isSelected ? TEXT_COLOR : NEGATIVE_COLOR);
                            }
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }

                return c;
            }
        };
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);

        // Add zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }

                // Add some padding
                ((JLabel) c).setBorder(new EmptyBorder(0, 5, 0, 5));

                return c;
            }
        });
    }

    /**
     * Shows the dialog for adding a new expense.
     */
    private void showAddExpenseDialog() {
        JDialog dialog = new JDialog(this, "Add Expense", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Form fields
        JTextField nameField = new JTextField(20);
        styleTextField(nameField);

        JTextField amountField = new JTextField(10);
        styleTextField(amountField);

        JComboBox<Category> categoryCombo = new JComboBox<>(Category.values());
        styleComboBox(categoryCombo);

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date());
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));

        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        // Add form fields to the panel with styled labels
        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(createStyledLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(createStyledLabel("Date:"));
        formPanel.add(dateSpinner);
        formPanel.add(createStyledLabel("Description:"));
        formPanel.add(descScrollPane);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton saveButton = createStyledButton("Save", POSITIVE_COLOR);
        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showValidationError(dialog, "Please enter a name");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        showValidationError(dialog, "Amount must be greater than zero");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showValidationError(dialog, "Please enter a valid amount");
                    return;
                }

                Category category = (Category) categoryCombo.getSelectedItem();
                java.util.Date date = (java.util.Date) dateSpinner.getValue();
                LocalDate localDate = LocalDate.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
                String description = descriptionArea.getText().trim();

                controller.addExpense(name, amount, localDate, category, description);
                refreshExpenseTable();
                updateTotalLabel();
                updateReportPanel();
                updateChartPanel();
                dialog.dispose();

                // Show success message
                showSuccessMessage("Expense added successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding expense: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Show dialog
        dialog.pack();
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Creates a styled label for forms.
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }


    /**
     * Styles a text field with custom appearance.
     */
    private void styleTextField(JTextField textField) {
        // Set font and colors
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);  // Bright white background for contrast
        textField.setForeground(new Color(50, 50, 50));  // Darker text for better readability

        // Create a more visible border
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),  // Darker outer border
                BorderFactory.createEmptyBorder(5, 5, 5, 5)  // Inner padding
        ));

        // Add focus listener for even better visibility when active
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBackground(new Color(255, 255, 220));  // Light yellow when focused
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),  // Thicker colored border when focused
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)  // Inner padding
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBackground(Color.WHITE);
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
    }

    /**
     * Shows a validation error message with improved styling.
     */
    private void showValidationError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message with improved styling.
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Edits the selected expense.
     */
    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String expenseId = (String) tableModel.getValueAt(selectedRow, 0);
        List<Expense> allExpenses = controller.getAllExpenses();
        Expense selectedExpense = null;

        for (Expense expense : allExpenses) {
            if (expense.getId().equals(expenseId)) {
                selectedExpense = expense;
                break;
            }
        }

        if (selectedExpense == null) {
            JOptionPane.showMessageDialog(this, "Could not find the selected expense",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and show the edit dialog
        JDialog dialog = new JDialog(this, "Edit Expense", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Form fields with existing values
        JTextField nameField = new JTextField(selectedExpense.getName(), 20);
        styleTextField(nameField);

        JTextField amountField = new JTextField(String.valueOf(selectedExpense.getAmount()), 10);
        styleTextField(amountField);

        JComboBox<Category> categoryCombo = new JComboBox<>(Category.values());
        categoryCombo.setSelectedItem(selectedExpense.getCategory());
        styleComboBox(categoryCombo);

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        java.util.Date date = java.util.Date.from(selectedExpense.getDate()
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(date);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextArea descriptionArea = new JTextArea(selectedExpense.getDescription(), 3, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));

        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        // Add form fields to the panel
        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(createStyledLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(createStyledLabel("Date:"));
        formPanel.add(dateSpinner);
        formPanel.add(createStyledLabel("Description:"));
        formPanel.add(descScrollPane);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton saveButton = createStyledButton("Save", POSITIVE_COLOR);
        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        final Expense expenseToEdit = selectedExpense;
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showValidationError(dialog, "Please enter a name");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        showValidationError(dialog, "Amount must be greater than zero");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showValidationError(dialog, "Please enter a valid amount");
                    return;
                }

                Category category = (Category) categoryCombo.getSelectedItem();
                java.util.Date updatedDate = (java.util.Date) dateSpinner.getValue();
                LocalDate localDate = LocalDate.ofInstant(updatedDate.toInstant(),
                        java.time.ZoneId.systemDefault());
                String description = descriptionArea.getText().trim();

                // Update the expense
                expenseToEdit.setName(name);
                expenseToEdit.setAmount(amount);
                expenseToEdit.setCategory(category);
                expenseToEdit.setDate(localDate);
                expenseToEdit.setDescription(description);

                controller.updateExpense(expenseToEdit);
                refreshExpenseTable();
                updateTotalLabel();
                updateReportPanel();
                updateChartPanel();
                dialog.dispose();

                // Show success message
                showSuccessMessage("Expense updated successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating expense: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Show dialog
        dialog.pack();
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Deletes the selected expense.
     */
    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String expenseId = (String) tableModel.getValueAt(selectedRow, 0);

        // Create a custom confirmation dialog
        JDialog confirmDialog = new JDialog(this, "Confirm Delete", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        confirmDialog.setSize(400, 150);
        confirmDialog.setLocationRelativeTo(this);

        JLabel confirmLabel = new JLabel("Are you sure you want to delete this expense?");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmLabel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton yesButton = createStyledButton("Yes", NEGATIVE_COLOR);
        JButton noButton = createStyledButton("No", Color.GRAY);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        confirmDialog.add(confirmLabel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);

        yesButton.addActionListener(e -> {
            confirmDialog.dispose();
            if (controller.removeExpense(expenseId)) {
                refreshExpenseTable();
                updateTotalLabel();
                updateReportPanel();
                updateChartPanel();
                showSuccessMessage("Expense deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete expense",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        noButton.addActionListener(e -> confirmDialog.dispose());

        confirmDialog.setVisible(true);
    }

    /**
     * Filters expenses based on selected month and year.
     */
    private void filterExpenses() {
        int year = (int) yearSelector.getSelectedItem();
        Month month = Month.valueOf(monthSelector.getSelectedItem().toString());

        List<Expense> filteredExpenses = controller.getExpensesByMonth(year, month);
        updateTable(filteredExpenses);

        double monthlyTotal = controller.calculateMonthlyTotal(year, month);
        totalLabel.setText(String.format("Monthly Total: $%.2f", monthlyTotal));

        updateReportPanel(year, month);
        updateChartPanel(year, month);
    }

    /**
     * Refreshes the expense table with all expenses.
     */
    private void refreshExpenseTable() {
        updateTable(controller.getAllExpenses());
    }

    /**
     * Updates the table with the given list of expenses.
     */
    private void updateTable(List<Expense> expenses) {
        tableModel.setRowCount(0); // Clear the table

        for (Expense expense : expenses) {
            Object[] row = {
                    expense.getId(),
                    expense.getName(),
                    String.format("$%.2f", expense.getAmount()),
                    expense.getDate().format(DATE_FORMATTER),
                    expense.getCategory().getDisplayName(),
                    expense.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Updates the total label with the current total expenses.
     */
    private void updateTotalLabel() {
        double total = controller.calculateTotalExpenses();
        totalLabel.setText(String.format("Total Expenses: $%.2f", total));
    }

    /**
     * Updates the report panel with current data.
     */
    private void updateReportPanel() {
        int currentYear = LocalDate.now().getYear();
        Month currentMonth = LocalDate.now().getMonth();
        updateReportPanel(currentYear, currentMonth);
    }

    /**
     * Updates the report panel with data for the specified month and year.
     */
    private void updateReportPanel(int year, Month month) {
        reportPanel.removeAll();

        // Add title
        JLabel titleLabel = new JLabel(month.toString() + " " + year + " Report");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        reportPanel.add(titleLabel);

        // Add monthly total
        double monthlyTotal = controller.calculateMonthlyTotal(year, month);
        JPanel totalPanel = new JPanel();
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        totalPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY_COLOR));

        JLabel totalLabel = new JLabel(String.format("Monthly Total: $%.2f", monthlyTotal));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(NEGATIVE_COLOR);
        totalPanel.add(totalLabel);

        reportPanel.add(totalPanel);
        reportPanel.add(Box.createVerticalStrut(15));

        // Add category breakdown
        JLabel breakdownLabel = new JLabel("Category Breakdown");
        breakdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        breakdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        breakdownLabel.setForeground(PRIMARY_COLOR);
        reportPanel.add(breakdownLabel);
        reportPanel.add(Box.createVerticalStrut(10));

        // Create a panel for category breakdown
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(Color.WHITE);
        categoriesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoriesPanel.setBorder(new EmptyBorder(0, 15, 0, 15));

        Map<Category, Double> categoryTotals = controller.getCategoryTotalsForMonth(year, month);
        for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > 0) {
                JPanel categoryRow = new JPanel(new BorderLayout());
                categoryRow.setBackground(Color.WHITE);
                categoryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

                JLabel categoryLabel = new JLabel(entry.getKey().getDisplayName());
                categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JLabel amountLabel = new JLabel(String.format("$%.2f", entry.getValue()));
                amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                amountLabel.setForeground(NEGATIVE_COLOR);

                categoryRow.add(categoryLabel, BorderLayout.WEST);
                categoryRow.add(amountLabel, BorderLayout.EAST);

                categoriesPanel.add(categoryRow);
                categoriesPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane categoriesScrollPane = new JScrollPane(categoriesPanel);
        categoriesScrollPane.setBorder(null);
        categoriesScrollPane.setPreferredSize(new Dimension(0, 150));
        reportPanel.add(categoriesScrollPane);
        reportPanel.add(Box.createVerticalStrut(20));

        // Add monthly trend (last 6 months)
        JLabel trendLabel = new JLabel("6-Month Trend");
        trendLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trendLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        trendLabel.setForeground(PRIMARY_COLOR);
        reportPanel.add(trendLabel);
        reportPanel.add(Box.createVerticalStrut(10));

        // Create a panel for monthly trends
        JPanel trendsPanel = new JPanel();
        trendsPanel.setLayout(new BoxLayout(trendsPanel, BoxLayout.Y_AXIS));
        trendsPanel.setBackground(Color.WHITE);
        trendsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trendsPanel.setBorder(new EmptyBorder(0, 15, 0, 15));

        Map<YearMonth, Double> monthlyTotals = controller.getMonthlyTotals(6);
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            YearMonth yearMonth = entry.getKey();

            JPanel monthRow = new JPanel(new BorderLayout());
            monthRow.setBackground(Color.WHITE);
            monthRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            JLabel monthLabel = new JLabel(String.format("%s %d",
                    yearMonth.getMonth().toString(), yearMonth.getYear()));
            monthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel amountLabel = new JLabel(String.format("$%.2f", entry.getValue()));
            amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            amountLabel.setForeground(NEGATIVE_COLOR);

            monthRow.add(monthLabel, BorderLayout.WEST);
            monthRow.add(amountLabel, BorderLayout.EAST);

            trendsPanel.add(monthRow);
            trendsPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane trendsScrollPane = new JScrollPane(trendsPanel);
        trendsScrollPane.setBorder(null);
        trendsScrollPane.setPreferredSize(new Dimension(0, 150));
        reportPanel.add(trendsScrollPane);

        reportPanel.revalidate();
        reportPanel.repaint();
    }

    /**
     * Updates the chart panel with current data.
     */
    private void updateChartPanel() {
        int currentYear = LocalDate.now().getYear();
        Month currentMonth = LocalDate.now().getMonth();
        updateChartPanel(currentYear, currentMonth);
    }

    /**
     * Updates the chart panel with data for the specified month and year.
     */
    private void updateChartPanel(int year, Month month) {
        chartPanel.removeAll();

        // Create a simple bar chart for category breakdown
        Map<Category, Double> categoryTotals = controller.getCategoryTotalsForMonth(year, month);

        // Find the maximum value for scaling
        double maxValue = 0;
        for (Double value : categoryTotals.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Create the chart panel
        JPanel barChartPanel = new JPanel(new BorderLayout());
        barChartPanel.setBackground(Color.WHITE);

        // Create the bars panel
        JPanel barsPanel = new JPanel(new GridLayout(1, categoryTotals.size(), 10, 0));
        barsPanel.setBackground(Color.WHITE);
        barsPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Add bars for each category
        for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > 0) {
                JPanel barPanel = new JPanel(new BorderLayout());
                barPanel.setBackground(Color.WHITE);

                // Calculate bar height (percentage of max)
                int barHeight = (int) (150 * (entry.getValue() / maxValue));
                if (barHeight < 5) barHeight = 5; // Minimum height

                // Create the bar
                JPanel bar = new JPanel();
                bar.setPreferredSize(new Dimension(30, barHeight));
                bar.setBackground(getCategoryColor(entry.getKey()));
                bar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

                // Create the label
                JLabel categoryLabel = new JLabel(entry.getKey().toString().substring(0, 3));
                categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

                // Create the value label
                JLabel valueLabel = new JLabel(String.format("$%.0f", entry.getValue()));
                valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

                // Add components to the bar panel
                JPanel barContainer = new JPanel(new BorderLayout());
                barContainer.setBackground(Color.WHITE);
                barContainer.add(bar, BorderLayout.SOUTH);

                barPanel.add(valueLabel, BorderLayout.NORTH);
                barPanel.add(barContainer, BorderLayout.CENTER);
                barPanel.add(categoryLabel, BorderLayout.SOUTH);

                barsPanel.add(barPanel);
            }
        }

        // Add the bars panel to the chart panel
        barChartPanel.add(barsPanel, BorderLayout.CENTER);

        // Add title
        JLabel chartTitle = new JLabel("Monthly Expenses by Category", SwingConstants.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartTitle.setForeground(PRIMARY_COLOR);
        chartTitle.setBorder(new EmptyBorder(10, 0, 10, 0));

        barChartPanel.add(chartTitle, BorderLayout.NORTH);

        // Add the chart to the main chart panel
        chartPanel.add(barChartPanel, BorderLayout.CENTER);

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    /**
     * Returns a color for a specific category.
     */
    private Color getCategoryColor(Category category) {
        switch (category) {
            case FOOD: return new Color(255, 182, 193); // Light Pink
            case TRANSPORTATION: return new Color(138, 43, 226); // Blue Violet (Purple)
            case HOUSING: return new Color(75, 0, 130); // Indigo (Deep Purple)
            case ENTERTAINMENT: return new Color(30, 144, 255); // Dodger Blue
            case SHOPPING: return new Color(0, 191, 255); // Deep Sky Blue (Cyan)
            case HEALTHCARE: return new Color(0, 128, 128); // Teal
            case EDUCATION: return new Color(34, 139, 34); // Forest Green
            case TRAVEL: return new Color(255, 165, 0); // Orange
            case PERSONAL: return new Color(139, 69, 19); // Saddle Brown
            case OTHER: return new Color(128, 128, 128); // Gray
            default: return SECONDARY_COLOR;
        }
    }

    /**
     * Creates an ImageIcon from the specified path.
     */
    private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}