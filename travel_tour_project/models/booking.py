# models/booking.py
from models.database import Database

class Booking:
    def __init__(self):
        self.db = Database()
        self.db.execute_query("""
            CREATE TABLE IF NOT EXISTS booking(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                package_id INTEGER NOT NULL,
                booking_date TEXT NOT NULL,
                FOREIGN KEY(user_id) REFERENCES user(id),
                FOREIGN KEY(package_id) REFERENCES package(id)
            )
        """)

    def add_booking(self, user_id, package_id, booking_date):
        self.db.execute_query(
            "INSERT INTO booking (user_id, package_id, booking_date) VALUES (?, ?, ?)",
            (user_id, package_id, booking_date)
        )

    def delete_booking(self, booking_id):
        self.db.execute_query("DELETE FROM booking WHERE id=?", (booking_id,))

    def get_all_bookings(self):
        return self.db.fetch_data("""
            SELECT b.id, u.name, p.name, b.booking_date
            FROM booking b
            JOIN user u ON b.user_id = u.id
            JOIN package p ON b.package_id = p.id
        """)
