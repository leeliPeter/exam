public class App {
    public static void main(String[] args) {
        try {
            ProductManager manager = new ProductManager();

            // Add some products
            System.out.println("\nAdding products:");
            // manager.addProduct("Laptop", "Electronics", 999.99, 10);
            // manager.addProduct("Coffee Maker", "Appliances", 79.99, 15);
            // manager.addProduct("Desk Chair", "Furniture", 199.99, 5);

            // // Search for a product
            // System.out.println("\nSearching for product:");
            // manager.searchProduct("Laptop");

            // // Update a product
            // System.out.println("\nUpdating product:");
            manager.updateProduct("Laptop", 90.99, 9);

            // Delete a product by name
            // System.out.println("\nDeleting product:");
            // manager.deleteProduct("Laptop");

            // Display all products
            manager.displayAllProducts();

            // Close connection
            manager.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
