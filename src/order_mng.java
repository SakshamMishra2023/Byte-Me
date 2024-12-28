import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class order_mng implements Serializable {
    private static order_mng instance;
    private PriorityQueue<order> pendingOrders;

    private Map<customer, List<order>> orderHistory;
    private int total_sales;


    private static final String PENDING_ORDERS_FILE = "src\\pending_orders.txt";

    public void writePendingOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PENDING_ORDERS_FILE))) {

            writer.write("OrderID|CustomerName|Status|Items|TotalCost|SpecialRequest\n");

            for (order order : pendingOrders) {
                StringBuilder itemsStr = new StringBuilder();
                for (Map.Entry<food_item, Integer> entry : order.getItems().entrySet()) {
                    itemsStr.append(entry.getKey().getName())
                            .append("(")
                            .append(entry.getValue())
                            .append("),");
                }

                if (itemsStr.length() > 0) {
                    itemsStr.setLength(itemsStr.length() - 1);
                }

                String line = String.format("%d|%s|%s|%s|%.2f|%s\n",
                        order.getOrderId(),
                        order.getCustomer().getName(),
                        order.getStatus(),
                        itemsStr.toString(),
                        order.getTotalCost(),
                        order.getSpecialRequest() != null ? order.getSpecialRequest() : "None"
                );
                writer.write(line);
            }
            System.out.println("Pending orders saved successfully to " + PENDING_ORDERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving pending orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private order_mng(){
        this.pendingOrders = new PriorityQueue<>((o1, o2) -> {
            boolean o1IsVIP = o1.getCustomer().isVIP();
            boolean o2IsVIP = o2.getCustomer().isVIP();
            if(o1IsVIP && !o2IsVIP){
                return -1;
            }
            else if(!o1IsVIP && o2IsVIP){
                return 1;
            }
            else{
                return o1.getOrderDateTime().compareTo(o2.getOrderDateTime());
            }
        });
        this.orderHistory = new HashMap<>();
        this.total_sales = 0;
    }

    public void addSale(double amount){
        total_sales += amount;
    }

    public static order_mng getInstance(){
        if(instance == null){
            instance = new order_mng();
        }
        return instance;
    }

    public void placeOrder(order order){

            System.out.println("DEBUG: Placing order with ID " + order.getOrderId());
            System.out.println("DEBUG: Order items: " + order.getItems().size());
            for (Map.Entry<food_item, Integer> entry : order.getItems().entrySet()) {
                System.out.println("DEBUG: Item: " + entry.getKey().getName()
                        + ", Price: " + entry.getKey().get_price()
                        + ", Quantity: " + entry.getValue());
            }

        pendingOrders.offer(order);
        customer customer = order.getCustomer();
        if(!orderHistory.containsKey(customer)){
            orderHistory.put(customer, new ArrayList<>());
        }
        orderHistory.get(customer).add(order);
        customer.addOrdermenu(order);
    }

    public void updateOrderStatus(order order, order_stat newStatus){
        order.setStatus(newStatus);
    }

    public void processRefund(order order){
        if(order.getStatus() == order_stat.DELIVERED){
            order.setStatus(order_stat.CANCELED);
            total_sales -= order.getTotalCost();
            System.out.println("Refund processed for Order ID: " + order.hashCode());
        }
        else{
            System.out.println("Refund failed. Only delivered orders can be refunded.");
        }
    }

    public List<order> getPendingOrders(){
        return new ArrayList<>(pendingOrders);
    }

    public List<order> getCustomerOrders(customer customer) {
        List<order> customerOrders = new ArrayList<>();
        for (List<order> orderList : orderHistory.values()) {
            for (order o : orderList) {
                if (o.getCustomer().equals(customer)) {
                    customerOrders.add(o);
                }
            }
        }
        return customerOrders;
    }

    public daily_sales_report generateDailySalesReport(){
        Map<food_item, Integer> popularItems = new HashMap<>();
        int totalOrders = 0;

        for(List<order> orders : orderHistory.values()){
            totalOrders += orders.size();
            for(order order : orders){
                for(Map.Entry<food_item, Integer> entry : order.getItems().entrySet()){
                    food_item item = entry.getKey();
                    int quantity = entry.getValue();
                    popularItems.merge(item, quantity, Integer::sum);
                }
            }
        }

        Map<food_item, Integer> sortedPopularItems = popularItems.entrySet().stream().sorted(Map.Entry.<food_item, Integer>comparingByValue().reversed()).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return new daily_sales_report(total_sales, sortedPopularItems, totalOrders);
    }


    public void updateOrderStatusForRemovedItem(food_item item){
        pendingOrders.stream().filter(order -> order.getItems().containsKey(item)).forEach(order -> order.setStatus(order_stat.DENIED));
    }

    public Map<customer, List<order>> getOrderHistory(){
        return orderHistory;
    }
    public List<order> getCurrentOrders(){
        List<order> allCurrentOrders = new ArrayList<>();
        for (List<order> orders : orderHistory.values()) {
            allCurrentOrders.addAll(orders);
        }
        return allCurrentOrders;
    }
}