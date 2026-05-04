# models/database.py
import sqlite3

class Database:
    def __init__(self, db_path="travel_tour.db"):
        self.conn = sqlite3.connect(db_path, check_same_thread=False)
        self.cur = self.conn.cursor()
        self.cur.execute("PRAGMA foreign_keys = ON")  # enable foreign keys
        self.conn.commit()

    def execute_query(self, query, params=()):
        self.cur.execute(query, params)
        self.conn.commit()
        return self.cur

    def fetch_data(self, query, params=()):
        self.cur.execute(query, params)
        return self.cur.fetchall()
