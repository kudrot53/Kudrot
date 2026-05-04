package cafe.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer's order — the central transaction entity.
 *
 * <p>An Order is created when a customer places items, tracks status through
 * the kitchen workflow, and produces a formatted receipt on completion.</p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Encapsulation</b> — all fields private with controlled access</li>
 *   <li><b>Static members</b> — {@code idCounter} shared across all Order instances</li>
 *   <li><b>Enum</b> — {@code Status} enum for type-safe order states</li>
 *   <li><b>Serializable</b> — enables file-based persistence</li>
 *   <li><b>Method Overloading</b> — multiple addItem() method signatures</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 7L;

    /**
     * Enum representing possible states of an order.
     * <p><b>OOP Concept:</b> Enum type used for type-safe status tracking.</p>
     */
    public enum Status { PENDING, PREPARING, READY, COMPLETED, CANCELLED }

    /**
     * Static counter — auto-increments to assign unique order IDs.
     * <p><b>OOP Concept:</b> Static field shared across ALL Order instances.</p>
     */
    private static int idCounter = 1000;

    /** Unique ID for this order. */
    private int orderId;

    /** The customer who placed this order. */
    private Customer customer;

    /** The list of items in this order. */
    private List<OrderItem> items;

    /** Current status of this order in the workflow. */
    private Status status;

    /** Timestamp when this order was created. */
    private LocalDateTime createdAt;

    /** Timestamp when this order was completed (null until completed). */
    private LocalDateTime completedAt;

    /** The table number where this order was placed. */
    private String tableNumber;

    /** Tax rate applied to this order (8%). */
    private final double taxRate = 0.08;

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Default constructor required for serialization.
     */
    public Order() {
        this.items = new ArrayList<>();
    }

    /**
     * Parameterized constructor — creates a new PENDING order.
     *
     * @param customer    the customer placing the order
     * @param tableNumber the table the order is for
     */
    public Order(Customer customer, String tableNumber) {
        this.orderId      = ++idCounter;
        this.customer     = customer;
        this.tableNumber  = tableNumber;
        this.items        = new ArrayList<>();
        this.status       = Status.PENDING;
        this.createdAt    = LocalDateTime.now();
    }

    // ── Business Logic ────────────────────────────────────────────────────────

    /**
     * Adds an OrderItem to this order.
     *
     * <p><b>OOP Concept:</b> Method Overloading — addItem with only item (no note).</p>
     *
     * @param item the order line item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
    }

    /**
     * Removes an item from this order by index.
     *
     * @param index the 0-based index of the item to remove
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) items.remove(index);
    }

    /**
     * Calculates the pre-tax subtotal for all items.
     *
     * @return sum of all OrderItem subtotals
     */
    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    /**
     * Calculates the tax amount based on the subtotal.
     *
     * @return tax amount (8% of subtotal)
     */
    public double getTax() { return getSubtotal() * taxRate; }

    /**
     * Calculates the total amount due including tax.
     *
     * @return subtotal plus tax
     */
    public double getTotal() { return getSubtotal() + getTax(); }

    /**
     * Marks this order as completed and records the completion timestamp.
     */
    public void complete() {
        this.status      = Status.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Cancels this order by setting its status to CANCELLED.
     */
    public void cancel() { this.status = Status.CANCELLED; }

    /**
     * Updates the status of this order.
     *
     * @param status the new status to apply
     */
    public void setStatus(Status status) { this.status = status; }

    /**
     * Returns whether this order has any items.
     *
     * @return true if no items have been added
     */
    public boolean isEmpty() { return items.isEmpty(); }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return the unique order ID */
    public int getOrderId()               { return orderId; }

    /** @return the customer who placed the order */
    public Customer getCustomer()         { return customer; }

    /** @return list of items in this order */
    public List<OrderItem> getItems()     { return items; }

    /** @return current order status */
    public Status getStatus()             { return status; }

    /** @return when this order was created */
    public LocalDateTime getCreatedAt()   { return createdAt; }

    /** @return when this order was completed (may be null) */
    public LocalDateTime getCompletedAt() { return completedAt; }

    /** @return the table number for this order */
    public String getTableNumber()        { return tableNumber; }

    // ── Receipt ───────────────────────────────────────────────────────────────

    /**
     * Generates a formatted ASCII receipt string for this order.
     *
     * @return multi-line receipt string ready for console output
     */
    public String getFormattedReceipt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a");
        StringBuilder sb = new StringBuilder();
        sb.append("\n+======================================+\n");
        sb.append("|      BREW AND BLISS CAFE             |\n");
        sb.append("|    \"Where Every Sip Counts\"          |\n");
        sb.append("+======================================+\n");
        sb.append(String.format("|  Order #%-5d   Table: %-12s|\n", orderId, tableNumber));
        sb.append(String.format("|  Customer: %-26s|\n", customer.getName()));
        sb.append(String.format("|  Date: %-30s|\n", createdAt.format(fmt)));
        sb.append("+======================================+\n");
        sb.append("|  ITEMS ORDERED:                      |\n");
        for (OrderItem oi : items) {
            String line = String.format("  %-20s x%d  $%.2f",
                    oi.getMenuItem().getName(), oi.getQuantity(), oi.getSubtotal());
            sb.append(String.format("| %-38s |\n", line));
        }
        sb.append("+======================================+\n");
        sb.append(String.format("|  Subtotal:              $%-13.2f|\n", getSubtotal()));
        sb.append(String.format("|  Tax (8%%):              $%-13.2f|\n", getTax()));
        sb.append("|  ----------------------------------  |\n");
        sb.append(String.format("|  TOTAL:                 $%-13.2f|\n", getTotal()));
        sb.append("+======================================+\n");
        sb.append("|  Thank you for visiting us! :)       |\n");
        sb.append("|     Come again soon!                 |\n");
        sb.append("+======================================+\n");
        return sb.toString();
    }

    /**
     * Returns a compact one-line summary of this order.
     *
     * <p><b>OOP Concept:</b> Method Overriding — overrides {@code Object.toString()}.</p>
     *
     * @return formatted order summary string
     */
    @Override
    public String toString() {
        return String.format("  Order #%d | Table %-3s | %-9s | %d item(s) | Total: $%.2f",
                orderId, tableNumber, status, items.size(), getTotal());
    }
}
