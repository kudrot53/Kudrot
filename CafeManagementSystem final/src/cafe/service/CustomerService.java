package cafe.service;

import cafe.exception.InvalidInputException;
import cafe.exception.ItemNotFoundException;
import cafe.model.Customer;
import cafe.util.FileManager;
import cafe.util.Manageable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class managing all customer-related operations.
 *
 * <p>Implements {@link Manageable} and persists data via {@link FileManager}.</p>
 *
 * <p><b>OOP Concepts:</b> Interface implementation, exception handling, file I/O,
 * Collections (ArrayList, streams).</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class CustomerService implements Manageable<Customer, Integer> {

    /** In-memory list of all registered customers. */
    private List<Customer> customers;

    /**
     * Constructs CustomerService, loading saved data or seeding sample customers.
     */
    public CustomerService() {
        List<Customer> saved = FileManager.loadCustomers();
        if (!saved.isEmpty()) {
            customers = saved;
        } else {
            customers = new ArrayList<>();
            seedSampleCustomers();
            save();
        }
    }

    /** Seeds sample customers for demo purposes. */
    private void seedSampleCustomers() {
        Customer c1 = new Customer("Alice Johnson",  "555-0101", "alice@email.com");
        Customer c2 = new Customer("Bob Martinez",   "555-0102", "bob@email.com");
        Customer c3 = new Customer("Clara Thompson", "555-0103", "clara@email.com");
        c1.addLoyaltyPoints(120);
        c2.addLoyaltyPoints(45);
        c3.addLoyaltyPoints(200);
        customers.addAll(List.of(c1, c2, c3));
    }

    // ── Manageable Implementation ─────────────────────────────────────────────

    /** @return all registered customers */
    @Override
    public List<Customer> getAll() { return new ArrayList<>(customers); }

    /**
     * Finds a customer by their unique ID.
     *
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    @Override
    public Optional<Customer> findById(Integer id) {
        return customers.stream().filter(c -> c.getCustomerId() == id).findFirst();
    }

    /**
     * Removes a customer by ID.
     *
     * @param id the customer ID to remove
     * @return true if removed successfully
     */
    @Override
    public boolean remove(Integer id) {
        boolean removed = customers.removeIf(c -> c.getCustomerId() == id);
        if (removed) save();
        return removed;
    }

    /** @return total number of registered customers */
    @Override
    public int getCount() { return customers.size(); }

    // ── Business Operations ───────────────────────────────────────────────────

    /**
     * Registers a new customer with validation.
     *
     * @param name  the customer's name (must not be blank)
     * @param phone the phone number
     * @param email the email address
     * @return the newly registered Customer
     * @throws InvalidInputException if name is blank
     */
    public Customer registerCustomer(String name, String phone, String email) {
        if (name == null || name.isBlank())
            throw new InvalidInputException("name", name, "Customer name cannot be empty");
        Customer c = new Customer(name.trim(), phone, email);
        customers.add(c);
        save();
        return c;
    }

    /**
     * Finds a customer by ID, throwing if not found.
     *
     * @param id the customer ID
     * @return the found Customer
     * @throws ItemNotFoundException if no customer has that ID
     */
    public Customer findByIdOrThrow(int id) throws ItemNotFoundException {
        return findById(id).orElseThrow(() -> new ItemNotFoundException("Customer", id));
    }

    /**
     * Searches customers whose names contain the given keyword.
     *
     * @param keyword the search keyword (case-insensitive)
     * @return list of matching customers
     */
    public List<Customer> searchByName(String keyword) {
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the top N customers ranked by loyalty points.
     *
     * @param limit maximum number of customers to return
     * @return sorted list of top customers
     */
    public List<Customer> getTopCustomers(int limit) {
        return customers.stream()
                .sorted((a, b) -> b.getLoyaltyPoints() - a.getLoyaltyPoints())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Awards loyalty points to a customer based on their order total.
     * 1 point is awarded per $1 spent.
     *
     * @param customer   the customer to award points to
     * @param orderTotal the total amount spent
     */
    public void awardLoyaltyPoints(Customer customer, double orderTotal) {
        customer.addLoyaltyPoints((int) orderTotal);
        save();
    }

    /** @return total number of registered customers */
    public int getTotalCustomers() { return customers.size(); }

    // ── Persistence ───────────────────────────────────────────────────────────

    /**
     * Persists all customer data to disk.
     */
    public void save() {
        FileManager.saveCustomers(customers);
    }
}
