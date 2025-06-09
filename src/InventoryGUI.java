import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InventoryGUI extends JFrame {
    private Inventory inventory;

    // GUI Components for Add Product
    private JTextField productIdField;
    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JButton addButton;

    // GUI Components for Search Product
    private JTextField searchIdField;
    private JButton searchButton;
    private JButton viewAllButton;

    // GUI Components for Display
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    public InventoryGUI() {
        inventory = new Inventory(); // Initialize inventory manager

        setTitle("Inventory Tracker");
        setSize(800, 600); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        initializeComponents();
        setupLayout();
        addListeners();
        updateProductTable(inventory.getAllProducts()); // Load existing products into table on startup
    }

    private void initializeComponents() {
        // Add Product Form
        productIdField = new JTextField(15);
        nameField = new JTextField(15);
        quantityField = new JTextField(15);
        priceField = new JTextField(15);
        addButton = new JButton("Add Product");

        // Search Product Form
        searchIdField = new JTextField(15);
        searchButton = new JButton("Search by ID");
        viewAllButton = new JButton("View All Products");

        // Product Table
        String[] columnNames = {"Product ID", "Name", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        productTable = new JTable(tableModel);
        tableScrollPane = new JScrollPane(productTable);
    }

    private void setupLayout() {
        // Main panel using BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Gap of 10 pixels

        // --- North Panel: Add Product Form (using GridLayout for labels and fields) ---
        JPanel addPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // 5 rows, 2 columns, 5px h/v gap
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Product"));
        addPanel.add(new JLabel("Product ID:"));
        addPanel.add(productIdField);
        addPanel.add(new JLabel("Name:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Quantity:"));
        addPanel.add(quantityField);
        addPanel.add(new JLabel("Price:"));
        addPanel.add(priceField);
        addPanel.add(new JLabel("")); // Empty label for spacing
        addPanel.add(addButton);

        // --- West Panel: Search/View All (using BorderLayout for components, GridLayout for buttons) ---
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & View Options"));

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Use FlowLayout for text field and button
        searchInputPanel.add(new JLabel("Search ID:"));
        searchInputPanel.add(searchIdField);
        searchInputPanel.add(searchButton);

        JPanel viewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the "View All" button
        viewButtonPanel.add(viewAllButton);

        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(viewButtonPanel, BorderLayout.CENTER);


        // --- Center Panel: Product Table (will fill available space) ---
        // This is handled by BorderLayout directly placing tableScrollPane in CENTER
        mainPanel.add(addPanel, BorderLayout.NORTH); // Add form at the top
        mainPanel.add(searchPanel, BorderLayout.WEST); // Search panel to the left
        mainPanel.add(tableScrollPane, BorderLayout.CENTER); // Table in the center

        add(mainPanel); // Add the main panel to the JFrame
    }

    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProduct();
            }
        });

        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProductTable(inventory.getAllProducts());
                searchIdField.setText(""); // Clear search field
            }
        });
    }

    private void addProduct() {
        try {
            String productId = productIdField.getText().trim();
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (productId.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product ID and Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product newProduct = new Product(productId, name, quantity, price);
            List<Product> existing = inventory.searchProductById(productId);

            if (!existing.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product with ID '" + productId + "' already exists. Please use a unique ID.", "Duplicate ID", JOptionPane.WARNING_MESSAGE);
                return;
            }

            inventory.addProduct(newProduct);
            updateProductTable(inventory.getAllProducts()); // Refresh table
            clearAddFormFields(); // Clear fields after successful add
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Quantity and Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProduct() {
        String searchId = searchIdField.getText().trim();
        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Product ID to search.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Product> searchResults = inventory.searchProductById(searchId);
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No product found with ID: " + searchId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
        updateProductTable(searchResults); // Display search results
    }

    private void updateProductTable(List<Product> productsToDisplay) {
        tableModel.setRowCount(0); // Clear existing rows

        for (Product product : productsToDisplay) {
            Object[] rowData = {
                    product.getProductId(),
                    product.getName(),
                    product.getQuantity(),
                    String.format("%.2f", product.getPrice()) // Format price to 2 decimal places
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearAddFormFields() {
        productIdField.setText("");
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }
}
