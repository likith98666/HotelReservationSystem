import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isAvailable;

    Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }
}

class Booking {
    String customerName;
    int roomNumber;
    String category;
    double amount;

    Booking(String customerName, int roomNumber, String category, double amount) {
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Name: " + customerName + ", Room No: " + roomNumber + ", Category: " + category + ", Paid: ₹" + amount;
    }
}

public class HotelReservationSystem {
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static final String fileName = "bookings.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadRooms();
        loadBookings();

        while (true) {
            System.out.println("\n--- Hotel Reservation System ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // flush

            switch (choice) {
                case 1: showAvailableRooms(); break;
                case 2: bookRoom(); break;
                case 3: cancelBooking(); break;
                case 4: viewBookings(); break;
                case 5: saveBookings(); System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void loadRooms() {
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Standard"));
        rooms.add(new Room(201, "Deluxe"));
        rooms.add(new Room(202, "Deluxe"));
        rooms.add(new Room(301, "Suite"));
    }

    static void loadBookings() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                bookings.add(new Booking(data[0], Integer.parseInt(data[1]), data[2], Double.parseDouble(data[3])));
                for (Room room : rooms) {
                    if (room.roomNumber == Integer.parseInt(data[1])) {
                        room.isAvailable = false;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // First run, file may not exist
        }
    }

    static void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Booking b : bookings) {
                pw.println(b.customerName + "," + b.roomNumber + "," + b.category + "," + b.amount);
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    static void showAvailableRooms() {
        System.out.println("--- Available Rooms ---");
        for (Room room : rooms) {
            if (room.isAvailable) {
                System.out.println("Room No: " + room.roomNumber + " | Category: " + room.category);
            }
        }
    }

    static void bookRoom() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine();

        for (Room room : rooms) {
            if (room.isAvailable && room.category.equalsIgnoreCase(category)) {
                double price = getPrice(category);
                System.out.println("Room " + room.roomNumber + " is available. Price: ₹" + price);
                System.out.print("Confirm booking? (yes/no): ");
                if (sc.nextLine().equalsIgnoreCase("yes")) {
                    room.isAvailable = false;
                    Booking booking = new Booking(name, room.roomNumber, category, price);
                    bookings.add(booking);
                    System.out.println("Booking successful!");
                } else {
                    System.out.println("Booking cancelled.");
                }
                return;
            }
        }
        System.out.println("No rooms available in selected category.");
    }

    static void cancelBooking() {
        System.out.print("Enter your name to cancel booking: ");
        String name = sc.nextLine();
        Iterator<Booking> iterator = bookings.iterator();
        boolean found = false;

        while (iterator.hasNext()) {
            Booking b = iterator.next();
            if (b.customerName.equalsIgnoreCase(name)) {
                iterator.remove();
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isAvailable = true;
                        break;
                    }
                }
                found = true;
                System.out.println("Booking cancelled.");
                break;
            }
        }
        if (!found) {
            System.out.println("Booking not found.");
        }
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("--- Booking Details ---");
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    static double getPrice(String category) {
        switch (category.toLowerCase()) {
            case "standard": return 1500;
            case "deluxe": return 2500;
            case "suite": return 4000;
            default: return 0;
        }
    }
}