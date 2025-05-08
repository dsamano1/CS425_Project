import java.sql.*;
import java.time.LocalDate;

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
    public void searchProperties(Connection conn,
                             String city,
                             String state,
                             String type,
                             Integer minBedrooms,
                             Double minPrice,
                             Double maxPrice,
                             LocalDate desiredDate,  // now required
                             String orderBy) throws SQLException {

    StringBuilder sql = new StringBuilder("""
        SELECT
          p.property_id,
          p.city,
          p.state,
          p.type,
          p.square_footage,
          p.price,
          p.description,                             -- added
          COALESCE(a.number_of_rooms, h.number_of_rooms) AS bedrooms
        FROM property p
        LEFT JOIN apartments a ON p.property_id = a.property_id
        LEFT JOIN houses h     ON p.property_id = h.property_id
        WHERE 1=1
        """);

    if (city     != null)     sql.append(" AND LOWER(p.city)=LOWER(?)");
    if (state    != null)     sql.append(" AND LOWER(p.state)=LOWER(?)");
    if (type     != null)     sql.append(" AND LOWER(p.type)=LOWER(?)");
    if (minPrice != null)     sql.append(" AND p.price >= ?");
    if (maxPrice != null)     sql.append(" AND p.price <= ?");
    if (minBedrooms != null)  sql.append(" AND COALESCE(a.number_of_rooms, h.number_of_rooms) >= ?");
    
    // date-availability filter
    sql.append("""
      AND NOT EXISTS (
        SELECT 1 FROM booking b
         WHERE b.property_id = p.property_id
           AND NOT (b.end_date   < ?
                 OR b.start_date > ?)
      )
    """);

    // ordering
    if (orderBy.equals("bedrooms")) sql.append(" ORDER BY bedrooms ASC");
    else                            sql.append(" ORDER BY p.price ASC");

    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
        int idx = 1;
        if (city      != null) ps.setString(idx++, city);
        if (state     != null) ps.setString(idx++, state);
        if (type      != null) ps.setString(idx++, type);
        if (minPrice  != null) ps.setDouble(idx++, minPrice);
        if (maxPrice  != null) ps.setDouble(idx++, maxPrice);
        if (minBedrooms != null) ps.setInt(idx++, minBedrooms);
        // bind the desiredDate twice for the NOT EXISTS subquery
        ps.setDate(idx++, Date.valueOf(desiredDate));
        ps.setDate(idx++, Date.valueOf(desiredDate));

        ResultSet rs = ps.executeQuery();
        System.out.printf(
          "%-4s %-10s %-6s %-10s %-6s %-8s %-8s %-30s%n",
          "ID", "City", "State", "Type", "SqFt", "Price", "Beds", "Description"
        );
        while (rs.next()) {
            System.out.printf(
              "%-4d %-10s %-6s %-10s %-6d $%-7.2f %-8d %-30s%n",
              rs.getInt("property_id"),
              rs.getString("city"),
              rs.getString("state"),
              rs.getString("type"),
              rs.getInt("square_footage"),
              rs.getDouble("price"),
              rs.getInt("bedrooms"),
              rs.getString("description")
            );
        }
    }
}

}
