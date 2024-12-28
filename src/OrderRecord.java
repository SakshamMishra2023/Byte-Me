import java.io.*;
import java.util.*;

class OrderRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private order order;
    private List<food_item> uniqueItems;
    private List<Integer> quantities;

    public OrderRecord(order order, List<food_item> uniqueItems, List<Integer> quantities) {
        this.order = order;
        this.uniqueItems = uniqueItems;
        this.quantities = quantities;
    }
}
