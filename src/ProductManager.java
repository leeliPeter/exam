import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ProductManager {
    private Map<String, Product> productMap;
    private Connection connection;

    public ProductManager() throws SQLException, ClassNotFoundException {
        productMap = new HashMap<>();
        initializeDatabase();
        loadProductsFromDatabase();
    }

    private void initializeDatabase() throws ClassNotFoundException, SQLException {
        Class.forName(OracleInfo.DRIVER_CLASS_ORACLE);
        connection = DriverManager.getConnection(OracleInfo.URL, OracleInfo.U, OracleInfo.P);
        System.out.println("Connected to Oracle Database");
    }

    private void loadProductsFromDatabase() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                productMap.put(product.getName(), product);
            }
            System.out.println("Products loaded from database");
        }
    }

    public void addProduct(String name, String category, double price, int quantity) throws SQLException {
        // Check for duplicate product
        if (productMap.containsKey(name)) {
            throw new IllegalArgumentException("Product with name '" + name + "' already exists");
        }

        // Create new product
        Product product = new Product(name, category, price, quantity);

        // Add to database
        String sql = "INSERT INTO PRODUCTS (product_name, category, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[] { "product_id" })) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            
            pstmt.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
        }

        // Add to HashMap
        productMap.put(name, product);
        System.out.println("Added product: " + product);
    }

    public void updateProduct(String name, double newPrice, int newQuantity) throws SQLException {
        Product product = productMap.get(name);
        if (product == null) {
            throw new IllegalArgumentException("Product '" + name + "' not found");
        }

        // Update database
        String sql = "UPDATE PRODUCTS SET price = ?, quantity = ? WHERE product_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newQuantity);
            pstmt.setString(3, name);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
        }

        // Update HashMap
        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
        System.out.println("Updated product: " + product);
    }

    public void deleteProduct(String name) throws SQLException {
        if (!productMap.containsKey(name)) {
            throw new IllegalArgumentException("Product '" + name + "' not found");
        }

        // Delete from database
        String sql = "DELETE FROM PRODUCTS WHERE product_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
        }

        // Remove from HashMap
        Product removedProduct = productMap.remove(name);
        System.out.println("Deleted product: " + removedProduct);
    }

    public Product searchProduct(String name) {
        Product product = productMap.get(name);
        if (product == null) {
            System.out.println("Product '" + name + "' not found");
        } else {
            System.out.println("Found product: " + product);
        }
        return product;
    }

    public void displayAllProducts() {
        if (productMap.isEmpty()) {
            System.out.println("No products in inventory");
            return;
        }
        
        System.out.println("\nAll Products:");
        for (Product product : productMap.values()) {
            System.out.println(product);
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed");
        }
    }
} 