package cafe.service;

import cafe.exception.InvalidInputException;
import cafe.exception.ItemNotFoundException;
import cafe.model.Manager;
import cafe.model.Staff;
import cafe.util.FileManager;
import cafe.util.Manageable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class managing all staff-related operations.
 *
 * <p>Implements {@link Manageable} and persists data via {@link FileManager}.
 * Demonstrates runtime polymorphism — the staff list holds both
 * {@link Staff} and {@link Manager} objects via parent reference.</p>
 *
 * <p><b>OOP Concepts:</b> Interface, polymorphism (Staff/Manager in same list),
 * exception handling, file I/O.</p>
 *
 * @author Brew and Bliss Cafe System
 * @version 1.0
 */
public class StaffService implements Manageable<Staff, Integer> {

    /**
     * In-memory list of all staff members.
     * <p><b>OOP Concept:</b> Runtime Polymorphism — holds both Staff and Manager objects.</p>
     */
    private List<Staff> staffList;

    /**
     * Constructs StaffService, loading saved data or seeding sample staff.
     */
    public StaffService() {
        List<Staff> saved = FileManager.loadStaff();
        if (!saved.isEmpty()) {
            staffList = saved;
        } else {
            staffList = new ArrayList<>();
            seedDefaultStaff();
            save();
        }
    }

    /**
     * Seeds sample staff on first run, including a Manager instance.
     * <p><b>OOP Concept:</b> Polymorphism — Manager stored as Staff reference.</p>
     */
    private void seedDefaultStaff() {
        // Manager instance stored in Staff list — runtime polymorphism
        Manager mgr = new Manager("Maria Santos", "Morning", 22.00,
                "admin", Manager.hashPassword("cafe123"));
        mgr.setOnDuty(true);

        Staff s2 = new Staff("James Lee",    Staff.Role.BARISTA, "Morning", 16.50);
        Staff s3 = new Staff("Priya Sharma", Staff.Role.BARISTA, "Evening", 16.50);
        Staff s4 = new Staff("Tom Williams", Staff.Role.CASHIER, "Morning", 15.00);
        Staff s5 = new Staff("Emma Garcia",  Staff.Role.WAITER,  "Evening", 14.50);
        s2.setOnDuty(true);
        s4.setOnDuty(true);
        staffList.addAll(List.of(mgr, s2, s3, s4, s5));
    }

    // ── Manageable Implementation ─────────────────────────────────────────────

    /** @return all staff members */
    @Override
    public List<Staff> getAll() { return new ArrayList<>(staffList); }

    /**
     * Finds a staff member by their unique ID.
     *
     * @param id the staff ID
     * @return Optional containing the staff member if found
     */
    @Override
    public Optional<Staff> findById(Integer id) {
        return staffList.stream().filter(s -> s.getStaffId() == id).findFirst();
    }

    /**
     * Removes a staff member by ID.
     *
     * @param id the staff ID to remove
     * @return true if removed
     */
    @Override
    public boolean remove(Integer id) {
        boolean removed = staffList.removeIf(s -> s.getStaffId() == id);
        if (removed) save();
        return removed;
    }

    /** @return total staff count */
    @Override
    public int getCount() { return staffList.size(); }

    // ── Business Operations ───────────────────────────────────────────────────

    /**
     * Adds a new staff member after validating inputs.
     *
     * @param name       the staff name (must not be blank)
     * @param role       the staff role
     * @param shift      the shift assignment
     * @param hourlyRate the hourly pay rate (must be positive)
     * @return the newly created Staff member
     * @throws InvalidInputException if name or rate is invalid
     */
    public Staff addStaff(String name, Staff.Role role, String shift, double hourlyRate) {
        if (name == null || name.isBlank())
            throw new InvalidInputException("name", name, "Staff name cannot be empty");
        if (hourlyRate <= 0)
            throw new InvalidInputException("hourlyRate", String.valueOf(hourlyRate), "Rate must be positive");
        Staff s = new Staff(name.trim(), role, shift, hourlyRate);
        staffList.add(s);
        save();
        return s;
    }

    /**
     * Finds a staff member by ID, throwing if not found.
     *
     * @param id the staff ID
     * @return the found Staff
     * @throws ItemNotFoundException if no staff has that ID
     */
    public Staff findByIdOrThrow(int id) throws ItemNotFoundException {
        return findById(id).orElseThrow(() -> new ItemNotFoundException("Staff", id));
    }

    /**
     * Toggles a staff member's on-duty status.
     *
     * @param id the staff member's ID
     * @return true if the toggle was applied
     */
    public boolean toggleDuty(int id) {
        Optional<Staff> opt = findById(id);
        if (opt.isPresent()) {
            opt.get().setOnDuty(!opt.get().isOnDuty());
            save();
            return true;
        }
        return false;
    }

    /**
     * Returns only staff currently on duty.
     *
     * @return list of on-duty staff
     */
    public List<Staff> getOnDutyStaff() {
        return staffList.stream().filter(Staff::isOnDuty).collect(Collectors.toList());
    }

    /**
     * Returns staff filtered by role.
     *
     * @param role the role to filter by
     * @return list of staff with that role
     */
    public List<Staff> getStaffByRole(Staff.Role role) {
        return staffList.stream().filter(s -> s.getRole() == role).collect(Collectors.toList());
    }

    /** @return total staff count */
    public int getTotalStaff()  { return staffList.size(); }

    /** @return number of staff currently on duty */
    public int getOnDutyCount() {
        return (int) staffList.stream().filter(Staff::isOnDuty).count();
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    /**
     * Persists all staff data to disk.
     */
    public void save() {
        FileManager.saveStaff(staffList);
    }
}
