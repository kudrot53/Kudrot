package cafe.model;

import java.io.Serializable;

/**
 * Represents one line item within a customer's order.
 *
 * <p>Each OrderItem links a {@link MenuItem} to a quantity and optional special note.</p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Encapsulation</b> — all fields private with getters/setters</li>
 *   <li><b>Method Overloading</b> — two constructors with different parameter sets</li>
 *   <li><b>Serializable</b> — supports file-based persistence</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 6L;

    /** The menu item being ordered. */
    private MenuItem menuItem;

    /** How many of this item were ordered. */
    private int quantity;

    /** Any customer special instruction (e.g., "no sugar", "extra hot"). */
    private String specialNote;

    /**
     * Default constructor required for serialization.
     */
    public OrderItem() {}

    /**
     * Constructor without a special note.
     *
     * <p><b>OOP Concept:</b> Constructor Overloading — same name, fewer parameters.</p>
     *
     * @param menuItem the menu item being ordered
     * @param quantity how many units to order
     */
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem    = menuItem;
        this.quantity    = quantity;
        this.specialNote = "";
    }

    /**
     * Constructor with a special note.
     *
     * <p><b>OOP Concept:</b> Constructor Overloading — same name, more parameters.</p>
     *
     * @param menuItem    the menu item being ordered
     * @param quantity    how many units to order
     * @param specialNote any special instruction from the customer
     */
    public OrderItem(MenuItem menuItem, int quantity, String specialNote) {
        this.menuItem    = menuItem;
        this.quantity    = quantity;
        this.specialNote = specialNote;
    }

    // ── Business Logic ────────────────────────────────────────────────────────

    /**
     * Calculates the subtotal for this line item.
     *
     * @return item price multiplied by quantity
     */
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    /** @return the menu item */
    public MenuItem getMenuItem()   { return menuItem; }

    /** @return the quantity ordered */
    public int getQuantity()        { return quantity; }

    /** @return any special note from the customer */
    public String getSpecialNote()  { return specialNote; }

    /** @param quantity the updated quantity */
    public void setQuantity(int quantity)      { this.quantity = quantity; }

    /** @param note the updated special instruction */
    public void setSpecialNote(String note)    { this.specialNote = note; }

    /**
     * Returns a formatted display line for this order item.
     *
     * <p><b>OOP Concept:</b> Method Overriding — overrides {@code Object.toString()}.</p>
     *
     * @return formatted string showing item name, quantity, subtotal, and note
     */
    @Override
    public String toString() {
        String note = (specialNote == null || specialNote.isEmpty())
                      ? "" : "  [Note: " + specialNote + "]";
        return String.format("  %-22s x%d  -->  $%.2f%s",
                menuItem.getName(), quantity, getSubtotal(), note);
    }
}
