package model;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the collection of expenses and provides operations to add, remove,
 * and analyze expense data.
 */
public class ExpenseManager {
    private final List<Expense> expenses;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }

    /**
     * Adds a new expense to the collection.
     *
     * @param expense The expense to add
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Removes an expense by its ID.
     *
     * @param expenseId The ID of the expense to remove
     * @return true if the expense was found and removed, false otherwise
     */
    public boolean removeExpense(String expenseId) {
        return expenses.removeIf(expense -> expense.getId().equals(expenseId));
    }

    /**
     * Updates an existing expense.
     *
     * @param updatedExpense The expense with updated information
     * @return true if the expense was found and updated, false otherwise
     */
    public boolean updateExpense(Expense updatedExpense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(updatedExpense.getId())) {
                expenses.set(i, updatedExpense);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all expenses.
     *
     * @return A list of all expenses
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Calculates the total of all expenses.
     *
     * @return The total amount
     */
    public double calculateTotalExpenses() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Gets expenses for a specific month and year.
     *
     * @param year  The year
     * @param month The month
     * @return A list of expenses for the specified month and year
     */
    public List<Expense> getExpensesByMonth(int year, Month month) {
        return expenses.stream()
                .filter(expense -> {
                    LocalDate date = expense.getDate();
                    return date.getYear() == year && date.getMonth() == month;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total expenses for a specific month and year.
     *
     * @param year  The year
     * @param month The month
     * @return The total amount for the specified month and year
     */
    public double calculateMonthlyTotal(int year, Month month) {
        return getExpensesByMonth(year, month).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Gets expenses by category.
     *
     * @param category The category to filter by
     * @return A list of expenses in the specified category
     */
    public List<Expense> getExpensesByCategory(Category category) {
        return expenses.stream()
                .filter(expense -> expense.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Calculates monthly totals for the past several months.
     *
     * @param numberOfMonths The number of past months to include
     * @return A map of YearMonth to total expense amount
     */
    public Map<YearMonth, Double> getMonthlyTotals(int numberOfMonths) {
        Map<YearMonth, Double> monthlyTotals = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < numberOfMonths; i++) {
            YearMonth yearMonth = YearMonth.from(today.minusMonths(i));
            double total = expenses.stream()
                    .filter(expense -> YearMonth.from(expense.getDate()).equals(yearMonth))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            monthlyTotals.put(yearMonth, total);
        }

        return monthlyTotals;
    }

    /**
     * Calculates spending by category for a specific month.
     *
     * @param year  The year
     * @param month The month
     * @return A map of Category to total expense amount
     */
    public Map<Category, Double> getCategoryTotalsForMonth(int year, Month month) {
        Map<Category, Double> categoryTotals = new HashMap<>();
        List<Expense> monthlyExpenses = getExpensesByMonth(year, month);

        for (Category category : Category.values()) {
            double total = monthlyExpenses.stream()
                    .filter(expense -> expense.getCategory() == category)
                    .mapToDouble(Expense::getAmount)
                    .sum();
            categoryTotals.put(category, total);
        }

        return categoryTotals;
    }
}