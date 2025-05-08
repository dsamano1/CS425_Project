import java.sql.*;
import java.util.Scanner;

public class PaymentService {

    public void addCreditCard(Connection conn, Scanner scanner, String email) throws SQLException {
        System.out.print("Card Number: ");
        String cardNum = scanner.nextLine();
        System.out.print("Expiration Date (YYYY-MM-DD): ");
        String expDate = scanner.nextLine();
        System.out.print("CVV: ");
        String cvv = scanner.nextLine();

        // Show available addresses
        PreparedStatement addressStmt = conn.prepareStatement(
            "SELECT a.address_id, a.address_line1, a.city, a.state, a.zipcode " +
            "FROM address a JOIN renter_address ra ON a.address_id = ra.address_id " +
            "WHERE ra.email_address = ?");
        addressStmt.setString(1, email);
        ResultSet rs = addressStmt.executeQuery();

        System.out.println("Available addresses:");
        while (rs.next()) {
            System.out.printf("  ID %d: %s, %s, %s %s\n",
                rs.getInt("address_id"),
                rs.getString("address_line1"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("zipcode"));
        }

        System.out.print("Billing address ID: ");
        int addressId = Integer.parseInt(scanner.nextLine());

        PreparedStatement cardStmt = conn.prepareStatement(
            "INSERT INTO credit_card (card_number, expiration_date, CVV) VALUES (?, ?, ?)");
        cardStmt.setString(1, cardNum);
        cardStmt.setDate(2, Date.valueOf(expDate));
        cardStmt.setString(3, cvv);
        cardStmt.executeUpdate();

        PreparedStatement userLink = conn.prepareStatement(
            "INSERT INTO users_cc (card_number, email_address) VALUES (?, ?)");
        userLink.setString(1, cardNum);
        userLink.setString(2, email);
        userLink.executeUpdate();

        PreparedStatement billingLink = conn.prepareStatement(
            "INSERT INTO credit_card_address (address_id, card_number) VALUES (?, ?)");
        billingLink.setInt(1, addressId);
        billingLink.setString(2, cardNum);
        billingLink.executeUpdate();

        System.out.println("✅ Credit card added.");
    }

    public void deleteCreditCard(Connection conn, String cardNum) throws SQLException {
        PreparedStatement delBilling = conn.prepareStatement("DELETE FROM credit_card_address WHERE card_number = ?");
        delBilling.setString(1, cardNum);
        delBilling.executeUpdate();

        PreparedStatement delLink = conn.prepareStatement("DELETE FROM users_cc WHERE card_number = ?");
        delLink.setString(1, cardNum);
        delLink.executeUpdate();

        PreparedStatement delCard = conn.prepareStatement("DELETE FROM credit_card WHERE card_number = ?");
        delCard.setString(1, cardNum);
        delCard.executeUpdate();

        System.out.println("✅ Credit card deleted.");
    }

    public void addAddress(Connection conn, String email, String line1, String city, String state, String zip) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO address (address_line1, city, state, zipcode) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, line1);
        stmt.setString(2, city);
        stmt.setString(3, state);
        stmt.setString(4, zip);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int addressId = rs.getInt(1);
            PreparedStatement link = conn.prepareStatement(
                "INSERT INTO renter_address (address_id, email_address) VALUES (?, ?)");
            link.setInt(1, addressId);
            link.setString(2, email);
            link.executeUpdate();
        }

        System.out.println("✅ Address added.");
    }

    public void deleteAddress(Connection conn, int addressId) throws SQLException {
        PreparedStatement check = conn.prepareStatement("SELECT * FROM credit_card_address WHERE address_id = ?");
        check.setInt(1, addressId);
        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            throw new SQLException("❌ Cannot delete address; it is linked to a credit card.");
        }

        PreparedStatement delRenter = conn.prepareStatement("DELETE FROM renter_address WHERE address_id = ?");
        delRenter.setInt(1, addressId);
        delRenter.executeUpdate();

        PreparedStatement delAddress = conn.prepareStatement("DELETE FROM address WHERE address_id = ?");
        delAddress.setInt(1, addressId);
        delAddress.executeUpdate();

        System.out.println("✅ Address deleted.");
    }
}
