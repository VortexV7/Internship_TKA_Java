
public class ShoppingBill {

    static double electronics(double a, double b) {
        double total = a + b;
        double gst = total * 18 / 100;
        return total + gst;
    }

    static double fashion(double a, double b) {
        double total = a + b;
        double gst = total * 13 / 100;
        return total + gst;
    }

    static double food(double a) {
        double gst = a * 7 / 100;
        return a + gst;
    }

    public static void main(String[] args) {
        double e1 = 100;
        double e2 = 100;
        double f1 = 100;
        double f2 = 100;
        double fd = 100;

        double total = electronics(e1, e2)
                     + fashion(f1, f2)
                     + food(fd);

        double discount = total * 20 / 100;
        double finalAmount = total - discount;

        System.out.printf("Total after GST = %.2f%n", total);
        System.out.printf("Discount = %.2f%n", discount);
        System.out.printf("Final payable amount = %.2f%n", finalAmount);
    }
}
