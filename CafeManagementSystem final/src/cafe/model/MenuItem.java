package cafe.model;

import java.io.Serializable;

/**
 * Represents a single item available on the cafe menu.
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Encapsulation</b> — all fields private, accessed via getters/setters</li>
 *   <li><b>Serializable</b> — enables file-based persistence via Java I/O</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 5L;

    /** Unique identifier for this menu item. */
    private int id;

    /** Display name of the item. */
    private String name;

    /** Category the item belongs to (e.g., "Hot Beverages"). */
    private String category;

    /** Price of the item in dollars. */
    private double price;

    /** Whether this item is currently available to order. */
    private boolean available;

    /** Short description shown to customers. */
    private String description;

    /**
     * Default constructor required for serialization.
     */
    public MenuItem() {}

    /**
     * Parameterized constructor — creates a new available menu item.
     *
     * @param id          the unique item ID
     * @param name        the display name
     * @param category    the menu category
     * @param price       the price in dollars
     * @param description a short customer-facing description
     */
    public MenuItem(int id, String name, String category, double price, String description) {
        this.id          = id;
        this.name        = name;
        this.category    = category;
        this.price       = price;
        this.description = description;
        this.available   = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return the item's unique ID */
    public int getId()              { return id; }

    /** @return the item's display name */
    public String getName()         { return name; }

    /** @return the menu category */
    public String getCategory()     { return category; }

    /** @return the price in dollars */
    public double getPrice()        { return price; }

    /** @return true if this item is available to order */
    public boolean isAvailable()    { return available; }

    /** @return the short description */
    public String getDescription()  { return description; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param name the new display name */
    public void setName(String name)              { this.name = name; }

    /** @param category the new category */
    public void setCategory(String category)      { this.category = category; }

    /** @param price the new price (must be positive) */
    public void setPrice(double price)            { this.price = price; }

    /** @param available true to make item orderable, false to hide it */
    public void setAvailable(boolean available)   { this.available = available; }

    /** @param description the new description */
    public void setDescription(String description){ this.description = description; }

    /**
     * Returns a formatted one-line display of this menu item.
     *
     * <p><b>OOP Concept:</b> Method Overriding — overrides {@code Object.toString()}.</p>
     *
     * @return formatted item string with ID, name, category, price, and status
     */
    @Override
    public String toString() {
        String status = available ? "[Available]" : "[Unavailable]";
        return String.format("  [%2d] %-22s | %-14s | $%-7.2f | %s",
                id, name, category, price, status);
    }
}
