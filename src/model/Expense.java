
package model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents an expense entry in the expense tracker application.
 */
public class Expense {
    private final String id;
    private String name;
    private double amount;
    private LocalDate date;
    private Category category;
    private String description;

    /**
     * Creates a new expense with the specified details.
     *
     * @param name        The name of the expense
     * @param amount      The amount spent
     * @param date        The date when the expense occurred
     * @param category    The category of the expense
     * @param description Additional description (optional)
     */
    public Expense(String name, double amount, LocalDate date, Category category, String description) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    /**
     * Simplified constructor with only essential fields.
     */
    public Expense(String name, double amount, LocalDate date, Category category) {
        this(name, amount, date, category, "");
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f (%s) - %s", name, amount, category, date);
    }
}