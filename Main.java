import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "401172";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            UserService userService = new UserService();
            PaymentService paymentService = new PaymentService();
            PropertyService propertyService = new PropertyService();
            BookingService bookingService = new BookingService();

            while (true) {
                System.out.println("\n=== Real Estate CLI ===");
                System.out.println("1. Register user");
                System.out.println("2. Add credit card (renters only)");
                System.out.println("3. Delete credit card (renters only)");
                System.out.println("4. Add address (renters only)");
                System.out.println("5. Delete address (renters only)");
                System.out.println("6. Add property (agents only)");
                System.out.println("7. Delete property (agents only)");
                System.out.println("8. Modify property (agents only)");
                System.out.println("9. Update address (renters only)");
                System.out.println("10. Update credit card (renters only)");
                System.out.println("11. Book a property (renters only)");
                System.out.println("12. View my bookings (renters only)");
                System.out.println("13. Cancel a booking (renters only)");
                System.out.println("14. View bookings for my properties (agents only)");
                System.out.println("15. Cancel a booking for my property (agents only)");
                System.out.println("16. Search properties (renters and agents)");
                System.out.println("0. Exit");
                System.out.print("Choose option: ");

                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1 -> userService.registerUser(conn, scanner);

                    case 2 -> {
                        System.out.print("Email: ");
                        String email2 = scanner.nextLine();
                        if (!isRenter(conn, email2)) {
                            System.out.println("Only renters can add credit cards.");
                            break;
                        }
                        paymentService.addCreditCard(conn, scanner, email2);
                    }

                    case 3 -> {
                        System.out.print("Email: ");
                        String email3 = scanner.nextLine();
                        if (!isRenter(conn, email3)) {
                            System.out.println("Only renters can delete credit cards.");
                            break;
                        }
                        System.out.print("Card Number: ");
                        String card3 = scanner.nextLine();
                        paymentService.deleteCreditCard(conn, card3);
                    }

                    case 4 -> {
                        System.out.print("Email: ");
                        String email4 = scanner.nextLine();
                        if (!isRenter(conn, email4)) {
                            System.out.println("Only renters can add addresses.");
                            break;
                        }
                        System.out.print("Address Line 1: ");
                        String line1 = scanner.nextLine();
                        System.out.print("City: ");
                        String city4 = scanner.nextLine();
                        System.out.print("State: ");
                        String state4 = scanner.nextLine();
                        System.out.print("Zip: ");
                        String zip4 = scanner.nextLine();
                        paymentService.addAddress(conn, email4, line1, city4, state4, zip4);
                    }

                    case 5 -> {
                        System.out.print("Email: ");
                        String email5 = scanner.nextLine();
                        if (!isRenter(conn, email5)) {
                            System.out.println("Only renters can delete addresses.");
                            break;
                        }
                        System.out.print("Address ID: ");
                        int id5 = Integer.parseInt(scanner.nextLine());
                        paymentService.deleteAddress(conn, id5);
                    }

                    case 6, 7, 8 -> {
                        System.out.print("Email: ");
                        String emailP = scanner.nextLine();
                        if (!isAgent(conn, emailP)) {
                            System.out.println("Only agents can manage properties.");
                            break;
                        }
                        if (option == 6) {
                            System.out.print("Type: ");
                            String type = scanner.nextLine();
                            System.out.print("Sq Ft: ");
                            double sqft = Double.parseDouble(scanner.nextLine());
                            System.out.print("Address: ");
                            String addr = scanner.nextLine();
                            System.out.print("City: ");
                            String cityP = scanner.nextLine();
                            System.out.print("State: ");
                            String stateP = scanner.nextLine();
                            System.out.print("Price: ");
                            double price = Double.parseDouble(scanner.nextLine());
                            System.out.print("Description: ");
                            String desc = scanner.nextLine();
                            propertyService.addProperty(conn, emailP, type, sqft, addr, cityP, stateP, price, desc);
                        } else if (option == 7) {
                            System.out.print("Property ID: ");
                            int idP = Integer.parseInt(scanner.nextLine());
                            propertyService.deleteProperty(conn, idP);
                        } else {
                            System.out.print("Property ID: ");
                            int idP2 = Integer.parseInt(scanner.nextLine());
                            System.out.print("Field to modify (type, square_footage, price, description): ");
                            String field = scanner.nextLine();
                            System.out.print("New Value: ");
                            String newVal = scanner.nextLine();
                            propertyService.modifyProperty(conn, idP2, field, newVal);
                        }
                    }

                    case 9 -> {
                        System.out.print("Email: ");
                        String email9 = scanner.nextLine();
                        if (!isRenter(conn, email9)) {
                            System.out.println("Only renters can update addresses.");
                            break;
                        }
                        System.out.print("Address ID: ");
                        int id9 = Integer.parseInt(scanner.nextLine());
                        System.out.print("New Line 1: ");
                        String nl1 = scanner.nextLine();
                        System.out.print("New City: ");
                        String nc = scanner.nextLine();
                        System.out.print("New State: ");
                        String ns = scanner.nextLine();
                        System.out.print("New Zip: ");
                        String nz = scanner.nextLine();
                        String sql9 = "UPDATE address SET address_line1=?, city=?, state=?, zipcode=? WHERE address_id=?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql9)) {
                            stmt.setString(1, nl1);
                            stmt.setString(2, nc);
                            stmt.setString(3, ns);
                            stmt.setString(4, nz);
                            stmt.setInt(5, id9);
                            stmt.executeUpdate();
                        }
                        System.out.println("Address updated.");
                    }

                    case 10 -> {
                        System.out.print("Email: ");
                        String email10 = scanner.nextLine();
                        if (!isRenter(conn, email10)) {
                            System.out.println("Only renters can update credit cards.");
                            break;
                        }
                        System.out.print("Card Number: ");
                        String card10 = scanner.nextLine();
                        System.out.print("New Expiration Date (YYYY-MM-DD): ");
                        String exp10 = scanner.nextLine();
                        System.out.print("New CVV: ");
                        String cvv10 = scanner.nextLine();
                        String sql10 = "UPDATE credit_card SET expiration_date=?, CVV=? WHERE card_number=?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql10)) {
                            stmt.setDate(1, Date.valueOf(exp10));
                            stmt.setString(2, cvv10);
                            stmt.setString(3, card10);
                            stmt.executeUpdate();
                        }
                        System.out.println("Credit card updated.");
                    }

                    case 11 -> {
                        System.out.print("Email: ");
                        String email11 = scanner.nextLine();
                        if (!isRenter(conn, email11)) {
                            System.out.println("Only renters can book properties.");
                            break;
                        }
                        bookingService.bookProperty(conn, scanner, email11);
                    }

                    case 12 -> {
                        System.out.print("Email: ");
                        String email12 = scanner.nextLine();
                        if (!isRenter(conn, email12)) {
                            System.out.println("Only renters can view their bookings.");
                            break;
                        }
                        bookingService.viewBookingsForRenter(conn, email12);
                    }

                    case 13 -> {
                        System.out.print("Email: ");
                        String email13 = scanner.nextLine();
                        if (!isRenter(conn, email13)) {
                            System.out.println("Only renters can cancel bookings.");
                            break;
                        }
                        System.out.print("Booking ID: ");
                        int bookingId13 = Integer.parseInt(scanner.nextLine());
                        bookingService.cancelBookingByRenter(conn, email13, bookingId13);
                    }

                    case 14 -> {
                        System.out.print("Email: ");
                        String email14 = scanner.nextLine();
                        if (!isAgent(conn, email14)) {
                            System.out.println("Only agents can view bookings for their properties.");
                            break;
                        }
                        bookingService.viewBookingsForAgent(conn, email14);
                    }

                    case 15 -> {
                        System.out.print("Email: ");
                        String email15 = scanner.nextLine();
                        if (!isAgent(conn, email15)) {
                            System.out.println("Only agents can cancel bookings for their properties.");
                            break;
                        }
                        System.out.print("Booking ID: ");
                        int bookingId15 = Integer.parseInt(scanner.nextLine());
                        bookingService.cancelBookingByAgent(conn, email15, bookingId15);
                    }

                    case 16 -> {
                        // ——— Required filters ———
                        System.out.print("City (or blank for any): ");
                        String city = scanner.nextLine().trim();
                        System.out.print("State (or blank for any): ");
                        String state = scanner.nextLine().trim();
                        if (city.isEmpty() && state.isEmpty()) {
                            System.out.println("You must specify at least a city or a state.");
                            break;
                        }
                    
                        System.out.print("Available on date (YYYY-MM-DD): ");
                        String ds = scanner.nextLine().trim();
                        if (ds.isEmpty()) {
                            System.out.println("You must enter a date for availability.");
                            break;
                        }
                        LocalDate desiredDate = LocalDate.parse(ds);
                    
                        // ——— Optional filters ———
                        System.out.print("Min bedrooms (or blank): ");
                        String bd = scanner.nextLine().trim();
                        Integer minBedrooms = bd.isEmpty() ? null : Integer.parseInt(bd);

                        System.out.print("Min price (or blank): ");
                        String minP = scanner.nextLine().trim();
                        Double minPrice = minP.isEmpty() ? null : Double.parseDouble(minP);

                        System.out.print("Max price (or blank): ");
                        String maxP = scanner.nextLine().trim();
                        Double maxPrice = maxP.isEmpty() ? null : Double.parseDouble(maxP);

                        System.out.print("Property type (e.g. Apartment, House; blank for any): ");
                        String type = scanner.nextLine().trim();
                        if (type.isEmpty()) type = null;

                        System.out.print("Order by (price|bedrooms): ");
                        String orderBy = scanner.nextLine().trim().equalsIgnoreCase("bedrooms")
                                         ? "bedrooms" : "price";

                        propertyService.searchProperties(conn, city.isEmpty() ? null : city, state.isEmpty() ? null : state, type, minBedrooms, minPrice, maxPrice, desiredDate, orderBy);
                        break;
                        
                    }

                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }

                    default -> System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static boolean isRenter(Connection conn, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT 1 FROM prospective_renter WHERE email_address = ?");
        stmt.setString(1, email);
        return stmt.executeQuery().next();
    }

    private static boolean isAgent(Connection conn, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT 1 FROM agent WHERE email_address = ?");
        stmt.setString(1, email);
        return stmt.executeQuery().next();
    }
}