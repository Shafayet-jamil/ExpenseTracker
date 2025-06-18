package model;

/**
 * Represents expense categories in the application.
 */
public enum Category {
    FOOD("Food & Dining"),
    TRANSPORTATION("Transportation"),
    HOUSING("Housing & Utilities"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    HEALTHCARE("Healthcare"),
    EDUCATION("Education"),
    TRAVEL("Travel"),
    PERSONAL("Personal Care"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
