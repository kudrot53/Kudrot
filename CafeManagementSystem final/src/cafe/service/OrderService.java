package cafe.service;

import cafe.exception.InvalidInputException;
import cafe.exception.ItemNotFoundException;
import cafe.model.*;
import cafe.util.FileManager;
import cafe.util.Manageable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class managing all order-related operations and sales reporting.
 *
 * <p>Implements {@link Manageable} and persists data via {@link FileManager}.</p>
 *
 * <p><b>OOP Concepts:</b> Interface implementation, exception handling,
 * file I/O, Collections (ArrayList, HashMap, LinkedHashMap), streams.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class OrderService implements Manageable<Order, Integer> {

    /** In-memory list of all orders. */
    private List<Order> orders;

    /**
     * Constructs OrderService, loading saved orders from disk.
     */
    public OrderService() {
        List<Order> saved = FileManager.loadOrders();
        orders = saved.isEmpty() ? new ArrayList<>() : saved;
    }

    // ── Manageable Implementation ─────────────────────────────────────────────

    /** @return all orders in the system */
    @Override
    public List<Order> getAll() { return new ArrayList<>(orders); }

    /**
     * Finds an order by its unique ID.
     *
     * @param id the order ID
     * @return Optional containing the order if found
     */
    @Override
    public Optional<Order> findById(Integer id) {
        return orders.stream().filter(o -> o.getOrderId() == id).findFirst();
    }

    /**
     * Removes an order by ID.
     *
     * @param id the order ID to remove
     * @return true if removed
     */
    @Override
    public boolean remove(Integer id) {
        boolean removed = orders.removeIf(o -> o.getOrderId() == id);
        if (removed) save();
        return removed;
    }

    /** @return total number of orders */
    @Override
    public int getCount() { return orders.size(); }

    // ── Business Operations ───────────────────────────────────────────────────

    /**
     * Creates a new order for a customer at a table.
     *
     * @param customer    the customer placing the order
     * @param tableNumber the table number (must not be blank)
     * @return the newly created Order
     * @throws InvalidInputException if table number is blank
     */
    public Order createOrder(Customer customer, String tableNumber) {
        if (tableNumber == null || tableNumber.isBlank())
            throw new InvalidInputException("tableNumber", tableNumber, "Table number cannot be empty");
        Order order = new Order(customer, tableNumber.trim());
        orders.add(order);
        save();
        return order;
    }

    /**
     * Adds a menu item to an order with a given quantity.
     *
     * <p><b>OOP Concept:</b> Method Overloading — two versions of addItemToOrder.</p>
     *
     * @param order    the order to add to
     * @param item     the menu item to add
     * @param quantity the quantity (must be at least 1)
     * @throws InvalidInputException if quantity is less than 1
     */
    public void addItemToOrder(Order order, MenuItem item, int quantity) {
        if (quantity < 1)
            throw new InvalidInputException("quantity", String.valueOf(quantity), "Must be at least 1");
        order.addItem(new OrderItem(item, quantity));
        save();
    }

    /**
     * Adds a menu item to an order with a quantity and special note.
     *
     * <p><b>OOP Concept:</b> Method Overloading — same name, extra parameter.</p>
     *
     * @param order       the order to add to
     * @param item        the menu item to add
     * @param quantity    the quantity (must be at least 1)
     * @param specialNote any special instruction from the customer
     * @throws InvalidInputException if quantity is less than 1
     */
    public void addItemToOrder(Order order, MenuItem item, int quantity, String specialNote) {
        if (quantity < 1)
            throw new InvalidInputException("quantity", String.valueOf(quantity), "Must be at least 1");
        order.addItem(new OrderItem(item, quantity, specialNote));
        save();
    }

    /**
     * Marks an order as completed and records the timestamp.
     *
     * @param orderId the order ID to complete
     * @return true if completed successfully
     * @throws ItemNotFoundException if no order has that ID
     */
    public boolean completeOrder(int orderId) throws ItemNotFoundException {
        Order order = findByIdOrThrow(orderId);
        order.complete();
        save();
        return true;
    }

    /**
     * Cancels an active order.
     *
     * @param orderId the order ID to cancel
     * @return true if cancelled, false if already completed or not found
     */
    public boolean cancelOrder(int orderId) {
        Optional<Order> opt = findById(orderId);
        if (opt.isPresent() && opt.get().getStatus() != Order.Status.COMPLETED) {
            opt.get().cancel();
            save();
            return true;
        }
        return false;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the order ID
     * @param status  the new status to apply
     * @return true if updated
     * @throws ItemNotFoundException if no order has that ID
     */
    public boolean updateOrderStatus(int orderId, Order.Status status) throws ItemNotFoundException {
        Order order = findByIdOrThrow(orderId);
        order.setStatus(status);
        save();
        return true;
    }

    /**
     * Finds an order by ID, throwing if not found.
     *
     * @param id the order ID
     * @return the found Order
     * @throws ItemNotFoundException if no order has that ID
     */
    public Order findByIdOrThrow(int id) throws ItemNotFoundException {
        return findById(id).orElseThrow(() -> new ItemNotFoundException("Order", id));
    }

    /**
     * Returns all orders with an active status (PENDING, PREPARING, READY).
     *
     * @return list of active orders
     */
    public List<Order> getActiveOrders() {
        return orders.stream()
                .filter(o -> o.getStatus() == Order.Status.PENDING
                          || o.getStatus() == Order.Status.PREPARING
                          || o.getStatus() == Order.Status.READY)
                .collect(Collectors.toList());
    }

    /**
     * Returns all completed orders.
     *
     * @return list of completed orders
     */
    public List<Order> getCompletedOrders() {
        return orders.stream()
                .filter(o -> o.getStatus() == Order.Status.COMPLETED)
                .collect(Collectors.toList());
    }

    /**
     * Returns all orders placed by a specific customer.
     *
     * @param customerId the customer's ID
     * @return list of that customer's orders
     */
    public List<Order> getOrdersByCustomer(int customerId) {
        return orders.stream()
                .filter(o -> o.getCustomer().getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    // ── Sales Reporting ───────────────────────────────────────────────────────

    /**
     * Calculates total revenue from all completed orders.
     *
     * @return total revenue in dollars
     */
    public double getTotalRevenue() {
        return getCompletedOrders().stream().mapToDouble(Order::getTotal).sum();
    }

    /**
     * Calculates total revenue from completed orders placed today.
     *
     * @return today's revenue in dollars
     */
    public double getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return getCompletedOrders().stream()
                .filter(o -> o.getCreatedAt().toLocalDate().equals(today))
                .mapToDouble(Order::getTotal)
                .sum();
    }

    /**
     * Returns revenue totals grouped by menu category.
     *
     * @return map of category name to total revenue
     */
    public Map<String, Double> getRevenueByCategory() {
        Map<String, Double> revenue = new TreeMap<>();
        for (Order order : getCompletedOrders()) {
            for (OrderItem oi : order.getItems()) {
                revenue.merge(oi.getMenuItem().getCategory(), oi.getSubtotal(), Double::sum);
            }
        }
        return revenue;
    }

    /**
     * Returns the top 5 best-selling items by quantity sold.
     *
     * @return LinkedHashMap of item name to quantity sold, sorted descending
     */
    public Map<String, Integer> getBestSellers() {
        Map<String, Integer> sales = new HashMap<>();
        for (Order order : getCompletedOrders()) {
            for (OrderItem oi : order.getItems()) {
                sales.merge(oi.getMenuItem().getName(), oi.getQuantity(), Integer::sum);
            }
        }
        return sales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Returns the number of orders placed today.
     *
     * @return count of today's orders
     */
    public int getTotalOrdersToday() {
        LocalDate today = LocalDate.now();
        return (int) orders.stream()
                .filter(o -> o.getCreatedAt().toLocalDate().equals(today))
                .count();
    }

    /** @return total number of orders ever placed */
    public int getTotalOrders() { return orders.size(); }

    // ── Persistence ───────────────────────────────────────────────────────────

    /**
     * Persists all orders to disk.
     */
    public void save() {
        FileManager.saveOrders(orders);
    }
}
