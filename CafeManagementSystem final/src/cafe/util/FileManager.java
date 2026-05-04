package cafe.util;

import cafe.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for saving and loading all cafe data to/from files.
 *
 * <p>Uses Java Object Serialization to persist data between program runs.
 * All data is stored in the {@code data/} folder as {@code .ser} binary files.</p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Static Methods</b> — all methods are static utility methods</li>
 *   <li><b>File I/O</b> — uses ObjectOutputStream and ObjectInputStream</li>
 *   <li><b>Exception Handling</b> — try-catch-finally blocks throughout</li>
 *   <li><b>Generics</b> — type-safe save/load operations</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class FileManager {

    /** Directory where all data files are stored. */
    private static final String DATA_DIR = "data/";

    /** File path for menu items. */
    public static final String MENU_FILE      = DATA_DIR + "menu.ser";

    /** File path for customer records. */
    public static final String CUSTOMERS_FILE = DATA_DIR + "customers.ser";

    /** File path for order records. */
    public static final String ORDERS_FILE    = DATA_DIR + "orders.ser";

    /** File path for staff records. */
    public static final String STAFF_FILE     = DATA_DIR + "staff.ser";

    /**
     * Private constructor — prevents instantiation of this utility class.
     * <p><b>OOP Concept:</b> Utility class pattern with only static members.</p>
     */
    private FileManager() {}

    /**
     * Ensures the data directory exists, creating it if necessary.
     * Called once at application startup.
     */
    public static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("  [i]  Data directory created at: " + DATA_DIR);
            }
        }
    }

    // ── Generic Save / Load ───────────────────────────────────────────────────

    /**
     * Saves a list of serializable objects to a file.
     *
     * <p><b>OOP Concept:</b> File I/O using ObjectOutputStream (Serialization).
     * try-catch-finally ensures the stream is always closed.</p>
     *
     * @param <T>      the type of objects in the list
     * @param data     the list of objects to save
     * @param filePath the path of the file to write to
     * @return true if saved successfully, false if an error occurred
     */
    public static <T extends Serializable> boolean saveList(List<T> data, String filePath) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.err.println("  [!]  Could not save data to " + filePath + ": " + e.getMessage());
            return false;
        } finally {
            // finally block — always closes the stream even if an exception occurred
            if (oos != null) {
                try { oos.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Loads a list of serializable objects from a file.
     *
     * <p><b>OOP Concept:</b> File I/O using ObjectInputStream (Deserialization).
     * Returns an empty list if the file does not exist yet.</p>
     *
     * @param <T>      the type of objects in the list
     * @param filePath the path of the file to read from
     * @return the loaded list, or an empty list if file not found
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("  [!]  Could not load data from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (ois != null) {
                try { ois.close(); } catch (IOException ignored) {}
            }
        }
    }

    // ── Specific Save Methods ─────────────────────────────────────────────────

    /**
     * Saves all menu items to the menu data file.
     *
     * @param menuItems the list of MenuItems to persist
     * @return true if saved successfully
     */
    public static boolean saveMenuItems(List<MenuItem> menuItems) {
        return saveList(menuItems, MENU_FILE);
    }

    /**
     * Saves all customer records to the customers data file.
     *
     * @param customers the list of Customers to persist
     * @return true if saved successfully
     */
    public static boolean saveCustomers(List<Customer> customers) {
        return saveList(customers, CUSTOMERS_FILE);
    }

    /**
     * Saves all order records to the orders data file.
     *
     * @param orders the list of Orders to persist
     * @return true if saved successfully
     */
    public static boolean saveOrders(List<Order> orders) {
        return saveList(orders, ORDERS_FILE);
    }

    /**
     * Saves all staff records to the staff data file.
     *
     * @param staffList the list of Staff to persist
     * @return true if saved successfully
     */
    public static boolean saveStaff(List<Staff> staffList) {
        return saveList(staffList, STAFF_FILE);
    }

    // ── Specific Load Methods ─────────────────────────────────────────────────

    /**
     * Loads menu items from the data file.
     *
     * @return list of MenuItems, or empty list if no file exists
     */
    public static List<MenuItem> loadMenuItems() {
        return loadList(MENU_FILE);
    }

    /**
     * Loads customer records from the data file.
     *
     * @return list of Customers, or empty list if no file exists
     */
    public static List<Customer> loadCustomers() {
        return loadList(CUSTOMERS_FILE);
    }

    /**
     * Loads order records from the data file.
     *
     * @return list of Orders, or empty list if no file exists
     */
    public static List<Order> loadOrders() {
        return loadList(ORDERS_FILE);
    }

    /**
     * Loads staff records from the data file.
     *
     * @return list of Staff, or empty list if no file exists
     */
    public static List<Staff> loadStaff() {
        return loadList(STAFF_FILE);
    }

    /**
     * Checks whether saved data files exist (i.e., the system has been run before).
     *
     * @return true if at least the menu file exists
     */
    public static boolean hasSavedData() {
        return new File(MENU_FILE).exists();
    }

    /**
     * Deletes all saved data files — used for a full system reset.
     *
     * @return true if all files were deleted successfully
     */
    public static boolean deleteAllData() {
        boolean ok = true;
        for (String path : new String[]{MENU_FILE, CUSTOMERS_FILE, ORDERS_FILE, STAFF_FILE}) {
            File f = new File(path);
            if (f.exists()) ok &= f.delete();
        }
        return ok;
    }
}
