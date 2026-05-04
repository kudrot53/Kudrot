# models/user.py
from models.database import Database
import hashlib

class User:
    def __init__(self):
        self.db = Database()
        self.db.execute_query("""
            CREATE TABLE IF NOT EXISTS user(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            )
        """)

    def hash_password(self, password):
        return hashlib.sha256(password.encode()).hexdigest()

    def register(self, name, username, email, password):
        hashed = self.hash_password(password)
        try:
            self.db.execute_query(
                "INSERT INTO user (name, username, email, password) VALUES (?, ?, ?, ?)",
                (name, username, email, hashed)
            )
            return True, "Registration Successful!"
        except:
            return False, "Username or Email already exists!"

    def login(self, username, password):
        hashed = self.hash_password(password)
        user = self.db.fetch_data(
            "SELECT * FROM user WHERE username=? AND password=?",
            (username, hashed)
        )
        if user:
            return True, user[0]
        else:
            return False, "Invalid Credentials"
