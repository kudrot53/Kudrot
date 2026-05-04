# models/package.py
from models.database import Database

class Package:
    def __init__(self):
        self.db = Database()
        self.db.execute_query("""
            CREATE TABLE IF NOT EXISTS package(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                spot_ids TEXT NOT NULL
            )
        """)

    def add_package(self, name, price, spot_ids):
        spot_ids_str = ",".join(map(str, spot_ids))
        self.db.execute_query(
            "INSERT INTO package (name, price, spot_ids) VALUES (?, ?, ?)",
            (name, price, spot_ids_str)
        )

    def update_package(self, package_id, name, price, spot_ids):
        spot_ids_str = ",".join(map(str, spot_ids))
        self.db.execute_query(
            "UPDATE package SET name=?, price=?, spot_ids=? WHERE id=?",
            (name, price, spot_ids_str, package_id)
        )

    def delete_package(self, package_id):
        self.db.execute_query("DELETE FROM package WHERE id=?", (package_id,))

    def get_all_packages(self):
        return self.db.fetch_data("SELECT * FROM package")
