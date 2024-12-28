import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class order_avail_tests {
    private customer_interference customerInterface;
    private menu_mng menuManager;
    private customer testCustomer;
    private cart testCart;

    @Before
    public void setUp() {

        testCustomer = new customer("Test Customer", "test@email.com", false);
        customerInterface = new customer_interference(testCustomer);
        menuManager = menu_mng.getInstance();
        testCart = new cart();


        menuManager.getAllItems().clear();


        food_item availableItem = new food_item("Pizza", 15, "Main Course", true);
        food_item unavailableItem = new food_item("Burger", 10, "Main Course", false);
        food_item partiallyAvailableItem = new food_item("Salad", 8, "Appetizer", true);

        menuManager.addItem(availableItem);
        menuManager.addItem(unavailableItem);
        menuManager.addItem(partiallyAvailableItem);
    }

    @Test
    public void testOrderWithAllAvailableItems(){

        food_item pizza = menuManager.getItemByName("Pizza");
        testCart.addItem(pizza, 2);


        assertTrue("Should allow order with all available items", validateOrder(testCart));
        assertNull("No error message should be present", getOrderValidationError(testCart));
    }

    @Test
    public void testOrderWithUnavailableItems() {

        food_item burger = menuManager.getItemByName("Burger");
        testCart.addItem(burger, 1);

        assertFalse("Should not allow order with unavailable items", validateOrder(testCart));
        String errorMsg = getOrderValidationError(testCart);
        assertNotNull("Error message should be present", errorMsg);
        assertTrue("Error message should mention unavailable items",
                errorMsg.contains("Burger is currently unavailable"));
    }

    @Test
    public void testOrderWithMixedAvailability() {

        food_item pizza = menuManager.getItemByName("Pizza");
        food_item burger = menuManager.getItemByName("Burger");

        testCart.addItem(pizza, 1);
        testCart.addItem(burger, 1);

        assertFalse("Should not allow order with any unavailable items", validateOrder(testCart));
        String errorMsg = getOrderValidationError(testCart);
        assertNotNull("Error message should be present", errorMsg);
        assertTrue("Error message should mention unavailable items",
                errorMsg.contains("Burger is currently unavailable"));
    }

    @Test
    public void testOrderWithItemBecomingUnavailable() {

        food_item salad = menuManager.getItemByName("Salad");
        testCart.addItem(salad, 1);


        assertTrue("Should initially allow order", validateOrder(testCart));


        salad.set_available(false);

        assertFalse("Should not allow order after item becomes unavailable", validateOrder(testCart));
        String errorMsg = getOrderValidationError(testCart);
        assertNotNull("Error message should be present", errorMsg);
        assertTrue("Error message should mention unavailable items",
                errorMsg.contains("Salad is currently unavailable"));
    }

    @Test
    public void testEmptyCart(){

        assertTrue("Should allow empty cart validation", validateOrder(testCart));
        assertNull("No error message should be present for empty cart", getOrderValidationError(testCart));
    }


    private boolean validateOrder(cart cart){
        for(Map.Entry<food_item, Integer> entry : cart.getItems().entrySet()){
            if(!entry.getKey().is_available()){
                return false;
            }
        }
        return true;
    }

    private String getOrderValidationError(cart cart){
        List<String> errorMessages = new ArrayList<>();
        for(Map.Entry<food_item, Integer> entry : cart.getItems().entrySet()){
            if(!entry.getKey().is_available()){
                errorMessages.add(entry.getKey().getName() + " is currently unavailable");
            }
        }

        if(!errorMessages.isEmpty()){
            return String.join(", ", errorMessages);
        }
        return null;
    }
}