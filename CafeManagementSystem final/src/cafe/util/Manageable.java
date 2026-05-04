package cafe.util;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining standard management operations for any service class.
 *
 * <p>Any class that manages a collection of entities (menu items, customers,
 * staff, orders) should implement this interface to ensure a consistent
 * API across all service layers.</p>
 *
 * <p><b>OOP Concept:</b> Abstraction via Interface — defines WHAT operations
 * must be available without specifying HOW they are implemented.
 * Each service class provides its own implementation.</p>
 *
 * @param <T>  the type of entity being managed
 * @param <ID> the type of the entity's identifier (usually Integer)
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public interface Manageable<T, ID> {

    /**
     * Returns all entities currently managed by this service.
     *
     * @return a List containing all entities
     */
    List<T> getAll();

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier to search for
     * @return an Optional containing the entity if found, or empty if not
     */
    Optional<T> findById(ID id);

    /**
     * Removes an entity from the system by its unique identifier.
     *
     * @param id the unique identifier of the entity to remove
     * @return true if the entity was found and removed, false otherwise
     */
    boolean remove(ID id);

    /**
     * Returns the total number of entities currently managed.
     *
     * @return the count of entities
     */
    int getCount();
}
