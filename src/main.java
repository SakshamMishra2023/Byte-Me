import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

public class main {
    private static menu_mng menumng = menu_mng.getInstance();
    static order_mng ordermng = order_mng.getInstance();

    public static void main(String[] args) {
        System.out.println("Welcome to Byte Me! Canteen System");
        System.out.println("1. CLI Interface");
        System.out.println("2. GUI Interface");
        System.out.println("3. Exit");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                runCLI();
                menumng.saveMenuToFile();
                break;
            case 2:
                runGUI();
                break;
            case 3:
                System.out.println("Exiting Byte Me! system. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        scanner.close();
    }

    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Select User Type:");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    admin_inf admininf = new admin_inf();
                    adminOperations(admininf);
                    break;
                case 2:
                    customerLogin();
                    break;
                case 3:
                    menumng.saveMenuToFile();
                    ordermng.writePendingOrdersToFile();
                    exit = true;

                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void adminOperations(admin_inf admininf) {
        Scanner scanner = new Scanner(System.in);
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("Admin Menu:");
            System.out.println("1. Manage Menu");
            System.out.println("2. Manage Orders");
            System.out.println("3. Generate Daily Sales Report");
            System.out.println("4. Back to Main Menu");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    admininf.manageMenu();
                    break;
                case 2:
                    admininf.manageOrders();
                    break;
                case 3:
                    admininf.generateDailySalesReport();
                    break;
                case 4:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void customerLogin() {
        Scanner scanner = new Scanner(System.in);
        CustomerManager customerManager = CustomerManager.getInstance();

        System.out.println("Customer Login:");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Are you a VIP customer? (yes/no): ");
        boolean isVIP = scanner.nextLine().equalsIgnoreCase("yes");

        customer customer = customerManager.loginOrRegister(name, email, isVIP);

        customer_interference customerinterference = new customer_interference(customer);
        customerOperations(customerinterference);
    }

    private static void customerOperations(customer_interference customerinterference){
        Scanner scanner = new Scanner(System.in);
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("Customer Menu:");
            System.out.println("1. Browse Menu");
            System.out.println("2. View Cart");
            System.out.println("3. Place Order");
            System.out.println("4. Cart Operations");
            System.out.println("5. Give Review");
            System.out.println("6. View Review");
            System.out.println("7. Back to Main Menu");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    customerinterference.browseMenu();
                    break;
                case 2:
                    customerinterference.viewCart();
                    break;
                case 3:
                    customerinterference.placeOrder();
                    break;
                case 4:
                    customerinterference.manageCart();
                    break;
                case 5:
                    customerinterference.addReview();
                    break;
                case 6:
                    customerinterference.viewReviews();
                    break;
                case 7:
                    backToMain = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void runGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Byte Me! Canteen System");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);


            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);


            JPanel menuPage = createMenuPage(cardLayout, mainPanel);
            mainPanel.add(menuPage, "MENU");


            JPanel ordersPage = createOrdersPage(cardLayout, mainPanel);
            mainPanel.add(ordersPage, "ORDERS");

            mainFrame.add(mainPanel);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }

    private static JPanel createMenuPage(CardLayout cardLayout, JPanel mainPanel) {
        JPanel menuPage = new JPanel(new BorderLayout());
        final String MENU_FILE = "C:\\Users\\saksham mishra\\Saved Games\\ByteThis\\src\\Menu_items.txt";


        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        JLabel titleLabel = new JLabel("Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        menuPage.add(headerPanel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);

        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String headerLine = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String name = parts[0].trim();
                        String category = parts[1].trim();
                        int price = Integer.parseInt(parts[2].trim());

                        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                        JLabel nameLabel = new JLabel(name);
                        JLabel categoryLabel = new JLabel(" - " + category);
                        JLabel priceLabel = new JLabel(String.format(" $%d", price));

                        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));

                        itemPanel.add(nameLabel);
                        itemPanel.add(categoryLabel);
                        itemPanel.add(priceLabel);

                        contentPanel.add(itemPanel);

                        contentPanel.add(new JSeparator());
                    }
                }
                catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e){

            JLabel errorLabel = new JLabel("Error loading menu items: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);
            e.printStackTrace();
        }

        menuPage.add(scrollPane, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton ordersButton = new JButton("View Orders");
        JButton exitButton = new JButton("Exit");

        ordersButton.addActionListener(e -> cardLayout.show(mainPanel, "ORDERS"));
        exitButton.addActionListener(e -> System.exit(0));

        navPanel.add(ordersButton);
        navPanel.add(exitButton);
        menuPage.add(navPanel, BorderLayout.SOUTH);

        return menuPage;
    }

    private static JPanel createOrdersPage(CardLayout cardLayout, JPanel mainPanel) {
        JPanel ordersPage = new JPanel(new BorderLayout());
        final String PENDING_ORDERS_FILE = "C:\\Users\\saksham mishra\\Saved Games\\ByteThis\\src\\pending_orders.txt";

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 153, 255));
        JLabel titleLabel = new JLabel("Pending Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        ordersPage.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);

        try (BufferedReader reader = new BufferedReader(new FileReader(PENDING_ORDERS_FILE))) {

            String headerLine = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    String orderId = parts[0].trim();
                    String customerName = parts[1].trim();
                    String status = parts[2].trim();
                    String items = parts[3].trim();
                    String totalCost = parts[4].trim();
                    String specialRequest = parts[5].trim();


                    JPanel orderPanel = new JPanel();
                    orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                    orderPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));


                    JPanel headerPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JLabel orderIdLabel = new JLabel("Order #" + orderId);
                    orderIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    headerPane.add(orderIdLabel);


                    JPanel infoPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    infoPane.add(new JLabel("Customer: " + customerName));
                    JLabel statusLabel = new JLabel("Status: " + status);
                    statusLabel.setForeground(getStatusColor(status));
                    infoPane.add(statusLabel);


                    JPanel itemsPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    itemsPane.add(new JLabel("Items: " + items));


                    JPanel costPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    costPane.add(new JLabel("Total Cost: $" + totalCost));


                    JPanel requestPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    if (!specialRequest.equals("None")) {
                        requestPane.add(new JLabel("Special Request: " + specialRequest));
                    }


                    orderPanel.add(headerPane);
                    orderPanel.add(infoPane);
                    orderPanel.add(itemsPane);
                    orderPanel.add(costPane);
                    if (!specialRequest.equals("None")) {
                        orderPanel.add(requestPane);
                    }

                    contentPanel.add(orderPanel);
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        catch(IOException e){

            JLabel errorLabel = new JLabel("Error loading orders: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);
            e.printStackTrace();
        }

        ordersPage.add(scrollPane, BorderLayout.CENTER);


        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton menuButton = new JButton("View Menu");
        JButton exitButton = new JButton("Exit");


        menuButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        exitButton.addActionListener(e -> System.exit(0));


        navPanel.add(menuButton);

        navPanel.add(exitButton);
        ordersPage.add(navPanel, BorderLayout.SOUTH);

        return ordersPage;
    }


    private static Color getStatusColor(String status) {
        switch (status) {
            case "PREPARING":
                return new Color(255, 165, 0); // Orange
            case "OUT_FOR_DELIVERY":
                return new Color(0, 0, 255);   // Blue
            case "DELIVERED":
                return new Color(0, 128, 0);   // Green
            case "CANCELED":
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }
}
