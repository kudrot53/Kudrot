package cafe.model;

import java.io.Serializable;

/**
 * Represents a cafe staff member (Barista, Cashier, or Waiter).
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
public class Staff extends Person implements Serializable {

    private static final long serialVersionUID = 3L;

    /** Roles available to staff members. */
    public enum Role { MANAGER, BARISTA, CASHIER, WAITER }

    /** Auto-incrementing counter for unique staff IDs. Static — shared across all instances. */
    protected static int idCounter = 200;

    /** Unique staff identifier. */
    protected int staffId;

    /** The staff member's assigned role. */
    private Role role;

    /** The shift this staff member works (Morning / Evening / Night). */
    private String shift;

    /** The staff member's hourly pay rate in dollars. */
    private double hourlyRate;

    /** Whether this staff member is currently on duty. */
    private boolean onDuty;

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Default constructor required for serialization.
     */
    public Staff() {
        super();
        this.onDuty = false;
    }

    /**
     * Parameterized constructor — creates a new staff member.
     *
     * <p>Calls {@code super(name, ...)} to initialize inherited Person fields,
     * demonstrating use of the {@code super} keyword.</p>
     *
     * @param name       the staff member's full name
     * @param phone      the contact phone number
     * @param email      the email address
     * @param role       the staff role (BARISTA, CASHIER, WAITER)
     * @param shift      the work shift (Morning/Evening/Night)
     * @param hourlyRate the pay rate per hour
     */
    public Staff(String name, String phone, String email,
                 Role role, String shift, double hourlyRate) {
        super(name, phone, email); // calls Person constructor via super
        this.staffId    = ++idCounter;
        this.role       = role;
        this.shift      = shift;
        this.hourlyRate = hourlyRate;
        this.onDuty     = false;
    }

    /**
     * Convenience constructor — creates staff without phone/email (backward compatible).
     *
     * <p><b>OOP Concept:</b> Method/Constructor Overloading — same constructor name,
     * different parameter lists.</p>
     *
     * @param name       the staff member's full name
     * @param role       the staff role
     * @param shift      the work shift
     * @param hourlyRate the pay rate per hour
     */
    public Staff(String name, Role role, String shift, double hourlyRate) {
        this(name, "--", "--", role, shift, hourlyRate); // constructor chaining
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return the unique staff ID */
    public int getStaffId()       { return staffId; }

    /** @return the staff member's role */
    public Role getRole()         { return role; }

    /** @return the assigned shift */
    public String getShift()      { return shift; }

    /** @return the hourly pay rate */
    public double getHourlyRate() { return hourlyRate; }

    /** @return true if staff is currently on duty */
    public boolean isOnDuty()     { return onDuty; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param onDuty set to true if staff is starting their shift */
    public void setOnDuty(boolean onDuty)   { this.onDuty = onDuty; }

    /** @param rate the new hourly rate */
    public void setHourlyRate(double rate)  { this.hourlyRate = rate; }

    /** @param shift the new shift assignment */
    public void setShift(String shift)      { this.shift = shift; }

    /** @param role the new role for this staff member */
    public void setRole(Role role)          { this.role = role; }

    // ── Abstract Method Implementations ───────────────────────────────────────

    /**
     * Returns the staff member's unique ID.
     *
     * <p><b>OOP Concept:</b> Implements abstract method from {@link Person}.</p>
     *
     * @return the staff ID
     */
    @Override
    public int getId() {
        return staffId;
    }

    /**
     * Returns full formatted details for this staff member.
     *
     * <p><b>OOP Concept:</b> Overrides abstract method from {@link Person} —
     * Runtime Polymorphism.</p>
     *
     * @return formatted staff details string
     */
    @Override
    public String getDetails() {
        return String.format(
            "Staff ID   : S%03d%nName       : %s%nRole       : %s%nShift      : %s%n"
          + "Rate       : $%.2f/hr%nOn Duty    : %s",
            staffId, getName(), role, shift, hourlyRate, onDuty ? "Yes" : "No"
        );
    }

    /**
     * Returns role label for display.
     *
     * @return a short label string for the role
     */
    public String getRoleLabel() {
        return switch (role) {
            case MANAGER -> "[MGR]";
            case BARISTA -> "[BAR]";
            case CASHIER -> "[CSH]";
            case WAITER  -> "[WTR]";
        };
    }

    /**
     * Returns a compact one-line summary of this staff member.
     *
     * <p><b>OOP Concept:</b> Overrides {@code toString()} from {@link Person}.</p>
     *
     * @return formatted one-line staff summary
     */
    @Override
    public String toString() {
        String duty = onDuty ? "[ON] On Duty" : "[OFF] Off Duty";
        return String.format("  [S%03d] %s %-18s | %-9s | %-8s | $%.2f/hr | %s",
                staffId, getRoleLabel(), getName(), role, shift, hourlyRate, duty);
    }
}
