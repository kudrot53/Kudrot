package cafe.model;

import java.io.Serializable;

/**
 * Represents a cafe Manager — a specialized Staff member with elevated privileges.
 *
 * <p>This class forms the 2nd level of the inheritance hierarchy:</p>
 * <pre>
 *   Person  (abstract)       ← Level 0: Root abstract class
 *   +-- Staff                ← Level 1: General staff
 *       +-- Manager          ← Level 2: Specialised staff (this class)
 * </pre>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Inheritance (2 levels)</b> — Manager IS-A Staff IS-A Person</li>
 *   <li><b>Polymorphism</b> — overrides {@code getDetails()} and {@code toString()}</li>
 *   <li><b>super keyword</b> — used to call Staff and Person constructors</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class Manager extends Staff implements Serializable {

    private static final long serialVersionUID = 4L;

    /** The department or area this manager is responsible for. */
    private String department;

    /** The manager's access level (1 = junior manager, 2 = senior manager). */
    private int accessLevel;

    /** Admin username used for system login. */
    private String username;

    /** Admin password hash used for system login. */
    private String passwordHash;

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Default constructor required for serialization.
     */
    public Manager() {
        super();
        this.department  = "General";
        this.accessLevel = 1;
    }

    /**
     * Full constructor — creates a Manager with all required fields.
     *
     * <p>Calls {@code super(...)} to initialize Staff and Person fields,
     * demonstrating multi-level constructor chaining with {@code super}.</p>
     *
     * @param name        the manager's full name
     * @param phone       the contact phone number
     * @param email       the email address
     * @param shift       the work shift
     * @param hourlyRate  the pay rate per hour
     * @param department  the department this manager oversees
     * @param accessLevel the system access level (1 or 2)
     * @param username    the admin login username
     * @param passwordHash the hashed admin password
     */
    public Manager(String name, String phone, String email, String shift,
                   double hourlyRate, String department, int accessLevel,
                   String username, String passwordHash) {
        super(name, phone, email, Role.MANAGER, shift, hourlyRate); // calls Staff constructor
        this.department   = department;
        this.accessLevel  = accessLevel;
        this.username     = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Convenience constructor with default department and access level.
     *
     * <p><b>OOP Concept:</b> Constructor Overloading — different parameter lists.</p>
     *
     * @param name        the manager's full name
     * @param shift       the work shift
     * @param hourlyRate  the pay rate per hour
     * @param username    the admin login username
     * @param passwordHash the hashed admin password
     */
    public Manager(String name, String shift, double hourlyRate,
                   String username, String passwordHash) {
        this(name, "--", "--", shift, hourlyRate, "Operations", 2, username, passwordHash);
    }

    // ── Business Logic ────────────────────────────────────────────────────────

    /**
     * Validates admin login credentials.
     *
     * @param inputUsername the username entered at login
     * @param inputPassword the password entered at login (will be hashed)
     * @return true if credentials match
     */
    public boolean authenticate(String inputUsername, String inputPassword) {
        String hashed = hashPassword(inputPassword);
        return this.username.equals(inputUsername) && this.passwordHash.equals(hashed);
    }

    /**
     * Simple password hashing using Java's built-in hashCode.
     *
     * <p>In a production system this would use BCrypt or SHA-256.
     * For coursework, this demonstrates the concept of password hashing.</p>
     *
     * @param password the plain-text password to hash
     * @return a hashed string representation
     */
    public static String hashPassword(String password) {
        return "HASH_" + Math.abs(password.hashCode());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    /** @return the department this manager oversees */
    public String getDepartment()  { return department; }

    /** @return the system access level */
    public int getAccessLevel()    { return accessLevel; }

    /** @return the admin username */
    public String getUsername()    { return username; }

    /** @param department the new department */
    public void setDepartment(String department) { this.department = department; }

    /** @param level the new access level */
    public void setAccessLevel(int level)        { this.accessLevel = level; }

    // ── Overridden Methods ────────────────────────────────────────────────────

    /**
     * Returns full details including manager-specific fields.
     *
     * <p><b>OOP Concept:</b> Overrides {@code getDetails()} from both
     * {@link Staff} and {@link Person} — demonstrating multi-level polymorphism.
     * Uses {@code super.getDetails()} to include parent class details.</p>
     *
     * @return formatted manager detail string
     */
    @Override
    public String getDetails() {
        return super.getDetails()  // calls Staff.getDetails() which has Person data
             + String.format("%nDepartment : %s%nAccess Lvl : %d%nUsername   : %s",
                             department, accessLevel, username);
    }

    /**
     * Returns a compact one-line manager summary.
     *
     * <p><b>OOP Concept:</b> Overrides {@code toString()} from {@link Staff}.</p>
     *
     * @return formatted manager summary string
     */
    @Override
    public String toString() {
        String duty = isOnDuty() ? "[ON] On Duty" : "[OFF] Off Duty";
        return String.format("  [S%03d] [MGR] %-16s | MANAGER   | %-8s | $%.2f/hr | %s | Dept: %s",
                staffId, getName(), getShift(), getHourlyRate(), duty, department);
    }
}
