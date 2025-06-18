package util;

import model.Category;
import model.Expense;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file operations for saving and loading expense data.
 */
public class FileHandler {
    private static final String DEFAULT_FILE_PATH = "expenses.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Saves a list of expenses to a CSV file.
     *
     * @param expenses The expenses to save
     * @param filePath The path to the file (optional)
     * @throws IOException If an I/O error occurs
     */
    public static void saveExpenses(List<Expense> expenses, String filePath) throws IOException {
        String path = (filePath != null && !filePath.isEmpty()) ? filePath : DEFAULT_FILE_PATH;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Write header
            writer.write("ID,Name,Amount,Date,Category,Description");
            writer.newLine();

            // Write expense data
            for (Expense expense : expenses) {
                String line = String.format("%s,%s,%.2f,%s,%s,%s",
                        expense.getId(),
                        escapeCSV(expense.getName()),
                        expense.getAmount(),
                        expense.getDate().format(DATE_FORMATTER),
                        expense.getCategory().name(),
                        escapeCSV(expense.getDescription()));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Loads expenses from a CSV file.
     *
     * @param filePath The path to the file (optional)
     * @return A list of loaded expenses
     * @throws IOException If an I/O error occurs
     */
    public static List<Expense> loadExpenses(String filePath) throws IOException {
        String path = (filePath != null && !filePath.isEmpty()) ? filePath : DEFAULT_FILE_PATH;
        List<Expense> expenses = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()) {
            return expenses; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read expense data
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    String id = parts[0];
                    String name = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3], DATE_FORMATTER);
                    Category category = Category.valueOf(parts[4]);
                    String description = parts[5];

                    // Create a new expense with the loaded data
                    Expense expense = new Expense(name, amount, date, category, description) {
                        // Anonymous subclass to override the ID
                        @Override
                        public String getId() {
                            return id;
                        }
                    };

                    expenses.add(expense);
                }
            }
        } catch (Exception e) {
            throw new IOException("Error loading expenses: " + e.getMessage(), e);
        }

        return expenses;
    }

    /**
     * Escapes special characters in CSV values.
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // If the value contains comma, quote, or newline, wrap it in quotes and escape any quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Parses a CSV line, handling quoted values correctly.
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Check if this is an escaped quote
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++; // Skip the next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString());
                currentValue.setLength(0); // Reset for next value
            } else {
                currentValue.append(c);
            }
        }

        // Add the last value
        result.add(currentValue.toString());

        return result.toArray(new String[0]);
    }
}