import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

// CustomerInterface.java
public class customer_interference implements Serializable{
    private menu_mng menumng;
    private order_mng ordermng;
    private customer customer;
    private cart cart;


    public customer_interference(customer customer) {
        this.menumng = menu_mng.getInstance();
        this.ordermng = order_mng.getInstance();
        this.customer = customer;
        this.cart = new cart();

    }


    public void browseMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean browsing = true;

        while (browsing) {
            System.out.println("Browse Menu:");
            List<food_item> allItems = menumng.getAllItems();
            for (food_item item : allItems) {
                System.out.println(item.getName() + " - $" + item.get_price() + " - " + item.get_category() + " - " + (item.is_available() ? "Available" : "Unavailable"));
            }

            System.out.println("Options:");
            System.out.println("1. Search items");
            System.out.println("2. Filter by category");
            System.out.println("3. Sort by price");
            System.out.println("4. Add item to cart");
            System.out.println("5. Order from history");
            System.out.println("6. View past Orders");
            System.out.println("7. Exit browsing");
            System.out.print("Choose an option (1-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchItems(scanner);
                    break;
                case 2:
                    filterByCategory(scanner);
                    break;
                case 3:
                    sortByPrice(scanner);
                    break;
                case 4:
                    addItemToCart(scanner);
                    break;
                case 5:
                    reorderFromHistory();
                    break;
                case 6:
                    viewPastOrders();
                    break;
                case 7:
                    browsing = false;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    public void cancelOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Order ID to cancel: ");
        int orderId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        order orderToCancel = findOrderById(orderId);

        if (orderToCancel == null) {
            System.out.println("Order not found.");
            return;
        }

        if (orderToCancel.getStatus() == order_stat.RECEIVED ||
                orderToCancel.getStatus() == order_stat.PREPARING) {
            orderToCancel.setStatus(order_stat.CANCELED);
            System.out.println("Order " + orderId + " has been canceled.");
        }
        else {
            System.out.println("Sorry, this order can no longer be canceled.");
        }
    }
    public void trackOrders() {
        List<order> customerOrders = ordermng.getCustomerOrders(customer);

        if (customerOrders.isEmpty()) {
            System.out.println("You have no recent orders.");
            return;
        }

        System.out.println("Your Orders:");
        for (order o : customerOrders) {
            System.out.println("Order ID: " + o.getOrderId() +
                    " | Status: " + o.getStatus() +
                    " | Total Cost: $" + o.getTotalCost());
        }
    }
    public void viewPastOrders() {
        Map<LocalDateTime, List<order>> pastOrders = customer.getOrderHistory();

        if (pastOrders.isEmpty()) {
            System.out.println("No past orders found.");
            return;
        }

        System.out.println("Past Orders:");
        for (Map.Entry<LocalDateTime, List<order>> entry : pastOrders.entrySet()) {
            LocalDateTime orderDate = entry.getKey();
            for (order o : entry.getValue()) {
                System.out.println("Order Date: " + orderDate +
                        " | Order ID: " + o.getOrderId() +
                        " | Total Cost: $" + o.getTotalCost() +
                        " | Status: " + o.getStatus());
            }
        }
    }

    private void modifyItemQuantity(Scanner scanner) {
        System.out.print("Enter item name to modify: ");
        String itemName = scanner.nextLine();
        food_item item = menumng.getItemByName(itemName);

        if (item != null && cart.getItems().containsKey(item)) {
            System.out.print("Enter new quantity: ");
            int newQuantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            cart.updateItemQuantity(item, newQuantity);
            System.out.println("Quantity updated.");
            viewCart();
        } else {
            System.out.println("Item not found in cart.");
        }
    }
    public void manageCart() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nCart Management:");
            System.out.println("1. View Cart");
            System.out.println("2. Add Item");
            System.out.println("3. Modify Quantity");
            System.out.println("4. Remove Item");
            System.out.println("5. Checkout");
            System.out.println("6. Track Orders");
            System.out.println("7. Cancel Order");
            System.out.println("8. Exit Cart");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewCart();
                    break;
                case 2:
                    addItemToCart(scanner);
                    break;
                case 3:
                    modifyItemQuantity(scanner);
                    break;
                case 4:
                    removeItemFromCart(scanner);
                    break;
                case 5:
                    placeOrder();
                    return;
                case 6:
                    trackOrders();
                    return;
                case 7:
                    cancelOrder();
                    return;
                case 8:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    public void reorderFromHistory() {
        Map<String, List<food_item>> personalHistory = customer.getPersonalMenuHistory();
        Map<String, List<Integer>> orderFrequency = customer.getOrderFrequency();

        if (personalHistory.isEmpty()) {
            System.out.println("No past orders found.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Your Past Orders:");

        List<Map.Entry<String, List<food_item>>> pastOrders = new ArrayList<>(personalHistory.entrySet());

        for (int i = 0; i < pastOrders.size(); i++) {
            Map.Entry<String, List<food_item>> entry = pastOrders.get(i);
            String orderDateTime = entry.getKey();
            List<food_item> items = entry.getValue();

            // Use a Set to keep track of unique items in this order
            Set<String> processedItems = new HashSet<>();

            System.out.println("\n" + (i + 1) + ". Order from: " + orderDateTime);
            System.out.println("   Items:");

            for (food_item item : items) {

                if (!processedItems.contains(item.getName())) {
                    processedItems.add(item.getName());

                    List<Integer> quantities = orderFrequency.get(item.getName());
                    int totalOrders = quantities != null ? quantities.size() : 0;
                    int totalQuantity = quantities != null ?
                            quantities.stream().mapToInt(Integer::intValue).sum() : 0;

                    System.out.println("   - " + item.getName() +
                            " ($" + item.get_price() +
                            ") - Ordered " + totalOrders + " times" +
                            " (Total quantity: " + totalQuantity + ")");

                    // Show quantity history for this item
                    if (quantities != null && !quantities.isEmpty()) {
                        System.out.println("     Previous order quantities: " +
                                quantities.toString());
                    }
                }
            }
        }

        System.out.print("\nEnter the number of the order you want to reorder (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice > 0 && choice <= pastOrders.size()) {
            List<food_item> selectedItems = pastOrders.get(choice - 1).getValue();

            // Clear existing cart
            cart.clear();

            // Try to add each item from the selected order
            for (food_item historicalItem : selectedItems) {
                food_item currentMenuItem = menumng.getItemByName(historicalItem.getName());

                if (currentMenuItem != null && currentMenuItem.is_available()) {
                    // Show previous quantities for this item
                    List<Integer> previousQuantities =
                            orderFrequency.get(historicalItem.getName());
                    if (previousQuantities != null && !previousQuantities.isEmpty()) {
                        System.out.println("Previous quantities for " +
                                historicalItem.getName() + ": " +
                                previousQuantities);
                    }

                    System.out.print("Enter quantity for " + currentMenuItem.getName() +
                            " (suggested: " +
                            previousQuantities.get(previousQuantities.size()-1) +
                            "): ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    cart.addItem(currentMenuItem, quantity);
                    System.out.println("Added to cart: " + currentMenuItem.getName() +
                            " - Quantity: " + quantity);
                } else {
                    System.out.println("Item currently unavailable: " +
                            historicalItem.getName());
                }
            }

            if (cart.getItems().isEmpty()) {
                System.out.println("\nCouldn't add any items to cart. All items from " +
                        "this order are currently unavailable.");
                return;
            }

            System.out.println("\nCurrent Cart Contents:");
            viewCart();

            System.out.print("\nWould you like to place this order? (yes/no): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("yes")) {
                placeOrder();
            } else {
                System.out.println("Order cancelled.");
                cart.clear();
            }
        } else if (choice != 0) {
            System.out.println("Invalid order number.");
        }
    }
    private void removeItemFromCart(Scanner scanner){
        System.out.print("Enter item name to remove: ");
        String itemName =scanner.nextLine();
        food_item item = menumng.getItemByName(itemName);

        if(item != null && cart.getItems().containsKey(item)){
            cart.removeItem(item);
            System.out.println("Item removed from cart.");
            viewCart();
        }
        else{
            System.out.println("Item not found in cart.");
        }
    }

    private void addItemToCart(Scanner scanner) {
        System.out.print("Enter item name to add to cart: ");
        String itemName = scanner.nextLine();
        food_item item = menumng.getItemByName(itemName);
        if (item != null && item.is_available()) {
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            cart.addItem(item, quantity);
            System.out.println("Added to cart.");
        } else {
            System.out.println("Item unavailable or not found.");
        }
    }


    private void searchItems(Scanner scanner) {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine();
        List<food_item> results = menumng.searchItems(keyword);
        if (results.isEmpty()) {
            System.out.println("No items found.");
        }
        else {
            results.forEach(item -> System.out.println(item.getName() + " - $" + item.get_price()));
        }
    }

    private void filterByCategory(Scanner scanner) {
        System.out.print("Enter category to filter by: ");
        String category = scanner.nextLine();
        List<food_item> filteredItems = menumng.getItemsByCategory(category);
        if (filteredItems.isEmpty()) {
            System.out.println("No items found in this category.");
        } else {
            filteredItems.forEach(item -> System.out.println(item.getName() + " - $" + item.get_price()));
        }
    }

    private void sortByPrice(Scanner scanner) {
        System.out.print("Sort by price (1: Ascending, 2: Descending): ");
        int order = scanner.nextInt();
        boolean ascending = (order == 1);
        List<food_item> sortedItems = menumng.sortByPrice(ascending);
        sortedItems.forEach(item -> System.out.println(item.getName() + " - $" + item.get_price()));
    }

    public void viewCart() {
        System.out.println("Your Cart:");
        for (Map.Entry<food_item, Integer> entry : cart.getItems().entrySet()) {
            System.out.println(entry.getKey().getName() + " - Quantity: " + entry.getValue() + " - Total: $" + (entry.getKey().get_price() * entry.getValue()));
        }
        System.out.println("Total Cost: $" + cart.getTotalCost());
    }

    public void placeOrder() {
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        if (!cart.validateOrder()) {
            System.out.println(cart.getOrderValidationError());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        order order = new order(customer);

        // Collect additional checkout information
        System.out.println("Checkout Process:");

        // Contact Information
        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.nextLine();

        // Shipping Address
        System.out.print("Enter Shipping Address (Street, City, State, ZIP): ");
        String shippingAddress = scanner.nextLine();

        // Payment Mode
        System.out.println("Select Payment Mode:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. PayPal");
        System.out.println("4. Cash on Delivery");
        System.out.print("Choose payment mode (1-4): ");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String paymentMode;
        switch(paymentChoice){
            case 1: paymentMode = "Credit Card"; break;
            case 2: paymentMode = "Debit Card"; break;
            case 3: paymentMode = "PayPal"; break;
            case 4: paymentMode = "Cash on Delivery"; break;
            default: paymentMode = "Not Specified";
        }

        // Optional: Collect payment details if needed
        String paymentDetails = null;
        if (paymentChoice == 1 || paymentChoice == 2) {
            System.out.print("Enter Card Last 4 Digits (for reference): ");
            paymentDetails = scanner.nextLine();
        }

        // Set additional order details
        order.setContactNumber(contactNumber);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMode(paymentMode);
        order.setPaymentDetails(paymentDetails);

        System.out.println("Order ID: " + order.getOrderId());
        order.setItems(cart.getItems());
        //cart.setDeep_copy(cart.getItems());
        order.calculateTotalCost();


        System.out.print("Any special requests? ");
        String specialRequest = scanner.nextLine();
        order.setSpecialRequest(specialRequest);

        // Place the order through order manager
        ordermng.placeOrder(order);


        // Create lists for unique items and their quantities
        List<food_item> uniqueItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        // Fill the lists from cart items
        for (Map.Entry<food_item, Integer> entry : cart.getItems().entrySet()) {
            uniqueItems.add(entry.getKey());
            quantities.add(entry.getValue());
        }

        // Add order using the new method
        customer.addOrder(order, uniqueItems, quantities);
       /// ordermng.placeOrder();

        // Create and append order record to file
        // Create and append order record to file
        try {
            order_hist_writer.appendOrderToHistory(
                    order,
                    uniqueItems,
                    quantities,
                    "C:\\Users\\saksham mishra\\Saved Games\\ByteThis\\src\\Order_history.txt"
            );
        } catch (Exception e) {
            System.err.println("Error writing to order history: " + e.getMessage());
        }
        cust_hist_mg historyManager = new cust_hist_mg();
        historyManager.updateHistory(customer, order, uniqueItems, quantities);

        System.out.println("Order placed successfully!");

        // Print simple order summary
        System.out.println("\nOrder Summary:");
        for (int i = 0; i < uniqueItems.size(); i++) {
            System.out.println(uniqueItems.get(i).getName() + " - Quantity: " + quantities.get(i));
        }


        cart.clear();
    }


    public void addReview() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the item name to review: ");
        String itemName = scanner.nextLine();
        food_item item = menumng.getItemByName(itemName);

        if (item != null) {
            System.out.print("Enter your name: ");
            String reviewerName = scanner.nextLine();
            System.out.print("Enter your rating (1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter your comment: ");
            String comment = scanner.nextLine();

            review review = new review(reviewerName, rating, comment);
            item.add_review(review);
            System.out.println("Review added successfully.");
        } else {
            System.out.println("Item not found.");
        }
    }
    public void viewReviews() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the item name to view reviews: ");
        String itemName = scanner.nextLine();
        food_item item = menumng.getItemByName(itemName);

        if (item != null) {
            List<review> reviews = item.get_review();
            if (reviews.isEmpty()) {
                System.out.println("No reviews for this item.");
            } else {
                System.out.println("Reviews for " + item.getName() + ":");
                reviews.forEach(System.out::println);
            }
        } else {
            System.out.println("Item not found.");
        }
    }
    private order findOrderById(int orderId) {
        List<order> customerOrders = ordermng.getCustomerOrders(customer);
        for (order o : customerOrders) {
            if (o.getOrderId() == orderId) {
                return o;
            }
        }
        return null;
    }


}






