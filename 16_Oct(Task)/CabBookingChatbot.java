import java.util.Scanner;

public class CabBookingChatbot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the VEX Cab Booking Chatbot");

        while (true) {
            System.out.println("----- MAIN MENU -----");
            System.out.println("1. Book Ride");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            if (choice.equals("2")) {
                System.out.println("Thank you for using the chatbot. Goodbye!");
                break;
            } else if (choice.equals("1")) {

                // Choose cab type
                System.out.println("\nChoose cab type:");
                System.out.println("1. Auto");
                System.out.println("2. Sedan");
                System.out.println("3. SUV");
                System.out.print("Enter cab choice: ");
                String cab = sc.nextLine();

                String cabName = "";
                int rate = 0;
                if (cab.equals("1")) {
                    cabName = "Auto";
                    rate = 12;
                } else if (cab.equals("2")) {
                    cabName = "Sedan";
                    rate = 18;
                } else if (cab.equals("3")) {
                    cabName = "SUV";
                    rate = 25;
                } else {
                    System.out.println("Invalid cab choice!");
                    continue;
                }

                // Choose route
                System.out.println("\nChoose route:");
                System.out.println("1. Pune Station to Shivajinagar (5 km)");
                System.out.println("2. Swargate to Kothrud (8 km)");
                System.out.println("3. Hinjawadi to Wakad (10 km)");
                System.out.print("Enter route number: ");
                String route = sc.nextLine();

                String source = "";
                String destination = "";
                int distance = 0;
                if (route.equals("1")) {
                    source = "Pune Station";
                    destination = "Shivajinagar";
                    distance = 5;
                } else if (route.equals("2")) {
                    source = "Swargate";
                    destination = "Kothrud";
                    distance = 8;
                } else if (route.equals("3")) {
                    source = "Hinjawadi";
                    destination = "Wakad";
                    distance = 10;
                } else {
                    System.out.println("Invalid route choice!");
                    continue;
                }

                // Calculate fare
                int fare = rate * distance;
                System.out.println("\n----- RIDE DETAILS -----");
                System.out.println("Cab Type    : " + cabName);
                System.out.println("Source      : " + source);
                System.out.println("Destination : " + destination);
                System.out.println("Distance    : " + distance + " km");
                System.out.println("Rate        : Rs. " + rate + " per km");
                System.out.println("Total Fare  : Rs. " + fare);

                // Confirm or cancel (only before OTP)
                System.out.println("\n1. Confirm & Share OTP");
                System.out.println("2. Cancel Ride");
                System.out.print("Enter your choice: ");
                String confirm = sc.nextLine();

                if (confirm.equals("1")) {
                    int otp = 4821; // 4-digit OTP assigned to user
                    System.out.println("\nOTP shared with you: " + otp);
                    System.out.println("Rule: Ride cannot be cancelled after OTP is shared.");
                    System.out.println("Booking confirmed! Your " + cabName + " is on the way.");
                } else if (confirm.equals("2")) {
                    System.out.println("Ride cancelled. No OTP was shared.");
                } else {
                    System.out.println("Invalid choice! Booking not confirmed, no OTP shared.");
                }

            } else {
                System.out.println("Invalid choice! Please enter 1 or 2.");
            }
        }
        sc.close();
    }
}