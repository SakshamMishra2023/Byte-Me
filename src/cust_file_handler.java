import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class cust_file_handler {
    private static final String CUSTOMER_FILE = "src\\customers_db.txt";
    private Set<String> existingEmails;

    public cust_file_handler(){
        existingEmails = loadExistingEmails();
    }

    private Set<String> loadExistingEmails(){
        Set<String> emails = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    emails.add(parts[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, will be created when first customer is added
            return new HashSet<>();
        } catch (IOException e) {
            System.err.println("Error reading customer file: " + e.getMessage());
        }
        return emails;
    }

    public void saveCustomer(String name, String email, boolean isVIP) {
        if (!existingEmails.contains(email)) {
            try (FileWriter writer = new FileWriter(CUSTOMER_FILE, true)) {
                // Format: Name|Email|VIP_Status
                writer.write(String.format("%s|%s|%s%n", name, email, isVIP ? "VIP" : "Regular"));
                existingEmails.add(email);
            } catch (IOException e) {
                System.err.println("Error writing to customer file: " + e.getMessage());
            }
        }
    }

    public boolean isEmailRegistered(String email) {
        return existingEmails.contains(email);
    }
}