import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static Map<String, Product> productMap = new HashMap<>();
    private static Connection connection;

    public static void main(String[] args) {
        try {
            // 1. Initialize database connection
            initializeDatabase();

            // 2. Load existing products into HashMap
            loadProductsFromDatabase();

            // 3. Demo operations
            // Add new products
            addProduct(new Product("Laptop", "Electronics", 999.99, 10));
            addProduct(new Product("Coffee Maker", "Appliances", 79.99, 15));

            // Search for a product
            Product laptop = searchProduct("Laptop");
            if (laptop != null) {
                System.out.println("Found product: " + laptop);
            }

            // Update a product
            updateProduct("Laptop", 1099.99, 8);

            // Delete a product
            deleteProduct("Coffee Maker");

            // Display all products
            System.out.println("\nAll Products:");
            displayAllProducts();

            // 4. Close connection
            connection.close();
            System.out.println("\nConnection closed");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws ClassNotFoundException, SQLException {
        Class.forName(OracleInfo.DRIVER_CLASS_ORACLE);
        connection = DriverManager.getConnection(OracleInfo.URL, OracleInfo.U, OracleInfo.P);
        System.out.println("Connected to Oracle Database");
    }

    private static void loadProductsFromDatabase() throws SQLException {
        String query = "SELECT * FROM PRODUCTS";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"));
                productMap.put(product.getName(), product);
            }
            System.out.println("Products loaded from database");
        }
    }

    private static void addProduct(Product product) throws SQLException {
        if (productMap.containsKey(product.getName())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }

        String sql = "INSERT INTO PRODUCTS (product_name, category, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, new String[] { "product_id" })) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
        }

        productMap.put(product.getName(), product);
        System.out.println("Added product: " + product);
    }

    private static void updateProduct(String name, double newPrice, int newQuantity) throws SQLException {
        Product product = productMap.get(name);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        String sql = "UPDATE PRODUCTS SET price = ?, quantity = ? WHERE product_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newQuantity);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }

        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
        System.out.println("Updated product: " + product);
    }

    private static void deleteProduct(String name) throws SQLException {
        if (!productMap.containsKey(name)) {
            throw new IllegalArgumentException("Product not found");
        }

        String sql = "DELETE FROM PRODUCTS WHERE product_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }

        productMap.remove(name);
        System.out.println("Deleted product: " + name);
    }

    private static Product searchProduct(String name) {
        return productMap.get(name);
    }

    private static void displayAllProducts() {
        for (Product product : productMap.values()) {
            System.out.println(product);
        }
    }
}
