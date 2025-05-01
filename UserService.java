import java.sql.*;
import java.math.BigDecimal;
import java.util.Scanner;

public class UserService {

    public void registerUser(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Is Agent? (yes/no): ");
        boolean isAgent = scanner.nextLine().trim().equalsIgnoreCase("yes");

        System.out.print("Is Renter? (yes/no): ");
        boolean isRenter = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (!isAgent && !isRenter) {
            System.out.println("Must register as at least an agent or a renter.");
            return;
        }

        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO users (email_address, name) VALUES (?, ?)");
        stmt.setString(1, email);
        stmt.setString(2, name);
        stmt.executeUpdate();

        if (isAgent) {
            System.out.print("Job Title: ");
            String jobTitle = scanner.nextLine();
            System.out.print("Real Estate Agency: ");
            String agency = scanner.nextLine();
            System.out.print("Contact Info: ");
            String contact = scanner.nextLine();

            PreparedStatement agentStmt = conn.prepareStatement(
                "INSERT INTO agent (email_address, job_title, real_estate_agency, contact_info) VALUES (?, ?, ?, ?)");
            agentStmt.setString(1, email);
            agentStmt.setString(2, jobTitle);
            agentStmt.setString(3, agency);
            agentStmt.setString(4, contact);
            agentStmt.executeUpdate();
        }

        if (isRenter) {
            System.out.print("Desired Move-in Date (YYYY-MM-DD): ");
            String moveIn = scanner.nextLine();
            System.out.print("Preferred Location: ");
            String location = scanner.nextLine();
            System.out.print("Budget: ");
            BigDecimal budget = new BigDecimal(scanner.nextLine());

            PreparedStatement renterStmt = conn.prepareStatement(
                "INSERT INTO prospective_renter (email_address, desired_movein_date, preferred_location, budget) VALUES (?, ?, ?, ?)");
            renterStmt.setString(1, email);
            renterStmt.setDate(2, Date.valueOf(moveIn));
            renterStmt.setString(3, location);
            renterStmt.setBigDecimal(4, budget);
            renterStmt.executeUpdate();
        }

        System.out.println("User registered successfully.");
    }
}
