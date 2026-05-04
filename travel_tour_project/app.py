import streamlit as st
import sqlite3
import os
from PIL import Image
import time

# ----------------- CUSTOM CSS FOR POINTER CURSOR -----------------
st.markdown("""
    <style>
    /* All buttons */
    button, div.stButton > button {
        cursor: pointer !important;
    }

    /* Sidebar menu (selectbox options) */
    div[role="listbox"] > div {
        cursor: pointer !important;
    }

    /* Streamlit selectbox */
    div[role="combobox"] {
        cursor: pointer !important;
    }
    </style>
""", unsafe_allow_html=True)

# ----------------- DATABASE SETUP -----------------
DB_FILE = "travel_tour.db"
IMAGE_DIR = "images"
os.makedirs(IMAGE_DIR, exist_ok=True)

conn = sqlite3.connect(DB_FILE, check_same_thread=False)
cur = conn.cursor()

# ----------------- TABLES -----------------
cur.execute("""
            CREATE TABLE IF NOT EXISTS users
            (
                id
                INTEGER
                PRIMARY
                KEY
                AUTOINCREMENT,
                name
                TEXT,
                email
                TEXT
                UNIQUE,
                username
                TEXT
                UNIQUE,
                password
                TEXT
            )
            """)

cur.execute("""
            CREATE TABLE IF NOT EXISTS admin
            (
                id
                INTEGER
                PRIMARY
                KEY
                AUTOINCREMENT,
                username
                TEXT
                UNIQUE,
                password
                TEXT
            )
            """)

cur.execute("""
            CREATE TABLE IF NOT EXISTS tourist_spots
            (
                id
                INTEGER
                PRIMARY
                KEY
                AUTOINCREMENT,
                name
                TEXT,
                location
                TEXT,
                description
                TEXT,
                image
                TEXT
            )
            """)

cur.execute("""
            CREATE TABLE IF NOT EXISTS packages
            (
                id
                INTEGER
                PRIMARY
                KEY
                AUTOINCREMENT,
                name
                TEXT,
                spot_id
                INTEGER,
                price
                REAL,
                FOREIGN
                KEY
            (
                spot_id
            ) REFERENCES tourist_spots
            (
                id
            )
                )
            """)

cur.execute("""
            CREATE TABLE IF NOT EXISTS bookings
            (
                id
                INTEGER
                PRIMARY
                KEY
                AUTOINCREMENT,
                user_id
                INTEGER,
                package_id
                INTEGER,
                FOREIGN
                KEY
            (
                user_id
            ) REFERENCES users
            (
                id
            ),
                FOREIGN KEY
            (
                package_id
            ) REFERENCES packages
            (
                id
            )
                )
            """)

conn.commit()

# ----------------- DEFAULT ADMIN -----------------
cur.execute("SELECT * FROM admin WHERE username='admin'")
if not cur.fetchone():
    cur.execute("INSERT INTO admin(username,password) VALUES(?,?)", ("admin", "admin123"))
    conn.commit()

# ----------------- SESSION -----------------
if "user_logged_in" not in st.session_state:
    st.session_state.user_logged_in = False
    st.session_state.user_id = None
    st.session_state.username = None

if "admin_logged_in" not in st.session_state:
    st.session_state.admin_logged_in = False
    st.session_state.admin_username = None

# Track payment process
if "paying_package" not in st.session_state:
    st.session_state.paying_package = None

# Dummy refresh key for rerun after cancel booking or login/logout
if "refresh" not in st.session_state:
    st.session_state.refresh = False


# ----------------- FUNCTIONS -----------------
def register_user(name, email, username, password):
    try:
        cur.execute(
            "INSERT INTO users(name,email,username,password) VALUES(?,?,?,?)",
            (name, email, username, password)
        )
        conn.commit()
        st.success("Registration successful! You can login now.")
    except sqlite3.IntegrityError:
        st.error("Username or email already exists.")


def login_user(username, password):
    cur.execute("SELECT id, username FROM users WHERE username=? AND password=?", (username, password))
    user = cur.fetchone()
    if user:
        st.session_state.user_logged_in = True
        st.session_state.user_id = user[0]
        st.session_state.username = user[1]
        st.success("Login successful!")
        st.session_state["refresh"] = not st.session_state.get("refresh", False)
    else:
        st.error("Invalid username or password")


def login_admin(username, password):
    cur.execute("SELECT username FROM admin WHERE username=? AND password=?", (username, password))
    admin = cur.fetchone()
    if admin:
        st.session_state.admin_logged_in = True
        st.session_state.admin_username = admin[0]
        st.success("Logged in Successfully ✅")
        time.sleep(1)
        st.session_state["refresh"] = not st.session_state.get("refresh", False)
    else:
        st.error("Invalid admin credentials")


def logout_user():
    st.session_state.user_logged_in = False
    st.session_state.user_id = None
    st.session_state.username = None
    st.session_state["refresh"] = not st.session_state.get("refresh", False)


def logout_admin():
    st.session_state.admin_logged_in = False
    st.session_state.admin_username = None
    st.session_state["refresh"] = not st.session_state.get("refresh", False)


def add_tourist_spot(name, location, description, image):
    if image:
        path = os.path.join(IMAGE_DIR, image.name)
        with open(path, "wb") as f:
            f.write(image.getbuffer())
        cur.execute(
            "INSERT INTO tourist_spots(name,location,description,image) VALUES(?,?,?,?)",
            (name, location, description, path)
        )
        conn.commit()
        st.success("Tourist spot added")


def add_package(name, spot_id, price):
    cur.execute("INSERT INTO packages(name,spot_id,price) VALUES(?,?,?)", (name, spot_id, price))
    conn.commit()
    st.success("Package added")


def book_package(user_id, package_id):
    cur.execute("INSERT INTO bookings(user_id,package_id) VALUES(?,?)", (user_id, package_id))
    conn.commit()
    st.success("Booking successful!")


# ----------------- UI -----------------
st.title("Travel & Tour Management System")

menu = ["Home", "Login", "Register", "Admin Login"]

if st.session_state.user_logged_in:
    menu = ["Home", "My Bookings", "Logout"]

if st.session_state.admin_logged_in:
    menu = ["Admin Dashboard", "Logout"]

choice = st.sidebar.selectbox("Menu", menu)

# ----------------- HOME -----------------
if choice == "Home":
    st.subheader("Welcome to Travel & Tour Management System")
    cur.execute("SELECT name,location,description,image FROM tourist_spots")
    for s in cur.fetchall():
        st.write(f"**{s[0]}** - {s[1]}")
        st.write(s[2])
        if s[3] and os.path.exists(s[3]):
            st.image(Image.open(s[3]), width=300)

    # ---------- Package Selection for Logged-in Users ----------
    if st.session_state.user_logged_in:
        cur.execute("""
                    SELECT p.id, p.name, p.price, s.name, s.location, s.description
                    FROM packages p
                             JOIN tourist_spots s ON p.spot_id = s.id
                    """)
        packages = cur.fetchall()
        pkg_dict = {f"{p[1]} (${p[2]})": p for p in packages}

        if pkg_dict:
            selected_name = st.selectbox("Select Package", list(pkg_dict.keys()))
            selected_pkg = pkg_dict[selected_name]

            # Show package details
            st.subheader(f"Package: {selected_pkg[1]}")
            st.write(f"**Price:** ${selected_pkg[2]}")
            st.write(f"**Tourist Spot:** {selected_pkg[3]} ({selected_pkg[4]})")
            st.write(f"**Spot Description:** {selected_pkg[5]}")

            # ---------- Payment Interface ----------
            if st.button("Book Now") or st.session_state.paying_package == selected_pkg[0]:
                st.session_state.paying_package = selected_pkg[0]

                st.info("Payment Interface")

                # Show package image
                cur.execute("SELECT image FROM tourist_spots WHERE name=?", (selected_pkg[3],))
                img_path = cur.fetchone()
                if img_path and img_path[0] and os.path.exists(img_path[0]):
                    st.image(Image.open(img_path[0]), width=300)

                st.subheader(f"Package: {selected_pkg[1]}")
                st.write(f"**Price:** ${selected_pkg[2]}")
                st.write(f"**Tourist Spot:** {selected_pkg[3]} ({selected_pkg[4]})")

                # Payment input fields
                card_number = st.text_input("Card Number", key="card_number")
                cvv = st.text_input("CVV", key="cvv")
                exp = st.text_input("Expiry (MM/YY)", key="exp")

                if st.button("Pay"):
                    if card_number and cvv and exp:
                        with st.spinner("Processing Payment..."):
                            time.sleep(2)
                        st.success("Payment Successful! Booking Confirmed ✅")
                        book_package(st.session_state.user_id, selected_pkg[0])

                        # Clear payment fields
                        st.session_state.paying_package = None
                        for key in ["card_number", "cvv", "exp"]:
                            if key in st.session_state:
                                del st.session_state[key]

# ----------------- MY BOOKINGS -----------------
elif choice == "My Bookings":
    st.subheader(f"{st.session_state.username}'s Bookings")

    cur.execute("""
                SELECT b.id, p.name, p.price, s.name, s.location, s.description
                FROM bookings b
                         JOIN packages p ON b.package_id = p.id
                         JOIN tourist_spots s ON p.spot_id = s.id
                WHERE b.user_id = ?
                """, (st.session_state.user_id,))

    bookings = cur.fetchall()

    if bookings:
        for bk in bookings:
            booking_id = bk[0]
            st.write(f"**Package:** {bk[1]} (${bk[2]})")
            st.write(f"**Tourist Spot:** {bk[3]} ({bk[4]})")
            st.write(f"**Spot Description:** {bk[5]}")

            # Cancel Booking button
            if st.button(f"Cancel Booking: {bk[1]}", key=f"cancel_{booking_id}"):
                cur.execute("DELETE FROM bookings WHERE id=?", (booking_id,))
                conn.commit()
                st.success(f"Booking for {bk[1]} cancelled.")
                st.session_state["refresh"] = not st.session_state.get("refresh", False)
            st.markdown("---")
    else:
        st.info("You have not booked any packages yet.")

# ----------------- REGISTER -----------------
elif choice == "Register":
    st.subheader("Register")
    name = st.text_input("Name")
    email = st.text_input("Email")
    username = st.text_input("Username")
    password = st.text_input("Password", type="password")
    if st.button("Register"):
        register_user(name, email, username, password)

# ----------------- LOGIN -----------------
elif choice == "Login":
    st.subheader("User Login")
    username = st.text_input("Username")
    password = st.text_input("Password", type="password")
    if st.button("Login"):
        login_user(username, password)

# ----------------- ADMIN LOGIN -----------------
elif choice == "Admin Login":
    st.subheader("Admin Login")
    username = st.text_input("Admin Username")
    password = st.text_input("Admin Password", type="password")
    if st.button("Login as Admin"):
        login_admin(username, password)

# ----------------- ADMIN DASHBOARD -----------------
elif choice == "Admin Dashboard":
    st.subheader(f"Welcome Admin: {st.session_state.admin_username}")

    admin_menu = st.selectbox(
        "Admin Options",
        [
            "Add Tourist Spot",
            "View Tourist Spots",
            "Delete Tourist Spot",
            "Add Package",
            "View Packages",
            "Delete Package",
            "View Bookings",
            "Delete Booking"
        ]
    )

    if admin_menu == "Add Tourist Spot":
        name = st.text_input("Name")
        loc = st.text_input("Location")
        desc = st.text_area("Description")
        img = st.file_uploader("Image", type=["jpg", "png"])
        if st.button("Add"):
            add_tourist_spot(name, loc, desc, img)

    elif admin_menu == "View Tourist Spots":
        cur.execute("SELECT id,name,location FROM tourist_spots")
        for s in cur.fetchall():
            st.write(s)

    elif admin_menu == "Delete Tourist Spot":
        cur.execute("SELECT id,name FROM tourist_spots")
        spots = {f"{s[0]} - {s[1]}": s[0] for s in cur.fetchall()}
        if spots:
            sel = st.selectbox("Select", list(spots.keys()))
            if st.button("Delete"):
                cur.execute("DELETE FROM tourist_spots WHERE id=?", (spots[sel],))
                conn.commit()
                st.success("Deleted")

    elif admin_menu == "Add Package":
        cur.execute("SELECT id,name FROM tourist_spots")
        spots = {s[1]: s[0] for s in cur.fetchall()}
        if spots:
            spot = st.selectbox("Spot", list(spots.keys()))
            pname = st.text_input("Package Name")
            price = st.number_input("Price", min_value=0.0)
            if st.button("Add Package"):
                add_package(pname, spots[spot], price)

    elif admin_menu == "View Packages":
        cur.execute("""
                    SELECT p.id, p.name, p.price, s.name
                    FROM packages p
                             JOIN tourist_spots s ON p.spot_id = s.id
                    """)
        for p in cur.fetchall():
            st.write(p)

    elif admin_menu == "Delete Package":
        cur.execute("SELECT id,name FROM packages")
        pkgs = {f"{p[0]} - {p[1]}": p[0] for p in cur.fetchall()}
        if pkgs:
            sel = st.selectbox("Select", list(pkgs.keys()))
            if st.button("Delete"):
                cur.execute("DELETE FROM packages WHERE id=?", (pkgs[sel],))
                conn.commit()
                st.success("Deleted")

    elif admin_menu == "View Bookings":
        cur.execute("""
                    SELECT b.id, u.username, p.name
                    FROM bookings b
                             JOIN users u ON b.user_id = u.id
                             JOIN packages p ON b.package_id = p.id
                    """)
        for b in cur.fetchall():
            st.write(b)

    elif admin_menu == "Delete Booking":
        cur.execute("SELECT id FROM bookings")
        ids = [str(i[0]) for i in cur.fetchall()]
        if ids:
            sel = st.selectbox("Booking ID", ids)
            if st.button("Delete"):
                cur.execute("DELETE FROM bookings WHERE id=?", (sel,))
                conn.commit()
                st.success("Deleted")

# ----------------- LOGOUT -----------------
elif choice == "Logout":
    if st.session_state.user_logged_in:
        logout_user()
    else:
        logout_admin()
