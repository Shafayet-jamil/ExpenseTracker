package controller;

import model.Category;
import model.Expense;
import model.ExpenseManager;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Controller class that connects the expense model with the user interface.
 */
public class ExpenseController {
    private final ExpenseManager expenseManager;

    public ExpenseController() {
        this.expenseManager = new ExpenseManager();
    }

    /**
     * Adds a new expense.
     */
    public void addExpense(String name, double amount, LocalDate date, Category category, String description) {
        Expense expense = new Expense(name, amount, date, category, description);
        expenseManager.addExpense(expense);
    }

    /**
     * Removes an expense by ID.
     */
    public boolean removeExpense(String expenseId) {
        return expenseManager.removeExpense(expenseId);
    }

    /**
     * Updates an existing expense.
     */
    public boolean updateExpense(Expense updatedExpense) {
        return expenseManager.updateExpense(updatedExpense);
    }

    /**
     * Gets all expenses.
     */
    public List<Expense> getAllExpenses() {
        return expenseManager.getAllExpenses();
    }

    /**
     * Calculates the total of all expenses.
     */
    public double calculateTotalExpenses() {
        return expenseManager.calculateTotalExpenses();
    }

    /**
     * Gets expenses for a specific month.
     */
    public List<Expense> getExpensesByMonth(int year, Month month) {
        return expenseManager.getExpensesByMonth(year, month);
    }

    /**
     * Calculates the total expenses for a specific month.
     */
    public double calculateMonthlyTotal(int year, Month month) {
        return expenseManager.calculateMonthlyTotal(year, month);
    }

    /**
     * Gets expenses by category.
     */
    public List<Expense> getExpensesByCategory(Category category) {
        return expenseManager.getExpensesByCategory(category);
    }

    /**
     * Gets monthly totals for the past several months.
     */
    public Map<YearMonth, Double> getMonthlyTotals(int numberOfMonths) {
        return expenseManager.getMonthlyTotals(numberOfMonths);
    }

    /**
     * Gets category totals for a specific month.
     */
    public Map<Category, Double> getCategoryTotalsForMonth(int year, Month month) {
        return expenseManager.getCategoryTotalsForMonth(year, month);
    }
}