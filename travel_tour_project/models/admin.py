# models/admin.py
 from models.tourist_spot import TouristSpot
 from models.package import Package
 from models.booking import Booking

 class Admin:
     def __init__(self):
         self.spot = TouristSpot()
         self.package = Package()
         self.booking = Booking()


