import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookingService {
    public BookingService() { }

    public void bookProperty(Connection conn, Scanner scanner, String renterEmail) {
        try {
            System.out.print("Enter property ID to book: ");
            int propertyId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter start date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter end date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());
            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date.");
                return;
            }

            // availability check
            String availQ = 
              "SELECT COUNT(*) FROM booking " +
              "WHERE property_id=? AND NOT(end_date < ? OR start_date > ?)";
            PreparedStatement av = conn.prepareStatement(availQ);
            av.setInt(1, propertyId);
            av.setDate(2, Date.valueOf(startDate));
            av.setDate(3, Date.valueOf(endDate));
            ResultSet rsAv = av.executeQuery();
            rsAv.next();
            if (rsAv.getInt(1)>0) {
                System.out.println("Property already booked in that period.");
                return;
            }

            // cost lookup
            PreparedStatement p = conn.prepareStatement(
                "SELECT price FROM property WHERE property_id=?");
            p.setInt(1, propertyId);
            ResultSet rp = p.executeQuery();
            if (!rp.next()) {
                System.out.println("Invalid property ID.");
                return;
            }
            double price = rp.getDouble("price");
            long days = endDate.toEpochDay() - startDate.toEpochDay() + 1;
            double totalCost = price * days;

             // renter’s credit cards 
            PreparedStatement cardStmt = conn.prepareStatement(
                "SELECT card_number FROM users_cc WHERE email_address = ?");
            cardStmt.setString(1, renterEmail);
            ResultSet rsCards = cardStmt.executeQuery();
                    
            List<String> cards = new ArrayList<>();
            while (rsCards.next()) {
                cards.add(rsCards.getString("card_number"));
            }
            if (cards.isEmpty()) {
                System.out.println("You have no saved credit cards; please add one first.");
                return;
            }
            
            System.out.println("Select a credit card:");
            for (int i = 0; i < cards.size(); i++) {
                System.out.printf("  %d) %s%n", i + 1, cards.get(i));
            }
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(scanner.nextLine());
            String selectedCard = cards.get(choice - 1);


            // insert booking
            PreparedStatement ins = conn.prepareStatement(
              "INSERT INTO booking(renter_email, card_number, property_id, start_date, end_date) VALUES(?,?,?,?,?)");
            ins.setString(1, renterEmail);
            ins.setString(2, selectedCard);
            ins.setInt(3, propertyId);
            ins.setDate(4, Date.valueOf(startDate));
            ins.setDate(5, Date.valueOf(endDate));
            ins.executeUpdate();

            System.out.println("Booked! Total cost: $"+ totalCost);
        } catch (Exception e) {
            System.out.println("Error booking: "+e.getMessage());
        }
    }

    public void viewBookingsForRenter(Connection conn, String renterEmail) {
        try {
            PreparedStatement ps = conn.prepareStatement(
              "SELECT b.booking_id,p.address,p.city,p.state,b.start_date,b.end_date,p.price " +
              "FROM booking b JOIN property p ON b.property_id=p.property_id " +
              "WHERE b.renter_email=?");
            ps.setString(1, renterEmail);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your bookings:");
            while (rs.next()) {
                System.out.printf(
                  "ID:%d – %s, %s, %s from %s to %s cost $%.2f%n",
                  rs.getInt("booking_id"),
                  rs.getString("address"),
                  rs.getString("city"),
                  rs.getString("state"),
                  rs.getDate("start_date"),
                  rs.getDate("end_date"),
                  rs.getDouble("price") * 
                    (rs.getDate("end_date").toLocalDate().toEpochDay() -
                     rs.getDate("start_date").toLocalDate().toEpochDay() +1)
                );
            }
        } catch (Exception e) {
            System.out.println("Error viewing: "+e.getMessage());
        }
    }

    public void cancelBookingByRenter(Connection conn, String renterEmail, int bookingId) {
        try {
            PreparedStatement del = conn.prepareStatement(
              "DELETE FROM booking WHERE booking_id=? AND renter_email=?");
            del.setInt(1, bookingId);
            del.setString(2, renterEmail);
            int c = del.executeUpdate();
            System.out.println(c>0 ? "Canceled." : "Not found/unauthorized.");
        } catch (Exception e) {
            System.out.println("Error canceling: "+e.getMessage());
        }
    }

    public void viewBookingsForAgent(Connection conn, String agentEmail) {
        try {
            PreparedStatement ps = conn.prepareStatement(
              "SELECT b.booking_id,b.renter_email,p.address,p.city,p.state," +
              "b.start_date,b.end_date,p.price " +
              "FROM booking b " +
              "JOIN property p ON b.property_id=p.property_id " +
              "JOIN manages m ON p.property_id=m.property_id " +
              "WHERE m.email_address=?");
            ps.setString(1, agentEmail);
            ResultSet rs = ps.executeQuery();
            System.out.println("Bookings on your properties:");
            while (rs.next()) {
                System.out.printf(
                  "ID:%d renter:%s – %s, %s, %s from %s to %s cost $%.2f%n",
                  rs.getInt("booking_id"),
                  rs.getString("renter_email"),
                  rs.getString("address"),
                  rs.getString("city"),
                  rs.getString("state"),
                  rs.getDate("start_date"),
                  rs.getDate("end_date"),
                  rs.getDouble("price") * 
                    (rs.getDate("end_date").toLocalDate().toEpochDay() -
                     rs.getDate("start_date").toLocalDate().toEpochDay() +1)
                );
            }
        } catch (Exception e) {
            System.out.println("Error viewing for agent: "+e.getMessage());
        }
    }

    public void cancelBookingByAgent(Connection conn, String agentEmail, int bookingId) {
        try {
            // ensure agent manages it
            PreparedStatement chk = conn.prepareStatement(
              "SELECT 1 FROM booking b JOIN manages m ON b.property_id=m.property_id " +
              "WHERE b.booking_id=? AND m.email_address=?");
            chk.setInt(1, bookingId);
            chk.setString(2, agentEmail);
            ResultSet r = chk.executeQuery();
            if (!r.next()) {
                System.out.println("Not authorized or booking not found.");
                return;
            }
            PreparedStatement del = conn.prepareStatement(
              "DELETE FROM booking WHERE booking_id=?");
            del.setInt(1, bookingId);
            del.executeUpdate();
            System.out.println("Booking canceled by agent.");
        } catch (Exception e) {
            System.out.println("Error canceling for agent: "+e.getMessage());
        }
    }
}
