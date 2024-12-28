import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class customer implements Serializable {
    private String name;
    private String email;
    private boolean isVIP;
    private Map<LocalDateTime, List<order>> orderHistory;
    private Map<String, List<food_item>> personalMenuHistory;
    private Map<String, List<Integer>> orderFrequency;

    public customer(String name, String email, boolean isVIP) {
        this.name = name;
        this.email = email;
        this.isVIP = isVIP;
        this.orderHistory = new TreeMap<>(Comparator.reverseOrder());
        this.personalMenuHistory = new HashMap<>();
        this.orderFrequency = new HashMap<>();
    }

    // Getters remain the same
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isVIP() { return isVIP; }
    public Map<LocalDateTime, List<order>> getOrderHistory() { return orderHistory; }
    public Map<String, List<food_item>> getPersonalMenuHistory() { return personalMenuHistory; }
    public Map<String, List<Integer>> getOrderFrequency() { return orderFrequency; }

    // Combined method for adding orders
    public void addOrder(order order, List<food_item> uniqueItems, List<Integer> quantities) {
        LocalDateTime orderDateTime = order.getOrderDateTime();

        // Check if we already have this order ID in any existing lists
        boolean orderExists = orderHistory.values().stream()
                .flatMap(List::stream)
                .anyMatch(existingOrder -> existingOrder.getOrderId() == order.getOrderId());

        if (!orderExists) {
            // Add to order history
            orderHistory.computeIfAbsent(orderDateTime, k -> new ArrayList<>()).add(order);

            // Add to personal menu history
            String orderKey = orderDateTime.toString();
            personalMenuHistory.put(orderKey, uniqueItems);

            // Update order frequency
            for (int i = 0; i < uniqueItems.size(); i++) {
                String itemName = uniqueItems.get(i).getName();
                orderFrequency.computeIfAbsent(itemName, k -> new ArrayList<>()).add(quantities.get(i));
            }
        }
    }

    // For simple order additions without menu tracking
    public void addOrdermenu(order order) {
        LocalDateTime orderDateTime = order.getOrderDateTime();

        // Check if we already have this order ID
        boolean orderExists = orderHistory.values().stream()
                .flatMap(List::stream)
                .anyMatch(existingOrder -> existingOrder.getOrderId() == order.getOrderId());

        if (!orderExists) {
            orderHistory.computeIfAbsent(orderDateTime, k -> new ArrayList<>()).add(order);
        }
    }

    public ArrayList<List<food_item>> getAllOrderedItems() {
        return new ArrayList<>(personalMenuHistory.values());
    }

    public boolean hasOrderedItem(String itemName) {
        return personalMenuHistory.values().stream()
                .flatMap(List::stream)
                .anyMatch(item -> item.getName().equals(itemName));
    }
}