
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class order implements Serializable {
    private customer customer;
    private Map<food_item, Integer> items;
    private String specialRequest;
    private boolean specialRequestHandled = false; // New field
    private String refundStatus = "NOT_REFUNDED";
    private order_stat status;
    private LocalDateTime orderDateTime;
    private int orderId;
    private double totalCost;
    private static int nextOrderId = 1;
    private String contactNumber;
    private String shippingAddress;
    private String paymentMode;
    private String paymentDetails;


    public order(customer customer){
        this.customer = customer;
        this.items = new HashMap<>();
        this.status = order_stat.RECEIVED;
        this.orderDateTime = LocalDateTime.now();
        this.orderId = nextOrderId++;
        System.out.println("DEBUG: Order created for customer " + customer.getName());

    }
    public void debugOrderContents() {
        //System.out.println("DEBUG: Order Contents");
        //System.out.println("Total items: " + items.size());
        //for (Map.Entry<food_item, Integer> entry : items.entrySet()) {
        //    System.out.println("Item: " + entry.getKey().getName()
         //           + ", Price: " + entry.getKey().get_price()
         //           + ", Quantity: " + entry.getValue());
        //}
    }
    public void calculateTotalCost(){
        this.totalCost = 0.0;
        for(Map.Entry<food_item, Integer> entry : items.entrySet()){
            food_item item = entry.getKey();
            int quantity = entry.getValue();
            this.totalCost += item.get_price() * quantity;
        }
    }
    public customer getCustomer(){
        return customer;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public Map<food_item, Integer> getItems(){
        return items;
    }

    public void setItems(Map<food_item, Integer> items){
        this.items = new HashMap<>(items);

    }

    public String getSpecialRequest(){
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest){
        this.specialRequest = specialRequest;
    }
    public int getOrderId() {
        return orderId;
    }

    public boolean isSpecialRequestHandled(){
        return specialRequestHandled;
    }
    public String getRefundStatus(){
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus){
        this.refundStatus = refundStatus;
    }

    public void setSpecialRequestHandled(boolean handled){
        this.specialRequestHandled = handled;
    }

    public order_stat getStatus(){
        return status;
    }

    public void setStatus(order_stat status){
        this.status = status;
    }

    public LocalDateTime getOrderDateTime(){
        return orderDateTime;
    }

    public double getTotalCost(){
        return this.totalCost;
    }

    public void setTotalCost(double v) {
        this.totalCost = v;
    }

    public void setOrderDateTime(LocalDateTime parse) {
        this.orderDateTime = parse;
    }
}
