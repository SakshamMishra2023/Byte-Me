import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class admin_inf {
    private menu_mng menumng;
    private order_mng order_mng;

    public admin_inf(){
        this.menumng = menu_mng.getInstance();
        this.order_mng = order_mng.getInstance();
    }

    public void manageMenu(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Menu Management:");
        System.out.println("1. Add new item");
        System.out.println("2. Update existing item");
        System.out.println("3. Remove item");
        System.out.println("4. View all items");
        System.out.println("5. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch(choice){
            case 1:
                System.out.print("Enter item name: ");
                String name = scanner.nextLine();
                System.out.print("Enter item price: ");
                int price = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter item category: ");
                String category = scanner.nextLine();
                System.out.print("Is the item available? (true/false): ");
                boolean available = scanner.nextBoolean();
                scanner.nextLine();
                food_item newItem = new food_item(name, price, category, available);
                menumng.addItem(newItem);
                System.out.println("Item added successfully.");
                break;
            case 2:
                System.out.print("Enter the name of the item to update: ");
                String itemToUpdate = scanner.nextLine();
                food_item item = menumng.getItemByName(itemToUpdate);
                if(item != null){
                    System.out.print("Enter the new price: ");
                    int newPrice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Is the item available? (true/false): ");
                    boolean newAvailable = scanner.nextBoolean();
                    scanner.nextLine();
                    item.set_pride(newPrice);
                    item.set_available(newAvailable);
                    menumng.updateItem(itemToUpdate, item);
                    System.out.println("Item updated successfully.");
                } else{
                    System.out.println("Item not found.");
                }
                break;
            case 3:
                System.out.print("Enter the name of the item to remove: ");
                String itemToRemove = scanner.nextLine();
                menumng.removeItem(itemToRemove);
                System.out.println("Item removed successfully.");
                break;
            case 4:
                List<food_item> allItems = menumng.getAllItems();
                System.out.println("All Menu Items:");
                for(food_item i : allItems){
                    System.out.println(i.getName() + " - $" + i.get_price() + " - " + i.get_category() + " - " + (i.is_available() ? "Available" : "Unavailable"));
                }
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public void manageOrders(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Manage Orders:");
        System.out.println("1. View Pending Orders");
        System.out.println("2. Update Order Status");
        System.out.println("3. Handle Special Request");
        System.out.println("4. Process Refund");
        System.out.println("5. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if(choice == 1){
            viewPendingOrders();
        }
        else if(choice == 2){
            update_o_status();
        }
        else if(choice == 3){
            handleSpecialRequest();
        }
        else if(choice == 4){
            processRefund();
        }
        else if(choice == 5){
            return;
        }
        else{
            System.out.println("Invalid choice. Please try again.");
        }
    }

    private void viewPendingOrders(){
        List<order> pendingOrders = order_mng.getPendingOrders();
        System.out.println("Pending Orders:");
        for(order order : pendingOrders){
            System.out.println("Order ID: " + order.hashCode() +
                    " | Customer: " + order.getCustomer().getName() +
                    " | Status: " + order.getStatus() +
                    " | Special Request: " + (order.getSpecialRequest() != null ? order.getSpecialRequest() : "None") +
                    " | Special Request Handled: " + (order.isSpecialRequestHandled() ? "Yes" : "No"));
            for(Map.Entry<food_item,Integer> entry : order.getItems().entrySet()){
                System.out.println(entry.getKey().getName() + " - Quantity: " + entry.getValue() + " - Total: $" + (entry.getKey().get_price() * entry.getValue()));
            }
            if(order.getItems().isEmpty()){
                System.out.println("this shits empty");
            }
        }
    }

    private void update_o_status(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the order ID to update status: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        order order = findOrderById(orderId);

        if(order != null){
            System.out.println("DEBUG: Order found with ID " + orderId);
            System.out.println("Current status: " + order.getStatus());
            System.out.print("Enter the new status (PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELED): ");
            String newStatus = scanner.nextLine();
            order.setStatus(order_stat.valueOf(newStatus));

            if(newStatus.equalsIgnoreCase("DELIVERED")){
                // Detailed debugging of total cost calculation
                System.out.println("DEBUG: Order Items:");
                for (Map.Entry<food_item, Integer> entry : order.getItems().entrySet()) {
                    System.out.println("Item: " + entry.getKey().getName()
                            + ", Price: " + entry.getKey().get_price()
                            + ", Quantity: " + entry.getValue());
                }

                double totalCost = order.getTotalCost();
                int total_c = (int) totalCost;

               // System.out.println("DEBUG: Calculated Total Cost (double): " + totalCost);
                //System.out.println("DEBUG: Calculated Total Cost (int): " + total_c);

                order_mng.addSale(totalCost);
                //System.out.println("Total sales updated: $" + total_c);
            }
            System.out.println("Order status updated successfully.");
        }
        else{
            System.out.println("Order not found.");
        }
    }

    private void handleSpecialRequest(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Order ID to handle special request: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        order order = findOrderById(orderId);

        if(order != null){
            System.out.println("Special Request: " + (order.getSpecialRequest() != null ? order.getSpecialRequest() : "None"));
            System.out.print("Has the special request been handled? (yes/no): ");
            String response = scanner.nextLine();
            boolean handled = response.equalsIgnoreCase("yes");
            order.setSpecialRequestHandled(handled);
            System.out.println("Special request status updated for Order ID " + orderId);
        }
        else{
            System.out.println("Order ID not found.");
        }
    }

    private void processRefund(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Order ID to process refund: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        order order = findOrderById(orderId);
        if(order != null){
            order_mng.processRefund(order);
        }
        else{
            System.out.println("Order not found.");
        }
    }

    public void generateDailySalesReport(){
        daily_sales_report report = order_mng.generateDailySalesReport();
        System.out.println("Daily Sales Report:");
        System.out.println("Total Sales: $" + report.getTotal_sales());
        System.out.println("Total Orders: " + report.getTotal_order());
        System.out.println("Most Popular Items:");

        for(Map.Entry<food_item, Integer> entry : report.getPopular_items().entrySet()){
            System.out.println(entry.getKey().getName() + " - " + entry.getValue() + " orders");
        }
    }

    private order findOrderById(int orderId) {
        // First, search in order history
        for (List<order> orders : order_mng.getOrderHistory().values()) {
            for (order o : orders) {
                if (o.getOrderId() == orderId) {
                    return o;
                }
            }
        }

        // If not found in history, add a method in order_mng to get current orders
        List<order> currentOrders = order_mng.getCurrentOrders();
        for (order o : currentOrders) {
            if (o.getOrderId() == orderId) {
                return o;
            }
        }

        return null;
    }
}