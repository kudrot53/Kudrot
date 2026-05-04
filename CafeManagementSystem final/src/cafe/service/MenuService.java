package cafe.service;

import cafe.exception.InvalidInputException;
import cafe.exception.ItemNotFoundException;
import cafe.model.MenuItem;
import cafe.util.FileManager;
import cafe.util.Manageable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class managing all menu-related operations.
 *
 * <p>Implements {@link Manageable} to provide a consistent CRUD API.
 * Handles loading from and saving to disk via {@link FileManager}.</p>
 *
 * <p><b>OOP Concepts demonstrated:</b></p>
 * <ul>
 *   <li><b>Interface Implementation</b> — implements Manageable</li>
 *   <li><b>Exception Handling</b> — throws and catches custom exceptions</li>
 *   <li><b>File I/O</b> — loads/saves data via FileManager</li>
 *   <li><b>Collections</b> — uses ArrayList, LinkedHashMap, streams</li>
 * </ul>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class MenuService implements Manageable<MenuItem, Integer> {

    /** In-memory list of all menu items. */
    private List<MenuItem> menuItems;

    /** Auto-incrementing ID counter for new items. */
    private int idCounter = 1;

    /** Defined display order for menu categories. */
    private static final List<String> CATEGORY_ORDER =
            List.of("Hot Beverages", "Cold Beverages", "Pastries", "Food");

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs the MenuService, loading saved data or seeding defaults.
     */
    public MenuService() {
        menuItems = new ArrayList<>();
        List<MenuItem> saved = FileManager.loadMenuItems();
        if (!saved.isEmpty()) {
            menuItems = saved;
            // Restore idCounter to max existing ID + 1
            idCounter = menuItems.stream().mapToInt(MenuItem::getId).max().orElse(0) + 1;
        } else {
            loadDefaultMenu();
            save();
        }
    }

    // ── Default Menu Seed ─────────────────────────────────────────────────────

    /**
     * Seeds the system with a default set of menu items on first run.
     */
    private void loadDefaultMenu() {
        addRaw("Espresso",         "Hot Beverages",  2.50, "Rich and bold single shot");
        addRaw("Cappuccino",       "Hot Beverages",  4.00, "Espresso with steamed milk foam");
        addRaw("Latte",            "Hot Beverages",  4.50, "Smooth espresso and steamed milk");
        addRaw("Americano",        "Hot Beverages",  3.50, "Espresso diluted with hot water");
        addRaw("Flat White",       "Hot Beverages",  4.25, "Velvety microfoam espresso");
        addRaw("Hot Chocolate",    "Hot Beverages",  3.75, "Creamy Belgian chocolate");
        addRaw("Iced Latte",       "Cold Beverages", 5.00, "Chilled espresso and milk over ice");
        addRaw("Cold Brew",        "Cold Beverages", 5.50, "12-hour slow-steeped coffee");
        addRaw("Frappuccino",      "Cold Beverages", 6.00, "Blended iced coffee treat");
        addRaw("Iced Matcha",      "Cold Beverages", 5.25, "Japanese green tea over ice");
        addRaw("Lemonade",         "Cold Beverages", 3.50, "Freshly squeezed lemon");
        addRaw("Croissant",        "Pastries",        3.00, "Buttery flaky French pastry");
        addRaw("Blueberry Muffin", "Pastries",        2.75, "Bursting with fresh blueberries");
        addRaw("Chocolate Cake",   "Pastries",        4.50, "Decadent triple-layer cake");
        addRaw("Cheesecake",       "Pastries",        4.75, "New York style baked cheesecake");
        addRaw("Banana Bread",     "Pastries",        3.25, "Moist homemade banana loaf");
        addRaw("Club Sandwich",    "Food",            7.50, "Triple-decker toasted sandwich");
        addRaw("Caesar Salad",     "Food",            8.00, "Crispy romaine with parmesan");
        addRaw("Avocado Toast",    "Food",            7.00, "Smashed avo on sourdough");
        addRaw("Granola Bowl",     "Food",            6.50, "Greek yogurt, granola and berries");
    }

    /** Internal helper to add item without save (used during seeding). */
    private void addRaw(String name, String cat, double price, String desc) {
        menuItems.add(new MenuItem(idCounter++, name, cat, price, desc));
    }

    // ── Manageable Interface Implementation ───────────────────────────────────

    /**
     * Returns all menu items.
     *
     * <p><b>OOP Concept:</b> Implements {@link Manageable#getAll()}.</p>
     *
     * @return a copy of the full menu item list
     */
    @Override
    public List<MenuItem> getAll() { return new ArrayList<>(menuItems); }

    /**
     * Finds a menu item by its unique ID.
     *
     * <p><b>OOP Concept:</b> Implements {@link Manageable#findById(Object)}.</p>
     *
     * @param id the ID to search for
     * @return Optional containing the item if found
     */
    @Override
    public Optional<MenuItem> findById(Integer id) {
        return menuItems.stream().filter(i -> i.getId() == id).findFirst();
    }

    /**
     * Removes a menu item by ID with file persistence.
     *
     * <p><b>OOP Concept:</b> Implements {@link Manageable#remove(Object)}.</p>
     *
     * @param id the ID of the item to remove
     * @return true if removed, false if not found
     */
    @Override
    public boolean remove(Integer id) {
        boolean removed = menuItems.removeIf(i -> i.getId() == id);
        if (removed) save();
        return removed;
    }

    /**
     * Returns the count of menu items.
     *
     * @return total number of menu items
     */
    @Override
    public int getCount() { return menuItems.size(); }

    // ── CRUD Operations ───────────────────────────────────────────────────────

    /**
     * Adds a new menu item after validating inputs.
     *
     * <p><b>OOP Concept:</b> Throws {@link InvalidInputException} on bad input.</p>
     *
     * @param name     the item name (must not be blank)
     * @param category the category (must not be blank)
     * @param price    the price (must be positive)
     * @param desc     the description
     * @return the newly created MenuItem
     * @throws InvalidInputException if name, category, or price is invalid
     */
    public MenuItem addMenuItem(String name, String category, double price, String desc) {
        if (name == null || name.isBlank())
            throw new InvalidInputException("name", name, "Item name cannot be empty");
        if (category == null || category.isBlank())
            throw new InvalidInputException("category", category, "Category cannot be empty");
        if (price <= 0)
            throw new InvalidInputException("price", String.valueOf(price), "Price must be greater than zero");

        MenuItem item = new MenuItem(idCounter++, name.trim(), category.trim(), price, desc.trim());
        menuItems.add(item);
        save();
        return item;
    }

    /**
     * Finds a menu item by ID, throwing if not found.
     *
     * @param id the item ID
     * @return the found MenuItem
     * @throws ItemNotFoundException if no item has that ID
     */
    public MenuItem findByIdOrThrow(int id) throws ItemNotFoundException {
        return findById(id).orElseThrow(() -> new ItemNotFoundException("MenuItem", id));
    }

    /**
     * Searches menu items whose names contain the given keyword.
     *
     * @param keyword the search keyword (case-insensitive)
     * @return list of matching items
     */
    public List<MenuItem> searchByName(String keyword) {
        return menuItems.stream()
                .filter(i -> i.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all items belonging to a given category.
     *
     * @param category the category name to filter by
     * @return list of items in that category
     */
    public List<MenuItem> findByCategory(String category) {
        return menuItems.stream()
                .filter(i -> i.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Returns only items currently marked as available.
     *
     * @return list of available menu items
     */
    public List<MenuItem> getAvailableItems() {
        return menuItems.stream().filter(MenuItem::isAvailable).collect(Collectors.toList());
    }

    /**
     * Returns all items grouped by category in display order.
     *
     * @return LinkedHashMap preserving category display order
     */
    public Map<String, List<MenuItem>> getMenuByCategory() {
        Map<String, List<MenuItem>> grouped = menuItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));
        Map<String, List<MenuItem>> ordered = new LinkedHashMap<>();
        for (String cat : CATEGORY_ORDER) {
            if (grouped.containsKey(cat)) ordered.put(cat, grouped.get(cat));
        }
        grouped.forEach((k, v) -> { if (!ordered.containsKey(k)) ordered.put(k, v); });
        return ordered;
    }

    /**
     * Returns all category names in display order.
     *
     * @return ordered list of category names
     */
    public List<String> getCategories() {
        List<String> all = new ArrayList<>(CATEGORY_ORDER);
        menuItems.stream().map(MenuItem::getCategory)
                .filter(c -> !all.contains(c)).distinct().forEach(all::add);
        return all;
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    /**
     * Saves all menu items to the data file.
     *
     * <p><b>OOP Concept:</b> File I/O via {@link FileManager}.</p>
     */
    public void save() {
        FileManager.saveMenuItems(menuItems);
    }
}
