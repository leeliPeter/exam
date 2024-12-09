import java.sql.*;

public class App {
    public static void main(String[] args) {
        try {
            // 1. Load the Oracle JDBC driver
            Class.forName(OracleInfo.DRIVER_CLASS_ORACLE);
            System.out.println("Driver loaded successfully");

            // 2. Establish connection to Oracle database
            Connection connection = DriverManager.getConnection(OracleInfo.URL, OracleInfo.U, OracleInfo.P);
            System.out.println("Connected to Oracle Database");

            // 3. Create the PRODUCTS table
            try {
                String createTableSQL = """
                        CREATE TABLE PRODUCTS (
                            product_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            product_name VARCHAR2(100) UNIQUE NOT NULL,
                            category VARCHAR2(50) NOT NULL,
                            price NUMBER(10,2) NOT NULL,
                            quantity NUMBER(10) NOT NULL
                        )""";

                Statement statement = connection.createStatement();
                statement.execute(createTableSQL);
                System.out.println("PRODUCTS table created successfully");
                statement.close();
            } catch (SQLException e) {
                if (e.getErrorCode() == 955) {
                    System.out.println("Table 'PRODUCTS' already exists");
                } else {
                    throw e;
                }
            }

            // 4. Close the connection
            connection.close();
            System.out.println("Connection closed");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
