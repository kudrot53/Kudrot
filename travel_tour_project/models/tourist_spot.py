# models/tourist_spot.py
from models.database import Database

class TouristSpot:
    def __init__(self):
        self.db = Database()
        self.db.execute_query("""
            CREATE TABLE IF NOT EXISTS tourist_spot(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                location TEXT NOT NULL,
                description TEXT,
                image_path TEXT
            )
        """)

    def add_spot(self, name, location, description, image_path):
        self.db.execute_query(
            "INSERT INTO tourist_spot (name, location, description, image_path) VALUES (?, ?, ?, ?)",
            (name, location, description, image_path)
        )

    def update_spot(self, spot_id, name, location, description, image_path):
        self.db.execute_query(
            "UPDATE tourist_spot SET name=?, location=?, description=?, image_path=? WHERE id=?",
            (name, location, description, image_path, spot_id)
        )

    def delete_spot(self, spot_id):
        self.db.execute_query("DELETE FROM tourist_spot WHERE id=?", (spot_id,))

    def get_all_spots(self):
        return self.db.fetch_data("SELECT * FROM tourist_spot")
