import java.sql.*;

public class PropertyService {

    public void addProperty(Connection conn, String agentEmail, String type, double sqft, String address, String city, String state, double price, String desc) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO property (type, square_footage, address, city, state, price, description) VALUES (?, ?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, type);
        stmt.setDouble(2, sqft);
        stmt.setString(3, address);
        stmt.setString(4, city);
        stmt.setString(5, state);
        stmt.setDouble(6, price);
        stmt.setString(7, desc);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int propertyId = rs.getInt(1);
            PreparedStatement link = conn.prepareStatement(
                "INSERT INTO manages (email_address, property_id) VALUES (?, ?)");
            link.setString(1, agentEmail);
            link.setInt(2, propertyId);
            link.executeUpdate();
        }

        System.out.println("✅ Property added.");
    }

    public void deleteProperty(Connection conn, int propertyId) throws SQLException {
        PreparedStatement unmanage = conn.prepareStatement(
            "DELETE FROM manages WHERE property_id = ?");
        unmanage.setInt(1, propertyId);
        unmanage.executeUpdate();

        PreparedStatement del = conn.prepareStatement(
            "DELETE FROM property WHERE property_id = ?");
        del.setInt(1, propertyId);
        del.executeUpdate();

        System.out.println("✅ Property deleted.");
    }

    public void modifyProperty(Connection conn, int propertyId, String field, String newValue) throws SQLException {
        if (!field.matches("^(type|square_footage|price|description)$")) {
            throw new IllegalArgumentException("Invalid field to update.");
        }

        String query = "UPDATE property SET " + field + " = ? WHERE property_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, newValue);
        stmt.setInt(2, propertyId);
        stmt.executeUpdate();

        System.out.println("✅ Property updated.");
    }
}
