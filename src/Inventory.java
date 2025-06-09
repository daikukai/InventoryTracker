import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    private List<Product> products;
    private final String DATA_FILE = "inventory_data.dat"; // File to save/load data

    public Inventory() {
        products = new ArrayList<>();
        loadProducts(); // Load existing products when inventory is created
    }

    public void addProduct(Product product) {
        // Check if product ID already exists
        boolean idExists = products.stream()
                .anyMatch(p -> p.getProductId().equalsIgnoreCase(product.getProductId()));
        if (idExists) {
            System.out.println("Error: Product with ID '" + product.getProductId() + "' already exists.");

            return;
        }
        products.add(product);
        saveProducts(); // Save after adding
        System.out.println("Product added: " + product.getName());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Return a copy to prevent external modification
    }

    public List<Product> searchProductById(String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equalsIgnoreCase(productId))
                .collect(Collectors.toList());
    }

    //  Save/Load functionality
    public void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(products);
            System.out.println("Inventory saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast from Object to ArrayList<Product>
    private void loadProducts() {
        File file = new File(DATA_FILE);
        if (file.exists() && file.length() > 0) { // Check if file exists and is not empty
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                products = (List<Product>) ois.readObject();
                System.out.println("Inventory loaded from " + DATA_FILE + ". " + products.size() + " products loaded.");
            } catch (FileNotFoundException e) {
                // This case should be caught by file.exists() but good to have
                System.out.println("Inventory data file not found: " + DATA_FILE);
            } catch (IOException e) {
                System.err.println("Error loading inventory (IO): " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Error loading inventory (Class not found): " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No existing inventory data found or file is empty. Starting with an empty inventory.");
        }
    }
}
