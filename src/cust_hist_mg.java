import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class cust_hist_mg{
    private static final String HISTORY_DIR = "src";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public cust_hist_mg(){
        File directory = new File(HISTORY_DIR);
        if(!directory.exists()){
            directory.mkdirs();
        }
    }

    public void saveCustomerHistory(customer customer) {
        String fileName = HISTORY_DIR + "\\" + sanitizeEmail(customer.getEmail()) + "_history.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Customer Information:");
            writer.println("Email: " + customer.getEmail());
            writer.println("Name: " + customer.getName());
            writer.println("VIP Status: " + (customer.isVIP() ? "Yes" : "No"));
            writer.println("\n-------------------\n");

            writer.println("Order History (Most Recent First):");
            for (Map.Entry<LocalDateTime, List<order>> entry : customer.getOrderHistory().entrySet()) {
                LocalDateTime orderDateTime = entry.getKey();
                List<order> orders = entry.getValue();

                writer.println("\nDate: " + orderDateTime.format(DATE_FORMAT));
                for (order ord : orders) {
                    writer.println("Order ID: " + ord.getOrderId());
                    writer.println("Status: " + ord.getStatus());
                    writer.println("Items:");
                    for (Map.Entry<food_item, Integer> itemEntry : ord.getItems().entrySet()) {
                        writer.println("  - " + itemEntry.getKey().getName() +
                                " x" + itemEntry.getValue() +
                                " (₹" + itemEntry.getKey().get_price() + " each)");
                    }
                    writer.println("Total Cost: ₹" + ord.getTotalCost());
                    if (ord.getSpecialRequest() != null) {
                        writer.println("Special Request: " + ord.getSpecialRequest());
                    }
                }
            }
            writer.println("\n-------------------\n");

            writer.println("Personal Menu History:");
            for (Map.Entry<String, List<food_item>> entry : customer.getPersonalMenuHistory().entrySet()) {
                writer.println("\nOrder Date: " + entry.getKey());
                writer.println("Unique Items Ordered:");
                for (food_item item : entry.getValue()) {
                    writer.println("  - " + item.getName() + " (₹" + item.get_price() + ")");
                }
            }
            writer.println("\n-------------------\n");

            /* Write order frequency data
            writer.println("Order Frequency Statistics:");
            for (Map.Entry<String, List<Integer>> entry : customer.getOrderFrequency().entrySet()) {
                String itemName = entry.getKey();
                List<Integer> quantities = entry.getValue();
                int totalOrders = quantities.size();
                int totalQuantity = quantities.stream().mapToInt(Integer::intValue).sum();
                double avgQuantity = totalQuantity / (double) totalOrders;

                writer.println("\nItem: " + itemName);
                writer.println("  Times Ordered: " + totalOrders);
                writer.println("  Total Quantity: " + totalQuantity);
                writer.println("  Average Quantity per Order: " + String.format("%.2f", avgQuantity));
                writer.println("  Order Quantities: " + quantities);
            }*/

        }
        catch (IOException e) {
            System.err.println("Error saving customer history: " + e.getMessage());
            e.printStackTrace();
        }

        saveBinaryHistory(customer);
    }

    private void saveBinaryHistory(customer customer) {
        String fileName = HISTORY_DIR + "\\" + sanitizeEmail(customer.getEmail()) + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("name", customer.getName());
            customerData.put("email", customer.getEmail());
            customerData.put("isVIP", customer.isVIP());
            customerData.put("orderHistory", customer.getOrderHistory());
            customerData.put("personalMenuHistory", customer.getPersonalMenuHistory());
            customerData.put("orderFrequency", customer.getOrderFrequency());
            oos.writeObject(customerData);
        } catch (IOException e) {
            System.err.println("Error saving binary customer history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCustomerHistory(customer customer) {
        String fileName = HISTORY_DIR + "\\" + sanitizeEmail(customer.getEmail()) + ".dat";
        File historyFile = new File(fileName);

        if(!historyFile.exists()){
            System.out.println("No previous history found for " + customer.getEmail());
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Map<String, Object> customerData = (Map<String, Object>) ois.readObject();


            Map<LocalDateTime, List<order>> orderHistory =
                    (Map<LocalDateTime, List<order>>) customerData.get("orderHistory");
            orderHistory.forEach((key, value) ->
                    customer.getOrderHistory().put(key, value));

            Map<String, List<food_item>> personalMenuHistory =
                    (Map<String, List<food_item>>) customerData.get("personalMenuHistory");
            personalMenuHistory.forEach((key, value) ->
                    customer.getPersonalMenuHistory().put(key, value));

            Map<String, List<Integer>> orderFrequency =
                    (Map<String, List<Integer>>) customerData.get("orderFrequency");
            orderFrequency.forEach((key, value) ->
                    customer.getOrderFrequency().put(key, value));

            System.out.println("Successfully loaded history for " + customer.getEmail());
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading customer history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String sanitizeEmail(String email) {
        return email.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public void updateHistory(customer customer, order order, List<food_item> uniqueItems, List<Integer> quantities) {
        customer.addOrder(order, uniqueItems, quantities);
        saveCustomerHistory(customer);
    }

    public boolean hasHistory(String email) {
        String datFile = HISTORY_DIR + "\\" + sanitizeEmail(email) + ".dat";
        String txtFile = HISTORY_DIR + "\\" + sanitizeEmail(email) + "_history.txt";
        return new File(datFile).exists() || new File(txtFile).exists();
    }
}