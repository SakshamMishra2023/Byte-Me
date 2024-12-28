import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class menu_mng {
    private static menu_mng instance;
    private Map<String, food_item> menu;
    private static final String MENU_FILE = "src\\Menu_items.txt";

    private menu_mng() {
        this.menu = new TreeMap<>();
        loadMenuFromFile();
    }

    public static menu_mng getInstance() {
        if(instance == null) {
            instance = new menu_mng();
        }
        return instance;
    }

    public void saveMenuToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MENU_FILE))) {
            System.out.println("Saving " + menu.size() + " menu items to file");

            // Write header
            writer.write("Name|Category|Price\n");

            // Write each menu item
            for (food_item item : menu.values()) {
                String line = String.format("%s|%s|%d\n",
                        item.getName(),
                        item.get_category(),
                        (int)item.get_price()
                );
                writer.write(line);
            }

            System.out.println("Menu items saved successfully to " + MENU_FILE);
        } catch (IOException e) {
            System.err.println("Error saving menu items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMenuFromFile() {
        File file = new File(MENU_FILE);
        if (!file.exists()) {
            System.out.println("No existing menu file found. Starting with empty menu.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            System.out.println("Attempting to load menu items from " + MENU_FILE);

            // Skip header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("Empty menu file found.");
                return;
            }

            String line;
            menu.clear();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        food_item item = new food_item(
                                parts[0].trim(),                    // name
                                Integer.parseInt(parts[2].trim()),  // price
                                parts[1].trim(),                    // category
                                true                                // availability (default to true)
                        );
                        menu.put(item.getName(), item);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }

            System.out.println("Successfully loaded " + menu.size() + " items from file");
        } catch (IOException e) {
            System.err.println("Error reading menu file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Add this helper method to menu_mng class

    // Add this method to menu_mng class



    public void addItem(food_item item) {
        menu.put(item.getName(), item);
        // Save immediately after adding an item
        saveMenuToFile();
    }

    public void updateItem(String name, food_item updatedItem) {
        menu.put(name, updatedItem);
        // Save immediately after updating an item
        saveMenuToFile();
    }

    public void removeItem(String name) {
        food_item item = menu.get(name);
        if (item != null) {
            menu.remove(name);
            order_mng.getInstance().updateOrderStatusForRemovedItem(item);
            // Save immediately after removing an item
            saveMenuToFile();
        }
    }


    public List<food_item> getAllItems() {
        return new ArrayList<>(menu.values());
    }

    public List<food_item> getItemsByCategory(String category) {
        return menu.values().stream()
                .filter(item -> item.get_category().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public food_item getItemByName(String name) {
        return menu.get(name);
    }

    public List<food_item> searchItems(String keyword) {
        return menu.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<food_item> sortByPrice(boolean ascending) {
        return menu.values().stream()
                .sorted(ascending ? Comparator.comparingDouble(food_item::get_price)
                        : Comparator.comparingDouble(food_item::get_price).reversed())
                .collect(Collectors.toList());
    }

    public List<food_item> getItemsSortedByPrice() {
        return sortByPrice(true);
    }

    public List<food_item> getItemsSortedByPriceDescending() {
        return sortByPrice(false);
    }
}