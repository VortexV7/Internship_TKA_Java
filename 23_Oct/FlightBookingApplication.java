
import java.util.Scanner;

abstract class Passenger {
    private String name;
    private int age;

    Passenger(String name, int age) {
        this.name = name;
        this.age = age;
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    abstract double getDiscount();

    abstract String getType();
}

class RegularPassenger extends Passenger {
    RegularPassenger(String name, int age) {
        super(name, age);
    }

    double getDiscount() {
        return 0;
    }

    String getType() {
        return "Regular";
    }
}

class StudentPassenger extends Passenger {
    StudentPassenger(String name, int age) {
        super(name, age);
    }

    double getDiscount() {
        return 10;
    }

    String getType() {
        return "Student";
    }
}

class Flight {
    private String code;
    private String airline;
    private String from;
    private String to;
    private int seats;
    private double fare;

    Flight(String code, String airline, String from, String to,
           int seats, double fare) {
        this.code = code;
        this.airline = airline;
        this.from = from;
        this.to = to;
        this.seats = seats;
        this.fare = fare;
    }

    String getCode() {
        return code;
    }

    String getAirline() {
        return airline;
    }

    String getFrom() {
        return from;
    }

    String getTo() {
        return to;
    }

    int getSeats() {
        return seats;
    }

    double getFare() {
        return fare;
    }

    void bookSeats(int count) {
        seats = seats - count;
    }

    void showFlight() {
        System.out.println("Flight Code: " + code);
        System.out.println("Airline: " + airline);
        System.out.println("Route: " + from + " to " + to);
        System.out.println("Available Seats: " + seats);
        System.out.println("Base Fare: Rs. " + fare);
        System.out.println();
    }
}

class Booking {
    private String id;
    private Flight flight;
    private Passenger passenger;
    private int tickets;

    Booking(String id, Flight flight, Passenger passenger, int tickets) {
        this.id = id;
        this.flight = flight;
        this.passenger = passenger;
        this.tickets = tickets;
    }

    void showBooking() {
        double subtotal = flight.getFare() * tickets;
        double discount = subtotal * passenger.getDiscount() / 100;
        double total = subtotal - discount;

        System.out.println("\nBooking confirmed!");
        System.out.println("Booking ID: " + id);
        System.out.println("Flight: " + flight.getCode());
        System.out.println("Airline: " + flight.getAirline());
        System.out.println("Route: " + flight.getFrom() + " to " + flight.getTo());
        System.out.println("Passenger: " + passenger.getName());
        System.out.println("Age: " + passenger.getAge());
        System.out.println("Passenger Type: " + passenger.getType());
        System.out.println("Tickets: " + tickets);
        System.out.println("Base Fare per Ticket: Rs. " + flight.getFare());
        System.out.println("Subtotal: Rs. " + subtotal);
        System.out.println("Discount: Rs. " + discount);
        System.out.println("Final Amount: Rs. " + total);
    }
}

public class FlightBookingApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Flight f1 = new Flight("PN101", "Air India", "Pune", "Delhi", 20, 4000);
        Flight f2 = new Flight("MB202", "IndiGo", "Mumbai", "Bengaluru", 25, 5000);
        Flight f3 = new Flight("PN303", "Akasa Air", "Pune", "Hyderabad", 15, 3500);

        Flight[] flights = {f1, f2, f3};

        Booking booking = null;
        int bookingNumber = 1001;

        while (true) {
            System.out.println("\nWelcome to Kiran Air Booking");
            System.out.println("1. View Flights");
            System.out.println("2. Book Flight");
            System.out.println("3. View Booking");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid choice. Enter a number from 1 to 4.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                for (Flight f : flights) {
                    f.showFlight();
                }
            }
            else if (choice == 2) {
                System.out.print("Enter flight code: ");
                String code = sc.nextLine().trim();

                Flight selected = null;

                for (Flight f : flights) {
                    if (f.getCode().equalsIgnoreCase(code)) {
                        selected = f;
                        break;
                    }
                }

                if (selected == null) {
                    System.out.println("Invalid flight code.");
                    continue;
                }

                System.out.print("Enter passenger name: ");
                String name = sc.nextLine().trim();

                System.out.print("Enter age: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid age.");
                    sc.nextLine();
                    continue;
                }

                int age = sc.nextInt();
                sc.nextLine();

                System.out.print("Passenger type (Regular/Student): ");
                String type = sc.nextLine().trim();

                System.out.print("Enter number of tickets: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid ticket count.");
                    sc.nextLine();
                    continue;
                }

                int tickets = sc.nextInt();
                sc.nextLine();

                if (name.isEmpty() || age <= 0 || tickets <= 0) {
                    System.out.println("Invalid details. Check name, age and ticket count.");
                    continue;
                }

                Passenger passenger;

                if (type.equalsIgnoreCase("Regular")) {
                    passenger = new RegularPassenger(name, age);
                }
                else if (type.equalsIgnoreCase("Student")) {
                    passenger = new StudentPassenger(name, age);
                }
                else {
                    System.out.println("Invalid passenger type.");
                    continue;
                }

                if (tickets > selected.getSeats()) {
                    System.out.println("Booking failed: Not enough seats available.");
                    continue;
                }

                booking = new Booking("BK" + bookingNumber, selected, passenger, tickets);
                bookingNumber++;

                selected.bookSeats(tickets);
                booking.showBooking();
            }
            else if (choice == 3) {
                if (booking == null) {
                    System.out.println("No booking found.");
                } else {
                    booking.showBooking();
                }
            }
            else if (choice == 4) {
                System.out.println("Thank you for using Kiran Air Booking!");
                break;
            }
            else {
                System.out.println("Invalid choice. Please select 1 to 4.");
            }
        }

        sc.close();
    }
}
