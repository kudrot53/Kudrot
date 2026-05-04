
# ☕ Brew & Bliss — Cafe Management System
### *"Where Every Sip Tells a Story"*

A complete, feature-rich Java console application for managing a cafe.
Built with clean OOP design — perfect for coursework! 🎓

---

## 🚀 HOW TO RUN IN VS CODE (Step-by-Step)

### Step 1 — Install Java JDK
1. Go to: https://www.oracle.com/java/technologies/downloads/
2. Download **JDK 17 or higher** for your OS (Windows/Mac/Linux)
3. Run the installer and follow the steps
4. To verify, open a terminal and type:
   ```
   java -version
   ```
   You should see something like: `java version "21.0.x"`

---

### Step 2 — Install VS Code
1. Download from: https://code.visualstudio.com/
2. Install and open it

---

### Step 3 — Install the Java Extension Pack in VS Code
1. Open VS Code
2. Click the **Extensions** icon on the left sidebar (or press `Ctrl+Shift+X`)
3. Search for: **"Extension Pack for Java"**
4. Click **Install** (by Microsoft) — this installs everything you need

---

### Step 4 — Open the Project
1. In VS Code, click **File → Open Folder**
2. Select the `CafeManagementSystem` folder
3. Wait a few seconds for VS Code to detect the Java project

---

### Step 5 — Run the Program

**Option A — Easiest way:**
1. Open `src/cafe/Main.java`
2. Click the **▶ Run** button that appears above the `main` method
3. The app launches in the integrated terminal!

**Option B — Terminal:**
1. Open the terminal in VS Code: `Ctrl + `` ` (backtick)
2. Run these commands:
   ```bash
   # Windows:
   javac -d bin src/cafe/*.java src/cafe/model/*.java src/cafe/service/*.java src/cafe/ui/*.java src/cafe/util/*.java
   java -cp bin cafe.Main

   # Mac / Linux:
   find src -name "*.java" > sources.txt
   javac -d bin @sources.txt
   java -cp bin cafe.Main
   ```

**Option C — F5 Launch Config (already set up!):**
1. Press **F5** in VS Code
2. It will use `.vscode/launch.json` and start automatically

---

## 📁 Project Structure

```
CafeManagementSystem/
├── src/
│   └── cafe/
│       ├── Main.java               ← Entry point
│       ├── model/
│       │   ├── MenuItem.java       ← Menu item data
│       │   ├── Order.java          ← Order with receipt
│       │   ├── OrderItem.java      ← Individual order line
│       │   ├── Customer.java       ← Customer + loyalty points
│       │   └── Staff.java          ← Staff member
│       ├── service/
│       │   ├── MenuService.java    ← Menu CRUD operations
│       │   ├── OrderService.java   ← Order processing + reports
│       │   ├── CustomerService.java← Customer management
│       │   └── StaffService.java   ← Staff management
│       ├── ui/
│       │   └── CafeApp.java        ← All user interaction
│       └── util/
│           └── ConsoleUtil.java    ← Colors, prompts, formatting
└── .vscode/
    ├── launch.json                 ← Run config (F5)
    └── settings.json               ← Java source paths
```

---

## ✨ Features

| Module | What it does |
|---|---|
| 🍽️ Menu Management | Add/remove/search items, toggle availability, browse by category |
| 📝 Order Management | Take orders, add items with notes, update status, print receipts |
| 👥 Customer Management | Register customers, search, view history, loyalty points |
| 🧑‍💼 Staff Management | Add/remove staff, assign roles & shifts, toggle on/off duty |
| 📊 Sales Reports | Revenue by category, best sellers, top loyalty customers |
| 🧾 Receipts | Beautifully formatted receipt printed on order completion |
| ⭐ Loyalty Points | Customers earn 1 point per $1 spent automatically |

---

## 🎓 OOP Concepts Demonstrated

- **Encapsulation** — All models use private fields with getters/setters
- **Abstraction** — Service layer hides implementation from UI
- **Inheritance** — Enums used for type safety (Role, Status)
- **Polymorphism** — `toString()` overrides on all models
- **Collections** — `List`, `Map`, `Optional`, streams
- **Lambda & Streams** — Used throughout service layer
- **Layered Architecture** — Model → Service → UI separation

---

## 📞 Sample Walkthrough

1. Start the app → Dashboard shows live stats
2. Go to **Order Management → Take New Order**
3. Enter customer name (or press Enter for walk-in)
4. Enter table number (e.g., `5`)
5. Type `menu` to see all items
6. Enter item IDs and quantities
7. Type `done` when finished
8. Go to **Complete Order** → enter the order ID
9. A formatted receipt prints automatically! 🧾

---

*Built with ❤️ using Java 17+ | Clean OOP Design | Console UI*
