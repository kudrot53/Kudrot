package cafe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cafe customer with loyalty points and order history.
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Inheritance</b> — extends {@link Person} (IS-A Person)</li>
 *   <li><b>Encapsulation</b> — all fields private with getters/setters</li>
 *   <li><b>Polymorphism</b> — overrides {@code getDetails()} and {@code toString()}</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class Customer extends Person implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Auto-incrementing counter for unique customer IDs. Static shared across all instances. */
    private static int idCounter = 100;

    /** Unique customer identifier. */
    private int customerId;

    /** Accumulated loyalty points (1 point per $1 spent). */
    private int loyaltyPoints;

    /** List of order IDs associated with this customer. */
    private List<Integer> orderHistory;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Default constructor required for serialization.
     */
    public Customer() {
        super();
        this.loyaltyPoints = 0;
        this.orderHistory  = new ArrayList<>();
    }

    /**
     * Parameterized constructor — registers a new customer.
     *
     * <p>Uses {@code super()} to call the parent {@link Person} constructor,
     * demonstrating constructor chaining via inheritance.</p>
     *
     * @param name  the customer's full name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(String name, String phone, String email) {
        super(name, phone, email); // calls Person constructor
        this.customerId    = ++idCounter;
        this.loyaltyPoints = 0;
        this.orderHistory  = new ArrayList<>();
    }

    /**
     * Copy constructor — creates a new Customer with the same data as another.
     *
     * @param other the Customer to copy from
     */
    public Customer(Customer other) {
        super(other.getName(), other.getPhone(), other.getEmail());
        this.customerId    = other.customerId;
        this.loyaltyPoints = other.loyaltyPoints;
        this.orderHistory  = new ArrayList<>(other.orderHistory);
    }

    // ── Business Logic ────────────────────────────────────────────────────────

    /**
     * Adds an order ID to this customer's history.
     *
     * @param orderId the ID of the completed order
     */
    public void addOrderToHistory(int orderId) {
        orderHistory.add(orderId);
    }

    /**
     * Adds loyalty points to this customer's balance.
     *
     * @param points the number of points to add (must be positive)
     */
    public void addLoyaltyPoints(int points) {
        if (points > 0) this.loyaltyPoints += points;
    }

    /**
     * Attempts to redeem loyalty points from this customer's balance.
     *
     * @param points the number of points to redeem
     * @return true if the customer had enough points and they were deducted
     */
    public boolean redeemLoyaltyPoints(int points) {
        if (loyaltyPoints >= points) {
            loyaltyPoints -= points;
            return true;
        }
        return false;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the unique customer ID.
     * @return the customer ID
     */
    public int getCustomerId()           { return customerId; }

    /**
     * Returns the accumulated loyalty points.
     * @return loyalty points balance
     */
    public int getLoyaltyPoints()        { return loyaltyPoints; }

    /**
     * Returns the list of order IDs placed by this customer.
     * @return order history list
     */
    public List<Integer> getOrderHistory() { return orderHistory; }

    // ── Abstract Method Implementations ───────────────────────────────────────

    /**
     * Returns the customer's unique ID.
     *
     * <p><b>OOP Concept:</b> Implements abstract method from {@link Person}.</p>
     *
     * @return the customer ID
     */
    @Override
    public int getId() {
        return customerId;
    }

    /**
     * Returns a full formatted detail string for this customer.
     *
     * <p><b>OOP Concept:</b> Overrides abstract method from {@link Person} —
     * Runtime Polymorphism.</p>
     *
     * @return formatted customer details
     */
    @Override
    public String getDetails() {
        return String.format(
            "Customer ID : C%03d%nName        : %s%nPhone       : %s%nEmail       : %s%n"
          + "Loyalty Pts : %d%nOrders      : %d",
            customerId, getName(), getPhone(), getEmail(),
            loyaltyPoints, orderHistory.size()
        );
    }

    /**
     * Returns a compact one-line summary of the customer.
     *
     * <p><b>OOP Concept:</b> Overrides {@code toString()} from {@link Person} —
     * Method Overriding / Polymorphism.</p>
     *
     * @return formatted one-line customer summary
     */
    @Override
    public String toString() {
        return String.format("  [C%03d] %-20s | Phone: %-13s | Points: %-5d | Orders: %d",
                customerId, getName(), getPhone(), loyaltyPoints, orderHistory.size());
    }
}
