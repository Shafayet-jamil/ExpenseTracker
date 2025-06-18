import view.ExpenseTrackerGUI;

import javax.swing.*;

/**
 * Main class for the Expense Tracker application.
 */
public class ExpenseTrackerApp {
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerGUI gui = new ExpenseTrackerGUI();
            gui.setVisible(true);
        });
    }
}