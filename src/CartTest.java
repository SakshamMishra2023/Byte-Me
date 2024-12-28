import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CartTest{
    private cart shoppingCart;
    private food_item testItem1;
    private food_item testItem2;

    @Before
    public void setUp(){

        shoppingCart = new cart();

        testItem1 = new food_item("Pizza", 1099, "Main Course", true);
        testItem2 = new food_item("Burger", 899, "Main Course", true);
    }

    @Test
    public void testAddItemToCart(){
        shoppingCart.addItem(testItem1, 1);
        assertEquals("Cart should contain 1 item", 1, shoppingCart.getItems().size());
        assertEquals("Total cost should be 1099", 1099.0, shoppingCart.getTotalCost(), 0.01);

        shoppingCart.addItem(testItem1, 2);
        assertEquals("Quantity should be updated to 3",
                Integer.valueOf(3), shoppingCart.getItems().get(testItem1));
        assertEquals("Total cost should be 3297", 3297.0, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testModifyItemQuantity(){
        shoppingCart.addItem(testItem1, 2);

        shoppingCart.updateItemQuantity(testItem1, 4);
        assertEquals("Quantity should be updated to 4",
                Integer.valueOf(4), shoppingCart.getItems().get(testItem1));
        assertEquals("Total cost should be 4396", 4396.0, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testMultipleItems(){

        shoppingCart.addItem(testItem1, 2);
        shoppingCart.addItem(testItem2, 3);

        double expectedTotal = (2 * 1099) + (3 * 899);
        assertEquals("Total cost should be sum of all items",
                expectedTotal, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testNegativeQuantity(){
        shoppingCart.addItem(testItem1, 2);

        shoppingCart.updateItemQuantity(testItem1, -1);

        assertFalse("Item should be removed from cart",
                shoppingCart.getItems().containsKey(testItem1));
        assertEquals("Total cost should be 0", 0.0, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testZeroQuantity() {
        shoppingCart.addItem(testItem1, 2);


        shoppingCart.updateItemQuantity(testItem1, 0);

        assertFalse("Item should be removed from cart",
                shoppingCart.getItems().containsKey(testItem1));
        assertEquals("Total cost should be 0", 0.0, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testClearCart(){

        shoppingCart.addItem(testItem1, 2);
        shoppingCart.addItem(testItem2, 3);

        shoppingCart.clear();

        assertTrue("Cart should be empty", shoppingCart.getItems().isEmpty());
        assertEquals("Total cost should be 0", 0.0, shoppingCart.getTotalCost(), 0.01);
    }

    @Test
    public void testRemoveItem(){
        shoppingCart.addItem(testItem1, 2);
        shoppingCart.addItem(testItem2, 3);

        shoppingCart.removeItem(testItem1);

        assertFalse("Item1 should be removed", shoppingCart.getItems().containsKey(testItem1));
        assertTrue("Item2 should still be present", shoppingCart.getItems().containsKey(testItem2));
        assertEquals("Total cost should be updated", 2697.0, shoppingCart.getTotalCost(), 0.01);
    }
}