package cafe.ui;

import cafe.exception.AuthenticationException;
import cafe.exception.InvalidInputException;
import cafe.exception.ItemNotFoundException;
import cafe.model.*;
import cafe.service.*;
import cafe.util.ConsoleUtil;

import java.util.*;

/**
 * Main UI controller for the Brew and Bliss Cafe Management System.
 *
 * <p>Handles all user interaction, routing between the Customer Portal
 * and the Admin Dashboard, and calls service layer methods.</p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Exception Handling</b> — try-catch-finally on all risky operations</li>
 *   <li><b>Polymorphism</b> — Person references used for Customer and Staff display</li>
 *   <li><b>Abstraction</b> — UI layer calls service methods without knowing internals</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 4.0
 */
public class CafeApp {

    private final MenuService     menuService     = new MenuService();
    private final CustomerService customerService = new CustomerService();
    private final OrderService    orderService    = new OrderService();
    private final StaffService    staffService    = new StaffService();

    // ── Admin credentials ──────────────────────────────────────────────────────
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "cafe123";

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOT
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Starts the application — shows banner then goes to login screen.
     */
    public void start() {
        printBanner();
        ConsoleUtil.pressEnter();
        loginScreen();
    }

    private void printBanner() {
        System.out.println(ConsoleUtil.CYAN + ConsoleUtil.BOLD);
        System.out.println("  +================================================+");
        System.out.println("  |                                                |");
        System.out.println("  |       BREW AND BLISS  CAFE                     |");
        System.out.println("  |       Management System  v4.0                  |");
        System.out.println("  |       OOP Java Project                         |");
        System.out.println("  |                                                |");
        System.out.println("  |   \"Where Every Sip Tells a Story\"             |");
        System.out.println("  |                                                |");
        System.out.println("  +================================================+");
        System.out.println(ConsoleUtil.RESET);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LOGIN SCREEN
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Shows the login screen and routes to the appropriate portal.
     */
    private void loginScreen() {
        while (true) {
            ConsoleUtil.clearScreen();
            ConsoleUtil.printHeader("WELCOME  --  Please Select Login Type");
            System.out.println();
            ConsoleUtil.printMenuOption(1, "Customer  --  View menu and place your order");
            ConsoleUtil.printMenuOption(2, "Admin     --  Full system access");
            ConsoleUtil.printMenuOption(0, "Exit");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Who are you");
            switch (choice) {
                case 1 -> customerLogin();
                case 2 -> adminLogin();
                case 0 -> { farewell(); return; }
                default -> ConsoleUtil.printError("Please choose 1 or 2.");
            }
        }
    }

    // ── Customer Login ─────────────────────────────────────────────────────────

    /**
     * Handles customer login — finds existing customer or registers new one.
     *
     * <p><b>OOP Concept:</b> Exception Handling — try-catch around
     * {@link InvalidInputException} thrown by registerCustomer.</p>
     */
    private void customerLogin() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("CUSTOMER LOGIN");
        System.out.println();
        ConsoleUtil.printInfo("Welcome! Tell us your name to get started.");
        System.out.println();

        String name = ConsoleUtil.prompt("Your name");

        try {
            if (name.isEmpty()) {
                throw new InvalidInputException("name", name, "Name cannot be empty");
            }

            List<Customer> found = customerService.searchByName(name);
            Customer customer;

            if (!found.isEmpty()) {
                customer = found.get(0);
                System.out.println();
                ConsoleUtil.printSuccess("Welcome back, " + customer.getName() + "! :)");
                ConsoleUtil.printInfo("Your loyalty points: * " + customer.getLoyaltyPoints());
            } else {
                System.out.println();
                ConsoleUtil.printInfo("New here! Let us get you registered.");
                String phone = ConsoleUtil.prompt("Phone number");
                String email = ConsoleUtil.prompt("Email (press ENTER to skip)");
                customer = customerService.registerCustomer(
                    name,
                    phone.isEmpty() ? "--" : phone,
                    email.isEmpty() ? "--" : email
                );
                ConsoleUtil.printSuccess("Welcome to Brew and Bliss, " + customer.getName() + "!");
            }

            ConsoleUtil.pressEnter();
            customerPortal(customer);

        } catch (InvalidInputException e) {
            // Custom exception caught -- display the validation message
            ConsoleUtil.printError(e.getMessage());
            ConsoleUtil.pressEnter();
        }
    }

    // ── Admin Login ────────────────────────────────────────────────────────────

    /**
     * Handles admin authentication using credentials.
     *
     * <p><b>OOP Concept:</b> Exception Handling — throws and catches
     * {@link AuthenticationException} on failed login.</p>
     */
    private void adminLogin() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("ADMIN LOGIN");
        System.out.println();

        String username = ConsoleUtil.prompt("Username");
        String password = ConsoleUtil.prompt("Password");

        try {
            if (!username.equals(ADMIN_USERNAME) || !password.equals(ADMIN_PASSWORD)) {
                throw new AuthenticationException(username);
            }
            ConsoleUtil.printSuccess("Access granted! Welcome back, Admin.");
            ConsoleUtil.pressEnter();
            adminDashboard();

        } catch (AuthenticationException e) {
            // Custom exception caught -- inform user without exposing details
            ConsoleUtil.printError(e.getMessage());
            ConsoleUtil.printWarning("Hint: username = admin | password = cafe123");
            ConsoleUtil.pressEnter();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CUSTOMER PORTAL
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Customer portal -- limited to menu viewing and order placement.
     *
     * @param customer the logged-in customer
     */
    private void customerPortal(Customer customer) {
        while (true) {
            ConsoleUtil.clearScreen();
            ConsoleUtil.printHeader("Welcome, " + customer.getName() + "!");
            System.out.println();
            System.out.printf("  * Loyalty Points : %d pts%n", customer.getLoyaltyPoints());
            System.out.printf("  Orders Placed    : %d%n",
                orderService.getOrdersByCustomer(customer.getCustomerId()).size());
            ConsoleUtil.printDivider();
            System.out.println();
            ConsoleUtil.printMenuOption(1, "View Menu");
            ConsoleUtil.printMenuOption(2, "Place an Order");
            ConsoleUtil.printMenuOption(3, "My Order History");
            ConsoleUtil.printMenuOption(0, "Logout");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> customerViewMenu();
                case 2 -> customerPlaceOrder(customer);
                case 3 -> customerOrderHistory(customer);
                case 0 -> {
                    ConsoleUtil.printInfo("Goodbye, " + customer.getName() + "! Come back soon.");
                    ConsoleUtil.pressEnter();
                    return;
                }
                default -> ConsoleUtil.printError("Please choose a valid option.");
            }
        }
    }

    /** Shows the menu to customers — available items only. */
    private void customerViewMenu() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("Our Menu");
        System.out.println();
        menuService.getMenuByCategory().forEach((category, items) -> {
            ConsoleUtil.printSubHeader(getCategoryLabel(category) + "  " + category);
            System.out.printf("  %-5s  %-24s  %s%n", "ID", "Item", "Price");
            System.out.println("  " + "-".repeat(40));
            items.stream().filter(MenuItem::isAvailable).forEach(item -> {
                System.out.printf("  [%2d]  %-24s  $%.2f%n",
                    item.getId(), item.getName(), item.getPrice());
                System.out.printf("        %s%s%s%n",
                    ConsoleUtil.CYAN, item.getDescription(), ConsoleUtil.RESET);
            });
            System.out.println();
        });
        ConsoleUtil.pressEnter();
    }

    /**
     * Handles customer self-ordering flow.
     *
     * <p><b>OOP Concept:</b> Exception Handling — catches ItemNotFoundException
     * when an invalid Item ID is entered, and InvalidInputException on bad qty.</p>
     *
     * @param customer the ordering customer
     */
    private void customerPlaceOrder(Customer customer) {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("Place Your Order");
        System.out.println();

        String tableNo = ConsoleUtil.prompt("Your table number");

        Order order;
        try {
            order = orderService.createOrder(customer, tableNo);
        } catch (InvalidInputException e) {
            ConsoleUtil.printError(e.getMessage());
            ConsoleUtil.pressEnter();
            return;
        }

        // Quick menu reference
        System.out.println();
        ConsoleUtil.printSubHeader("Quick Menu (available items)");
        menuService.getMenuByCategory().forEach((cat, items) -> {
            System.out.println("  " + ConsoleUtil.YELLOW + getCategoryLabel(cat)
                + " " + cat + ConsoleUtil.RESET);
            items.stream().filter(MenuItem::isAvailable).forEach(i ->
                System.out.printf("    [%2d] %-24s $%.2f%n",
                    i.getId(), i.getName(), i.getPrice()));
        });

        System.out.println();
        ConsoleUtil.printInfo("Enter Item ID to add. Type 'done' when finished.");
        System.out.println();

        while (true) {
            String input = ConsoleUtil.prompt("Item ID (or 'done')");
            if (input.equalsIgnoreCase("done")) break;

            try {
                int itemId = Integer.parseInt(input);
                // findByIdOrThrow -- uses ItemNotFoundException
                MenuItem item = menuService.findByIdOrThrow(itemId);

                if (!item.isAvailable()) {
                    ConsoleUtil.printWarning("Sorry, \"" + item.getName()
                        + "\" is not available right now.");
                    continue;
                }

                int qty = ConsoleUtil.promptInt("How many " + item.getName() + "?");
                String note = ConsoleUtil.prompt("Special request? (or ENTER to skip)");

                // addItemToOrder -- throws InvalidInputException if qty < 1
                orderService.addItemToOrder(order, item, qty, note);
                ConsoleUtil.printSuccess("Added " + qty + "x " + item.getName()
                    + "  -->  $" + String.format("%.2f", item.getPrice() * qty));

            } catch (NumberFormatException e) {
                ConsoleUtil.printError("Please enter a valid number.");
            } catch (ItemNotFoundException e) {
                // Custom exception caught
                ConsoleUtil.printError(e.getMessage());
            } catch (InvalidInputException e) {
                // Custom exception caught
                ConsoleUtil.printError(e.getMessage());
            }
        }

        if (order.isEmpty()) {
            ConsoleUtil.printWarning("No items added. Order cancelled.");
            orderService.cancelOrder(order.getOrderId());
            ConsoleUtil.pressEnter();
            return;
        }

        // Order summary
        System.out.println();
        ConsoleUtil.printSubHeader("Your Order Summary");
        order.getItems().forEach(System.out::println);
        System.out.println();
        ConsoleUtil.printDivider();
        System.out.printf("  Subtotal :  $%.2f%n", order.getSubtotal());
        System.out.printf("  Tax (8%%) :  $%.2f%n", order.getTax());
        System.out.printf("  " + ConsoleUtil.BOLD + "TOTAL    :  $%.2f"
            + ConsoleUtil.RESET + "%n", order.getTotal());
        ConsoleUtil.printDivider();
        System.out.println();

        String confirm = ConsoleUtil.prompt("Confirm your order? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            order.setStatus(Order.Status.PREPARING);
            customer.addOrderToHistory(order.getOrderId());
            customerService.awardLoyaltyPoints(customer, order.getTotal());
            int pts = (int) order.getTotal();
            orderService.save();
            System.out.println();
            ConsoleUtil.printSuccess("Order #" + order.getOrderId()
                + " placed! We are preparing it now.");
            ConsoleUtil.printInfo("You earned * " + pts + " loyalty points!");
        } else {
            orderService.cancelOrder(order.getOrderId());
            ConsoleUtil.printInfo("Order cancelled.");
        }
        ConsoleUtil.pressEnter();
    }

    /** Shows a customer their own order history. */
    private void customerOrderHistory(Customer customer) {
        ConsoleUtil.printHeader("My Order History");
        List<Order> myOrders = orderService.getOrdersByCustomer(customer.getCustomerId());
        if (myOrders.isEmpty()) {
            ConsoleUtil.printInfo("No orders yet. Go grab a coffee!");
        } else {
            myOrders.forEach(o -> {
                System.out.println(o);
                o.getItems().forEach(i -> System.out.println("       " + i));
                System.out.println();
            });
        }
        ConsoleUtil.pressEnter();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN DASHBOARD
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Admin dashboard -- full access to all modules.
     */
    private void adminDashboard() {
        while (true) {
            showAdminDashboard();
            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> adminMenuManagement();
                case 2 -> orderManagement();
                case 3 -> customerManagement();
                case 4 -> staffManagement();
                case 5 -> salesReports();
                case 0 -> {
                    ConsoleUtil.printInfo("Logged out. Goodbye, Admin!");
                    ConsoleUtil.pressEnter();
                    return;
                }
                default -> ConsoleUtil.printError("Invalid option.");
            }
        }
    }

    private void showAdminDashboard() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("ADMIN DASHBOARD  --  Brew and Bliss");
        System.out.println();
        System.out.printf("  Active Orders  : %-5d   Customers      : %-5d%n",
            orderService.getActiveOrders().size(), customerService.getTotalCustomers());
        System.out.printf("  Staff on Duty  : %-5d   Today Revenue  : $%.2f%n",
            staffService.getOnDutyCount(), orderService.getTodayRevenue());
        System.out.printf("  Menu Items     : %-5d   Total Revenue  : $%.2f%n",
            menuService.getCount(), orderService.getTotalRevenue());
        ConsoleUtil.printDivider();
        System.out.println();
        ConsoleUtil.printMenuOption(1, "Menu Management   (Add / Edit / Delete items)");
        ConsoleUtil.printMenuOption(2, "Order Management");
        ConsoleUtil.printMenuOption(3, "Customer Management");
        ConsoleUtil.printMenuOption(4, "Staff Management");
        ConsoleUtil.printMenuOption(5, "Sales Reports");
        ConsoleUtil.printMenuOption(0, "Logout");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN -- MENU MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private void adminMenuManagement() {
        while (true) {
            ConsoleUtil.printHeader("Admin -- Menu Management");
            ConsoleUtil.printMenuOption(1, "View Full Menu");
            ConsoleUtil.printMenuOption(2, "Browse by Category");
            ConsoleUtil.printMenuOption(3, "Search Item");
            ConsoleUtil.printMenuOption(4, "Add New Item");
            ConsoleUtil.printMenuOption(5, "Edit Item Price");
            ConsoleUtil.printMenuOption(6, "Toggle Item Availability");
            ConsoleUtil.printMenuOption(7, "Delete Item");
            ConsoleUtil.printMenuOption(0, "<-- Back");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> adminViewFullMenu();
                case 2 -> browseByCategory();
                case 3 -> searchMenu();
                case 4 -> adminAddMenuItem();
                case 5 -> adminEditItemPrice();
                case 6 -> adminToggleAvailability();
                case 7 -> adminDeleteMenuItem();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Invalid option.");
            }
        }
    }

    private void adminViewFullMenu() {
        ConsoleUtil.printHeader("Full Menu -- Admin View");
        System.out.println();
        menuService.getMenuByCategory().forEach((category, items) -> {
            ConsoleUtil.printSubHeader(getCategoryLabel(category) + "  " + category);
            System.out.printf("  %-5s  %-24s  %-10s  %s%n", "ID", "Name", "Price", "Status");
            System.out.println("  " + "-".repeat(52));
            items.forEach(item -> {
                String status = item.isAvailable()
                    ? ConsoleUtil.GREEN  + "[Available]"   + ConsoleUtil.RESET
                    : ConsoleUtil.RED    + "[Unavailable]" + ConsoleUtil.RESET;
                System.out.printf("  [%2d]  %-24s  $%-9.2f  %s%n",
                    item.getId(), item.getName(), item.getPrice(), status);
            });
            System.out.println();
        });
        ConsoleUtil.pressEnter();
    }

    /**
     * Admin adds a new item -- validates input, catches InvalidInputException.
     *
     * <p><b>OOP Concept:</b> Exception Handling with custom exception.</p>
     */
    private void adminAddMenuItem() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.printHeader("Add New Menu Item");
        System.out.println();

        try {
            String name = ConsoleUtil.prompt("Item name");

            System.out.println();
            ConsoleUtil.printMenuOption(1, "Hot Beverages");
            ConsoleUtil.printMenuOption(2, "Cold Beverages");
            ConsoleUtil.printMenuOption(3, "Pastries");
            ConsoleUtil.printMenuOption(4, "Food");
            ConsoleUtil.printMenuOption(5, "Other (type manually)");
            System.out.println();
            int catChoice = ConsoleUtil.promptInt("Category");
            String category = switch (catChoice) {
                case 1 -> "Hot Beverages";
                case 2 -> "Cold Beverages";
                case 3 -> "Pastries";
                case 4 -> "Food";
                default -> ConsoleUtil.prompt("Type category name");
            };

            double price = ConsoleUtil.promptDouble("Price ($)");
            String desc  = ConsoleUtil.prompt("Short description");

            System.out.println();
            System.out.printf("  Name     : %s%n", name);
            System.out.printf("  Category : %s%n", category);
            System.out.printf("  Price    : $%.2f%n", price);
            System.out.printf("  Desc     : %s%n", desc);
            System.out.println();

            String confirm = ConsoleUtil.prompt("Add this item? (yes/no)");
            if (confirm.equalsIgnoreCase("yes")) {
                // throws InvalidInputException if invalid
                MenuItem item = menuService.addMenuItem(name, category, price, desc);
                ConsoleUtil.printSuccess("\"" + item.getName()
                    + "\" added! (ID: " + item.getId() + ")");
            } else {
                ConsoleUtil.printInfo("Cancelled.");
            }

        } catch (InvalidInputException e) {
            ConsoleUtil.printError("Validation failed: " + e.getMessage());
        } finally {
            // finally block always runs
            ConsoleUtil.pressEnter();
        }
    }

    /**
     * Admin edits an item's price -- catches ItemNotFoundException.
     *
     * <p><b>OOP Concept:</b> Exception Handling with custom exception.</p>
     */
    private void adminEditItemPrice() {
        ConsoleUtil.printSubHeader("Edit Item Price");
        adminViewFullMenu();
        int id = ConsoleUtil.promptInt("Enter Item ID to edit");

        try {
            MenuItem item = menuService.findByIdOrThrow(id);
            System.out.printf("  Current price of \"%s\": $%.2f%n",
                item.getName(), item.getPrice());
            double newPrice = ConsoleUtil.promptDouble("New price ($)");
            if (newPrice <= 0) throw new InvalidInputException("price",
                String.valueOf(newPrice), "Price must be greater than zero");
            item.setPrice(newPrice);
            menuService.save();
            ConsoleUtil.printSuccess("\"" + item.getName()
                + "\" updated to $" + String.format("%.2f", newPrice));

        } catch (ItemNotFoundException | InvalidInputException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void adminToggleAvailability() {
        ConsoleUtil.printSubHeader("Toggle Availability");
        int id = ConsoleUtil.promptInt("Enter Item ID");
        try {
            MenuItem item = menuService.findByIdOrThrow(id);
            item.setAvailable(!item.isAvailable());
            menuService.save();
            String status = item.isAvailable() ? "[Available]" : "[Unavailable]";
            ConsoleUtil.printSuccess("\"" + item.getName() + "\" is now " + status);
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    /**
     * Admin deletes an item -- requires typing DELETE to confirm.
     *
     * <p><b>OOP Concept:</b> Exception Handling -- ItemNotFoundException.</p>
     */
    private void adminDeleteMenuItem() {
        ConsoleUtil.printSubHeader("Delete Menu Item");
        adminViewFullMenu();
        int id = ConsoleUtil.promptInt("Enter Item ID to DELETE");

        try {
            MenuItem item = menuService.findByIdOrThrow(id);
            System.out.println();
            ConsoleUtil.printWarning("About to permanently delete: \""
                + item.getName() + "\"  ($"
                + String.format("%.2f", item.getPrice()) + ")");
            String confirm = ConsoleUtil.prompt("Type DELETE to confirm");
            if (confirm.equals("DELETE")) {
                menuService.remove(id);
                ConsoleUtil.printSuccess("\"" + item.getName() + "\" removed.");
            } else {
                ConsoleUtil.printInfo("Cancelled. Item was NOT deleted.");
            }
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    // ── Shared Menu Helpers ────────────────────────────────────────────────────

    private void browseByCategory() {
        List<String> cats = menuService.getCategories();
        for (int i = 0; i < cats.size(); i++)
            ConsoleUtil.printMenuOption(i + 1, getCategoryLabel(cats.get(i)) + "  " + cats.get(i));
        int choice = ConsoleUtil.promptInt("Pick category (0 to cancel)");
        if (choice < 1 || choice > cats.size()) return;
        menuService.findByCategory(cats.get(choice - 1)).forEach(System.out::println);
        ConsoleUtil.pressEnter();
    }

    private void searchMenu() {
        String kw = ConsoleUtil.prompt("Search keyword");
        List<MenuItem> res = menuService.searchByName(kw);
        if (res.isEmpty()) ConsoleUtil.printWarning("No items found for \"" + kw + "\".");
        else res.forEach(System.out::println);
        ConsoleUtil.pressEnter();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN -- ORDER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private void orderManagement() {
        while (true) {
            ConsoleUtil.printHeader("Order Management");
            ConsoleUtil.printMenuOption(1, "Take New Order");
            ConsoleUtil.printMenuOption(2, "View Active Orders");
            ConsoleUtil.printMenuOption(3, "Update Order Status");
            ConsoleUtil.printMenuOption(4, "Complete Order and Print Receipt");
            ConsoleUtil.printMenuOption(5, "Cancel Order");
            ConsoleUtil.printMenuOption(6, "Order History");
            ConsoleUtil.printMenuOption(0, "<-- Back");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> adminTakeOrder();
                case 2 -> { ConsoleUtil.printHeader("Active Orders"); orderService.getActiveOrders().forEach(System.out::println); ConsoleUtil.pressEnter(); }
                case 3 -> adminUpdateStatus();
                case 4 -> adminCompleteOrder();
                case 5 -> adminCancelOrder();
                case 6 -> { ConsoleUtil.printHeader("Order History"); orderService.getAll().forEach(System.out::println); ConsoleUtil.pressEnter(); }
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Invalid option.");
            }
        }
    }

    private void adminTakeOrder() {
        ConsoleUtil.printSubHeader("Take New Order");
        String customerName = ConsoleUtil.prompt("Customer name (ENTER for walk-in)");
        Customer customer;
        try {
            if (customerName.isEmpty()) {
                customer = new Customer("Walk-in Guest", "--", "--");
            } else {
                List<Customer> found = customerService.searchByName(customerName);
                if (!found.isEmpty()) {
                    customer = found.get(0);
                    ConsoleUtil.printInfo("Found: " + customer.getName()
                        + " | Points: " + customer.getLoyaltyPoints());
                } else {
                    String phone = ConsoleUtil.prompt("Phone");
                    customer = customerService.registerCustomer(customerName, phone, "--");
                    ConsoleUtil.printSuccess("Registered: " + customer.getName());
                }
            }

            String tableNo = ConsoleUtil.prompt("Table number");
            Order order = orderService.createOrder(customer, tableNo);

            while (true) {
                String input = ConsoleUtil.prompt("Item ID ('menu' to browse, 'done' to finish)");
                if (input.equalsIgnoreCase("done")) break;
                if (input.equalsIgnoreCase("menu")) { adminViewFullMenu(); continue; }
                try {
                    int itemId = Integer.parseInt(input);
                    MenuItem item = menuService.findByIdOrThrow(itemId);
                    if (!item.isAvailable()) { ConsoleUtil.printWarning("Item unavailable."); continue; }
                    int qty = ConsoleUtil.promptInt("Quantity");
                    String note = ConsoleUtil.prompt("Special note (or ENTER)");
                    orderService.addItemToOrder(order, item, qty, note);
                    ConsoleUtil.printSuccess("Added " + qty + "x " + item.getName());
                } catch (NumberFormatException e) {
                    ConsoleUtil.printError("Enter a valid Item ID.");
                } catch (ItemNotFoundException | InvalidInputException e) {
                    ConsoleUtil.printError(e.getMessage());
                }
            }

            if (order.isEmpty()) {
                ConsoleUtil.printWarning("No items. Order cancelled.");
                orderService.cancelOrder(order.getOrderId());
            } else {
                order.getItems().forEach(System.out::println);
                System.out.printf("%n  Total: $%.2f%n", order.getTotal());
                order.setStatus(Order.Status.PREPARING);
                orderService.save();
                ConsoleUtil.printSuccess("Order #" + order.getOrderId() + " placed!");
            }

        } catch (InvalidInputException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void adminUpdateStatus() {
        ConsoleUtil.printSubHeader("Update Order Status");
        int orderId = ConsoleUtil.promptInt("Order ID");
        try {
            Order order = orderService.findByIdOrThrow(orderId);
            System.out.println("  Current: " + order.getStatus());
            System.out.println("  [1] PENDING  [2] PREPARING  [3] READY  [4] COMPLETED");
            int c = ConsoleUtil.promptInt("New status");
            Order.Status s = switch (c) {
                case 1 -> Order.Status.PENDING;
                case 2 -> Order.Status.PREPARING;
                case 3 -> Order.Status.READY;
                case 4 -> Order.Status.COMPLETED;
                default -> order.getStatus();
            };
            orderService.updateOrderStatus(orderId, s);
            ConsoleUtil.printSuccess("Order #" + orderId + " --> " + s);
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void adminCompleteOrder() {
        ConsoleUtil.printSubHeader("Complete Order and Print Receipt");
        int orderId = ConsoleUtil.promptInt("Order ID");
        try {
            Order order = orderService.findByIdOrThrow(orderId);
            if (order.getStatus() == Order.Status.COMPLETED) {
                ConsoleUtil.printWarning("Already completed.");
                return;
            }
            orderService.completeOrder(orderId);
            customerService.awardLoyaltyPoints(order.getCustomer(), order.getTotal());
            System.out.println(order.getFormattedReceipt());
            ConsoleUtil.printSuccess(order.getCustomer().getName()
                + " earned " + (int) order.getTotal() + " loyalty points! *");
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void adminCancelOrder() {
        int orderId = ConsoleUtil.promptInt("Order ID to cancel");
        if (orderService.cancelOrder(orderId))
            ConsoleUtil.printSuccess("Order #" + orderId + " cancelled.");
        else
            ConsoleUtil.printError("Could not cancel -- not found or already completed.");
        ConsoleUtil.pressEnter();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN -- CUSTOMER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private void customerManagement() {
        while (true) {
            ConsoleUtil.printHeader("Customer Management");
            ConsoleUtil.printMenuOption(1, "View All Customers");
            ConsoleUtil.printMenuOption(2, "Search Customer");
            ConsoleUtil.printMenuOption(3, "Register New Customer");
            ConsoleUtil.printMenuOption(4, "View Customer Orders");
            ConsoleUtil.printMenuOption(5, "Top Loyalty Customers");
            ConsoleUtil.printMenuOption(0, "<-- Back");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> {
                    ConsoleUtil.printHeader("All Customers");
                    // Polymorphism: getDetails() called on Person reference
                    customerService.getAll().forEach(c ->
                        System.out.println(c));
                    ConsoleUtil.pressEnter();
                }
                case 2 -> {
                    String k = ConsoleUtil.prompt("Name keyword");
                    customerService.searchByName(k).forEach(System.out::println);
                    ConsoleUtil.pressEnter();
                }
                case 3 -> {
                    try {
                        String n = ConsoleUtil.prompt("Name");
                        String p = ConsoleUtil.prompt("Phone");
                        String e = ConsoleUtil.prompt("Email");
                        Customer c = customerService.registerCustomer(n, p, e);
                        ConsoleUtil.printSuccess("Registered: " + c.getName());
                    } catch (InvalidInputException e) {
                        ConsoleUtil.printError(e.getMessage());
                    } finally {
                        ConsoleUtil.pressEnter();
                    }
                }
                case 4 -> {
                    try {
                        int id = ConsoleUtil.promptInt("Customer ID");
                        customerService.findByIdOrThrow(id);
                        orderService.getOrdersByCustomer(id).forEach(System.out::println);
                    } catch (ItemNotFoundException e) {
                        ConsoleUtil.printError(e.getMessage());
                    } finally {
                        ConsoleUtil.pressEnter();
                    }
                }
                case 5 -> {
                    ConsoleUtil.printHeader("Top Loyalty Customers");
                    int[] rank = {1};
                    customerService.getTopCustomers(5).forEach(c ->
                        System.out.printf("  %d. %-22s -- %d pts%n",
                            rank[0]++, c.getName(), c.getLoyaltyPoints()));
                    ConsoleUtil.pressEnter();
                }
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Invalid.");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN -- STAFF MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private void staffManagement() {
        while (true) {
            ConsoleUtil.printHeader("Staff Management");
            ConsoleUtil.printMenuOption(1, "View All Staff");
            ConsoleUtil.printMenuOption(2, "Staff On Duty");
            ConsoleUtil.printMenuOption(3, "Add Staff Member");
            ConsoleUtil.printMenuOption(4, "Toggle On/Off Duty");
            ConsoleUtil.printMenuOption(5, "Remove Staff Member");
            ConsoleUtil.printMenuOption(0, "<-- Back");
            System.out.println();

            int choice = ConsoleUtil.promptInt("Your choice");
            switch (choice) {
                case 1 -> {
                    ConsoleUtil.printHeader("All Staff");
                    staffService.getAll().forEach(System.out::println);
                    ConsoleUtil.pressEnter();
                }
                case 2 -> {
                    ConsoleUtil.printSubHeader("On Duty Now");
                    staffService.getOnDutyStaff().forEach(System.out::println);
                    ConsoleUtil.pressEnter();
                }
                case 3 -> addStaff();
                case 4 -> toggleDuty();
                case 5 -> removeStaff();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Invalid.");
            }
        }
    }

    private void addStaff() {
        try {
            String name = ConsoleUtil.prompt("Full name");
            System.out.println("  [1] Manager  [2] Barista  [3] Cashier  [4] Waiter");
            int r = ConsoleUtil.promptInt("Role");
            Staff.Role role = switch (r) {
                case 1 -> Staff.Role.MANAGER;
                case 2 -> Staff.Role.BARISTA;
                case 3 -> Staff.Role.CASHIER;
                default -> Staff.Role.WAITER;
            };
            String shift = ConsoleUtil.prompt("Shift (Morning/Evening/Night)");
            double rate  = ConsoleUtil.promptDouble("Hourly rate ($)");
            Staff s = staffService.addStaff(name, role, shift, rate);
            ConsoleUtil.printSuccess(s.getName() + " joined the team! " + s.getRoleLabel());
        } catch (InvalidInputException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void toggleDuty() {
        staffService.getAll().forEach(System.out::println);
        int id = ConsoleUtil.promptInt("Staff ID");
        try {
            Staff s = staffService.findByIdOrThrow(id);
            staffService.toggleDuty(id);
            ConsoleUtil.printSuccess(s.getName() + " is now "
                + (s.isOnDuty() ? "[ON] On Duty" : "[OFF] Off Duty"));
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    private void removeStaff() {
        try {
            int id = ConsoleUtil.promptInt("Staff ID");
            Staff s = staffService.findByIdOrThrow(id);
            String c = ConsoleUtil.prompt("Remove " + s.getName() + "? (yes/no)");
            if (c.equalsIgnoreCase("yes")) {
                staffService.remove(id);
                ConsoleUtil.printSuccess("Staff member removed.");
            }
        } catch (ItemNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } finally {
            ConsoleUtil.pressEnter();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN -- SALES REPORTS
    // ═══════════════════════════════════════════════════════════════════════════

    private void salesReports() {
        ConsoleUtil.printHeader("Sales Report");
        System.out.println();
        System.out.printf("  Today Revenue  :  $%.2f%n", orderService.getTodayRevenue());
        System.out.printf("  Total Revenue  :  $%.2f%n", orderService.getTotalRevenue());
        System.out.printf("  Orders Today   :  %d%n",    orderService.getTotalOrdersToday());
        System.out.printf("  Total Orders   :  %d%n",    orderService.getTotalOrders());

        ConsoleUtil.printSubHeader("Revenue by Category");
        Map<String, Double> rev = orderService.getRevenueByCategory();
        if (rev.isEmpty()) ConsoleUtil.printInfo("No completed orders yet.");
        else rev.forEach((cat, r) ->
            System.out.printf("  %-20s -->  $%.2f%n", getCategoryLabel(cat) + " " + cat, r));

        ConsoleUtil.printSubHeader("Best-Selling Items");
        Map<String, Integer> sellers = orderService.getBestSellers();
        if (sellers.isEmpty()) ConsoleUtil.printInfo("No sales data yet.");
        else {
            int[] rank = {1};
            sellers.forEach((n, q) ->
                System.out.printf("  %d. %-22s -- %d sold%n", rank[0]++, n, q));
        }

        ConsoleUtil.printSubHeader("Customer Stats");
        System.out.printf("  Total Customers: %d%n", customerService.getTotalCustomers());
        customerService.getTopCustomers(3).forEach(c ->
            System.out.printf("  * %-20s -- %d points%n", c.getName(), c.getLoyaltyPoints()));

        ConsoleUtil.pressEnter();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Returns a short plain-text category label for display.
     *
     * @param category the category name
     * @return a short bracketed label
     */
    private String getCategoryLabel(String category) {
        return switch (category) {
            case "Hot Beverages"  -> "[HOT]";
            case "Cold Beverages" -> "[COLD]";
            case "Pastries"       -> "[PASTRY]";
            case "Food"           -> "[FOOD]";
            default               -> "[OTHER]";
        };
    }

    private void farewell() {
        ConsoleUtil.clearScreen();
        System.out.println(ConsoleUtil.CYAN + ConsoleUtil.BOLD);
        System.out.println("  +==========================================+");
        System.out.println("  |                                          |");
        System.out.println("  |   Thanks for using Brew and Bliss!       |");
        System.out.println("  |   May your coffee always be perfect!     |");
        System.out.println("  |                                          |");
        System.out.println("  +==========================================+");
        System.out.println(ConsoleUtil.RESET);
    }
}
