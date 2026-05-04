package cafe.model;

import java.io.Serializable;

/**
 * Abstract base class representing any person in the cafe system.
 *
 * <p>This class forms the root of the inheritance hierarchy:
 * <pre>
 *   Person  (abstract)
 *   +-- Customer
 *   +-- Staff
 *       +-- Manager
 * </pre>
 * </p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Abstraction</b> — abstract class with abstract method getDetails()</li>
 *   <li><b>Encapsulation</b> — all fields private, accessed via getters/setters</li>
 *   <li><b>Inheritance</b> — Customer and Staff extend this class</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The person's full name. */
    private String name;

    /** The person's contact phone number. */
    private String phone;

    /** The person's email address. */
    private String email;

    /**
     * Default constructor — required for serialization.
     */
    public Person() {
        this.name  = "";
        this.phone = "";
        this.email = "";
    }

    /**
     * Parameterized constructor — initializes all basic person fields.
     *
     * @param name  the full name of the person
     * @param phone the contact phone number
     * @param email the email address
     */
    public Person(String name, String phone, String email) {
        this.name  = name;
        this.phone = phone;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Returns the person's full name.
     * @return the name
     */
    public String getName()  { return name; }

    /**
     * Returns the person's phone number.
     * @return the phone number
     */
    public String getPhone() { return phone; }

    /**
     * Returns the person's email address.
     * @return the email address
     */
    public String getEmail() { return email; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /**
     * Sets the person's full name.
     * @param name the new name (must not be null or empty)
     */
    public void setName(String name)   { this.name = name; }

    /**
     * Sets the person's phone number.
     * @param phone the new phone number
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Sets the person's email address.
     * @param email the new email address
     */
    public void setEmail(String email) { this.email = email; }

    // ── Abstract Methods ─────────────────────────────────────────────────────

    /**
     * Returns a formatted string with the person's full details.
     *
     * <p><b>OOP Concept:</b> Abstract method — every subclass MUST provide
     * its own implementation of how its details are displayed.</p>
     *
     * @return a formatted detail string specific to the subclass
     */
    public abstract String getDetails();

    /**
     * Returns a unique identifier for this person.
     *
     * <p><b>OOP Concept:</b> Abstract method — each subclass has its own
     * ID type (customerId, staffId) which it returns here.</p>
     *
     * @return the unique integer ID of this person
     */
    public abstract int getId();

    // ── Concrete Method ───────────────────────────────────────────────────────

    /**
     * Returns a brief greeting string using the person's name.
     * This is a concrete method shared by all subclasses.
     *
     * @return a greeting message
     */
    public String greet() {
        return "Hello, " + name + "! Welcome to Brew and Bliss Cafe.";
    }

    /**
     * Returns a basic string representation of this person.
     * Subclasses override this with more detailed output.
     *
     * @return the person's name and contact info
     */
    @Override
    public String toString() {
        return String.format("Name: %-20s | Phone: %-13s | Email: %s", name, phone, email);
    }
}
