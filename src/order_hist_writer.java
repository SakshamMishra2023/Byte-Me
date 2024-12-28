import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class order_hist_writer{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void appendOrderToHistory(order order, List<food_item> items, List<Integer> quantities, String filePath) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
            writer.write("--- ORDER START ---\n");
            writer.write("OrderID: " + order.getOrderId() + "\n");
            writer.write("DateTime: " + order.getOrderDateTime().format(formatter) + "\n");
            writer.write("CustomerEmail: " + order.getCustomer().getEmail() + "\n");
            writer.write("CustomerName: " + order.getCustomer().getName() + "\n");
            writer.write("Status: " + order.getStatus() + "\n");
            writer.write("TotalCost: " + order.getTotalCost() + "\n");
            writer.write("ContactNumber: " + order.getContactNumber() + "\n");
            writer.write("ShippingAddress: " + order.getShippingAddress() + "\n");
            writer.write("PaymentMode: " + order.getPaymentMode() + "\n");

            writer.write("Items:\n");
            for(int i = 0; i < items.size(); i++){
                food_item item = items.get(i);
                writer.write(String.format("%s|%s|%.2f|%d\n",
                        item.getName(),
                        item.get_category(),
                        item.get_price(),
                        quantities.get(i)
                ));
            }
            writer.write("--- ORDER END ---\n\n");
        }
    }

    public static void loadOrderHistory(customer customer, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            order currentOrder = null;
            List<food_item> items = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("--- ORDER START ---")) {
                    currentOrder = new order(customer);
                    items.clear();
                    quantities.clear();
                    continue;
                }

                if (line.equals("--- ORDER END ---")) {
                    if (currentOrder != null) {
                        customer.addOrder(currentOrder, items, quantities);
                    }
                    continue;
                }

                if (currentOrder != null) {
                    if (line.startsWith("OrderID: ")) {

                    }
                    else if (line.startsWith("DateTime: ")) {
                        String dateTimeStr = line.substring("DateTime: ".length());
                        currentOrder.setOrderDateTime(LocalDateTime.parse(dateTimeStr, formatter));
                    }
                    else if (line.startsWith("Status: ")) {
                        String statusStr = line.substring("Status: ".length());
                        currentOrder.setStatus(order_stat.valueOf(statusStr));
                    }
                    else if (line.startsWith("TotalCost: ")) {
                        String costStr = line.substring("TotalCost: ".length());
                        currentOrder.setTotalCost(Double.parseDouble(costStr));
                    }
                    else if (line.startsWith("ContactNumber: ")) {
                        String contact = line.substring("ContactNumber: ".length());
                        currentOrder.setContactNumber(contact);
                    }
                    else if (line.startsWith("ShippingAddress: ")) {
                        String address = line.substring("ShippingAddress: ".length());
                        currentOrder.setShippingAddress(address);
                    }
                    else if (line.startsWith("PaymentMode: ")) {
                        String paymentMode = line.substring("PaymentMode: ".length());
                        currentOrder.setPaymentMode(paymentMode);
                    }
                    else if (!line.equals("Items:") && !line.trim().isEmpty()) {

                        String[] itemData = line.split("\\|");
                        if (itemData.length == 4) {
                            food_item item = new food_item(
                                    itemData[0],
                                    (int) Double.parseDouble(itemData[2]), // price
                                    itemData[1],
                                    true
                            );
                            items.add(item);
                            quantities.add(Integer.parseInt(itemData[3]));
                        }
                    }
                }
            }
        }
    }

    public static Map<String, customer> loadAllCustomerHistories(String filePath) throws IOException {
        Map<String, customer> customers = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            customer currentCustomer = null;
            order currentOrder = null;
            List<food_item> items = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("--- ORDER START ---")) {
                    currentOrder = null;
                    items.clear();
                    quantities.clear();
                    continue;
                }

                if (line.startsWith("CustomerEmail: ")) {
                    String email = line.substring("CustomerEmail: ".length());
                    if (!customers.containsKey(email)) {

                        String nameLine = reader.readLine();
                        String name = nameLine.substring("CustomerName: ".length());
                        currentCustomer = new customer(name, email, false);
                        customers.put(email, currentCustomer);
                    }
                    else {
                        currentCustomer = customers.get(email);
                        reader.readLine();
                    }
                    currentOrder = new order(currentCustomer);
                    continue;
                }

                if(currentOrder != null && currentCustomer != null) {
                    if (line.equals("--- ORDER END ---")) {
                        Map<food_item, Integer> orderItems = new HashMap<>();
                        for (int i = 0; i < items.size(); i++) {
                            orderItems.put(items.get(i), quantities.get(i));
                        }
                        currentOrder.setItems(orderItems);
                        currentCustomer.addOrder(currentOrder, items, quantities);
                    }
                    else if (line.startsWith("DateTime: ")) {
                        String dateTimeStr = line.substring("DateTime: ".length());
                        currentOrder.setOrderDateTime(LocalDateTime.parse(dateTimeStr, formatter));
                    }
                    else if (line.startsWith("Status: ")) {
                        String statusStr = line.substring("Status: ".length());
                        currentOrder.setStatus(order_stat.valueOf(statusStr));
                    }
                    else if (line.startsWith("TotalCost: ")) {
                        String costStr = line.substring("TotalCost: ".length());
                        currentOrder.setTotalCost(Double.parseDouble(costStr));
                    }
                    else if (line.startsWith("ContactNumber: ")) {
                        String contact = line.substring("ContactNumber: ".length());
                        currentOrder.setContactNumber(contact);
                    } else if (line.startsWith("ShippingAddress: ")) {
                        String address = line.substring("ShippingAddress: ".length());
                        currentOrder.setShippingAddress(address);
                    }
                    else if (line.startsWith("PaymentMode: ")) {
                        String paymentMode = line.substring("PaymentMode: ".length());
                        currentOrder.setPaymentMode(paymentMode);
                    }
                    else if (!line.equals("Items:") && !line.trim().isEmpty() && line.contains("|")) {

                        String[] itemData = line.split("\\|");
                        if (itemData.length == 4) {
                            food_item item = new food_item(
                                    itemData[0],
                                    (int) Double.parseDouble(itemData[2]), // price
                                    itemData[1],
                                    true
                            );
                            items.add(item);
                            quantities.add(Integer.parseInt(itemData[3]));
                        }
                    }
                }
            }
        }
        return customers;
    }
}