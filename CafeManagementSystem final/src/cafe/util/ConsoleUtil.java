package cafe.util;

import java.util.Scanner;

/**
 * Shared console utilities for input, display, and formatting.
 */
public class ConsoleUtil {

    private static final Scanner scanner = new Scanner(System.in);

    // --- Colors (ANSI) -------------------------------------------------------
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    public static void printHeader(String title) {
        String border = "=".repeat(42);
        System.out.println("\n" + CYAN + BOLD + "+" + border + "+");
        System.out.printf("|  %-40s|%n", "  " + title);
        System.out.println("+" + border + "+" + RESET);
    }

    public static void printSubHeader(String title) {
        System.out.println("\n" + YELLOW + BOLD + "  -- " + title + " --" + RESET);
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "  [OK] " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "  [X] " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "  [i]  " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "  [!]  " + msg + RESET);
    }

    public static void printDivider() {
        System.out.println(CYAN + "  " + "-".repeat(50) + RESET);
    }

    public static void printMenuOption(int num, String label) {
        System.out.printf("  " + BOLD + "[%d]" + RESET + " %s%n", num, label);
    }

    public static String prompt(String label) {
        System.out.print(YELLOW + "  > " + label + ": " + RESET);
        return scanner.nextLine().trim();
    }

    public static int promptInt(String label) {
        while (true) {
            try {
                String input = prompt(label);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    public static double promptDouble(String label) {
        while (true) {
            try {
                String input = prompt(label);
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    public static void pressEnter() {
        System.out.print(CYAN + "\n  Press ENTER to continue..." + RESET);
        scanner.nextLine();
    }

    public static void clearScreen() {
        System.out.println("\n".repeat(3));
    }
}
