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

    public void searchProperties(Connection conn, String city, String state, Double minPrice, Double maxPrice, String type, Integer bedrooms, String orderBy) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM property WHERE 1=1");

        if (city != null && !city.isEmpty()) {
            query.append(" AND LOWER(city) = LOWER(?)");
        }
        if (state != null && !state.isEmpty()) {
            query.append(" AND LOWER(state) = LOWER(?)");
        }
        if (minPrice != null) {
            query.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            query.append(" AND price <= ?");
        }
        if (type != null && !type.isEmpty()) {
            query.append(" AND LOWER(type) = LOWER(?)");
        }
        if ("price".equals(orderBy)) {
            query.append(" ORDER BY price ASC");
        } else if ("bedrooms".equals(orderBy)) {
            query.append(" ORDER BY bedrooms ASC");
        }

        PreparedStatement stmt = conn.prepareStatement(query.toString());

        int i = 1;
        if (city != null && !city.isEmpty()) stmt.setString(i++, city);
        if (state != null && !state.isEmpty()) stmt.setString(i++, state);
        if (minPrice != null) stmt.setDouble(i++, minPrice);
        if (maxPrice != null) stmt.setDouble(i++, maxPrice);
        if (type != null && !type.isEmpty()) stmt.setString(i++, type);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("property_id");  
            String t = rs.getString("type");
            double sqft = rs.getDouble("square_footage");
            String addr = rs.getString("address");
            String c = rs.getString("city");
            String s = rs.getString("state");
            double price = rs.getDouble("price");
            String desc = rs.getString("description");

            System.out.printf("🏠 ID: %d | %s | %.0f sqft | $%.2f | %s, %s | %s | %s\n",
                            id, t, sqft, price, addr, c, s, desc);
        }
        System.out.println("✅ Property search completed.");

    }
}
