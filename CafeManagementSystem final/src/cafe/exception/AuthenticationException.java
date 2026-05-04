package cafe.exception;

/**
 * Exception thrown when an authentication attempt fails.
 *
 * <p>This exception is raised when an admin provides incorrect credentials
 * during the login process.</p>
 *
 * <p><b>OOP Concept:</b> Custom Exception — extends {@link Exception},
 * demonstrating a meaningful exception hierarchy for different failure types.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class AuthenticationException extends Exception {

    /** The username that failed authentication. */
    private final String attemptedUsername;

    /**
     * Constructs an AuthenticationException for a failed login attempt.
     *
     * @param attemptedUsername the username that was entered incorrectly
     */
    public AuthenticationException(String attemptedUsername) {
        super("Authentication failed for username: '" + attemptedUsername
                + "'. Invalid username or password.");
        this.attemptedUsername = attemptedUsername;
    }

    /**
     * Returns the username that failed authentication.
     *
     * @return the attempted username
     */
    public String getAttemptedUsername() {
        return attemptedUsername;
    }
}
