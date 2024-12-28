import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class cart {
    private Map<food_item, Integer> items;
    private static Map<food_item, Integer> deep_copy;
    private double totalCost;
    private static final String TEMP_CART_FILE = "src\\Cart_db.txt";
    private static boolean isSessionActive = false;
    private ScheduledExecutorService scheduler;
    private static final int UPDATE_INTERVAL_MS = 100; // 100ms update interval

    public cart() {
        this.items = new HashMap<>();
        this.totalCost = 0.0;
        deep_copy = new HashMap<>();
        startSession();
        startAutoSave();
    }

    private void startSession() {
        isSessionActive = true;
    }
    public boolean validateOrder() {
        for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
            if (!entry.getKey().is_available()) {
                return false;
            }
        }
        return true;
    }

    public String getOrderValidationError() {
        List<String> unavailableItems = new ArrayList<>();
        for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
            if (!entry.getKey().is_available()) {
                unavailableItems.add(entry.getKey().getName());
            }
        }

        if (!unavailableItems.isEmpty()) {
            return "Cannot place order. The following items are currently unavailable: " +
                    String.join(", ", unavailableItems);
        }
        return null;
    }

    private void startAutoSave() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (isSessionActive) {
                saveCartToFile();
            }
        }, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void endSession() {
        isSessionActive = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private synchronized void saveCartToFile() {
        if (!isSessionActive) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(TEMP_CART_FILE))) {
            // Write header
            writer.println("Current Cart Contents");
            writer.println("--------------------");

            // Write items
            for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
                food_item item = entry.getKey();
                int quantity = entry.getValue();
                writer.printf("Item: %s | Price: ₹%.2f | Quantity: %d | Subtotal: ₹%.2f%n",
                        item.getName(),
                        item.get_price(),
                        quantity,
                        item.get_price() * quantity);
            }

            // Write total
            writer.println("--------------------");
            writer.printf("Total Cost: ₹%.2f%n", totalCost);
            writer.println("--------------------");
            writer.printf("Last Updated: %s%n", java.time.LocalDateTime.now());

        } catch (IOException e) {
            System.err.println("Error saving cart to file: " + e.getMessage());
        }
    }

    private synchronized void loadCartFromFile() {
        if (!isSessionActive) return;

        File tempFile = new File(TEMP_CART_FILE);
        if (!tempFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_CART_FILE))) {
            String line;
            reader.readLine(); // Skip "Current Cart Contents"
            reader.readLine(); // Skip "--------------------"

            while ((line = reader.readLine()) != null && !line.startsWith("----")) {
                // Parse item information if needed
            }
        } catch (IOException e) {
            System.err.println("Error loading cart from file: " + e.getMessage());
        }
    }

    public Map<food_item, Integer> getItems() {
        return items;
    }

    public static Map<food_item, Integer> getDeep_copy() {
        return deep_copy;
    }

    public void setDeep_copy(Map<food_item, Integer> items) {
        if (!deep_copy.isEmpty()) {
            deep_copy.clear();
        }
        deep_copy = items;
    }

    public synchronized void addItem(food_item item, int quantity) {
        if (items.containsKey(item)) {
            int currentQuantity = items.get(item);
            items.put(item, currentQuantity + quantity);
        } else {
            items.put(item, quantity);
        }
        recalculateTotalCost();
    }

    public synchronized void removeItem(food_item item) {
        items.remove(item);
        recalculateTotalCost();
    }

    public synchronized void updateItemQuantity(food_item item, int newQuantity) {
        if (newQuantity > 0) {
            items.put(item, newQuantity);
        } else {
            items.remove(item);
        }
        recalculateTotalCost();
    }

    private synchronized void recalculateTotalCost() {
        totalCost = 0.0;
        for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
            totalCost += entry.getKey().get_price() * entry.getValue();
        }
    }

    public double getTotalCost() {
        return this.totalCost;
    }

    public synchronized void clear() {
        items.clear();
        totalCost = 0.0;
    }

    public void debugCartContents() {
        System.out.println("DEBUG: Cart Contents");
        System.out.println("Total items: " + items.size());
        for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
            System.out.println("Item: " + entry.getKey().getName()
                    + ", Price: " + entry.getKey().get_price()
                    + ", Quantity: " + entry.getValue());
        }
        System.out.println("Total Cart Cost: " + getTotalCost());
    }
}