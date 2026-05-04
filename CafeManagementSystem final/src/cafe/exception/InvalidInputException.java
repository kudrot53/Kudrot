package cafe.exception;

/**
 * Exception thrown when a user provides input that fails validation rules.
 *
 * <p>Examples include: empty name fields, negative prices, zero quantities,
 * invalid table numbers, or out-of-range menu IDs.</p>
 *
 * <p><b>OOP Concept:</b> Custom Exception — Inheritance from RuntimeException,
 * making it an unchecked exception suitable for input validation failures.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class InvalidInputException extends RuntimeException {

    /** The field name where the invalid input occurred. */
    private final String fieldName;

    /** The invalid value that was provided. */
    private final String invalidValue;

    /**
     * Constructs an InvalidInputException with field name and invalid value.
     *
     * @param fieldName    the name of the field that failed validation
     * @param invalidValue the value that was rejected
     * @param reason       the reason the value was rejected
     */
    public InvalidInputException(String fieldName, String invalidValue, String reason) {
        super("Invalid input for '" + fieldName + "': '" + invalidValue + "' — " + reason);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    /**
     * Constructs an InvalidInputException with a simple message.
     *
     * @param message the detail message describing what was invalid
     */
    public InvalidInputException(String message) {
        super(message);
        this.fieldName = "unknown";
        this.invalidValue = "";
    }

    /**
     * Returns the name of the field that failed validation.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the value that was considered invalid.
     *
     * @return the invalid value as a string
     */
    public String getInvalidValue() {
        return invalidValue;
    }
}
