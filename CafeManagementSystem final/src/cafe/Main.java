package cafe;

import cafe.ui.CafeApp;
import cafe.util.FileManager;

/**
 * Entry point for the Brew and Bliss Cafe Management System.
 *
 * <p>Initialises the data directory and launches the main application.</p>
 *
 * <p><b>System:</b> Brew and Bliss Cafe Management System v4.0</p>
 * <p><b>Course:</b> Object-Oriented Programming using Java</p>
 * <p><b>Features:</b> Full OOP — Encapsulation, Inheritance (3 levels),
 * Polymorphism, Abstraction, Custom Exceptions, File I/O, Interfaces.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 4.0
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Ensure the data/ folder exists before any service loads
        FileManager.ensureDataDirectory();

        CafeApp app = new CafeApp();
        app.start();
    }
}
