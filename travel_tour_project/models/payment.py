# models/payment.py
from models.database import Database
from datetime import datetime

class Payment:
    def __init__(self):
        self.db = Database()
        self.db.execute_query("""
            CREATE TABLE IF NOT EXISTS payment(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                booking_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                payment_method TEXT NOT NULL,
                status TEXT NOT NULL,
                payment_date TEXT NOT NULL,
                FOREIGN KEY(booking_id) REFERENCES booking(id),
                FOREIGN KEY(user_id) REFERENCES user(id)
            )
        """)

    def make_payment(self, booking_id, user_id, amount, method):
        date = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        status = "Paid"
        self.db.execute_query("""
            INSERT INTO payment (booking_id, user_id, amount, payment_method, status, payment_date)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (booking_id, user_id, amount, method, status, date))

    def get_user_payments(self, user_id):
        return self.db.fetch_data("SELECT * FROM payment WHERE user_id=?", (user_id,))

    def get_all_payments(self):
        return self.db.fetch_data("""
            SELECT p.id, u.name, b.id, p.amount, p.payment_method, p.status, p.payment_date
            FROM payment p
            JOIN user u ON p.user_id = u.id
            JOIN booking b ON p.booking_id = b.id
        """)
