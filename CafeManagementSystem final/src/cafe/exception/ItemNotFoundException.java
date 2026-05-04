package cafe.exception;

/**
 * Exception thrown when a requested menu item, order, customer,
 * or staff member cannot be found in the system.
 *
 * <p>This is a checked exception that extends {@link Exception},
 * forcing callers to handle the case where a record does not exist.</p>
 *
 * <p><b>OOP Concept:</b> Custom Exception — Inheritance from Exception class.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class ItemNotFoundException extends Exception {

    /** The ID that was searched but not found. */
    private final int searchedId;

    /** The type of entity that was not found (e.g., "MenuItem", "Customer"). */
    private final String entityType;

    /**
     * Constructs an ItemNotFoundException with an entity type and ID.
     *
     * @param entityType the type of entity that was not found
     * @param searchedId the ID that was searched for
     */
    public ItemNotFoundException(String entityType, int searchedId) {
        super(entityType + " with ID " + searchedId + " was not found in the system.");
        this.entityType = entityType;
        this.searchedId = searchedId;
    }

    /**
     * Constructs an ItemNotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public ItemNotFoundException(String message) {
        super(message);
        this.entityType = "Unknown";
        this.searchedId = -1;
    }

    /**
     * Returns the ID that was searched but not found.
     *
     * @return the searched ID
     */
    public int getSearchedId() {
        return searchedId;
    }

    /**
     * Returns the type of entity that was not found.
     *
     * @return the entity type string
     */
    public String getEntityType() {
        return entityType;
    }
}
