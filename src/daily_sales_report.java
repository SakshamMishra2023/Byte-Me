import java.util.Map;

public class daily_sales_report{
    private int total_sales;
    private Map<food_item, Integer> popular_items;
    private int total_order;

    public daily_sales_report(int total_sales, Map<food_item, Integer> popular_items, int total_order){
        this.total_sales = total_sales;
        this.popular_items = popular_items;
        this.total_order = total_order;
    }


    public double getTotal_sales(){
        return total_sales;
    }

    public Map<food_item, Integer> getPopular_items(){
        return popular_items;
    }

    public int getTotal_order(){
        return total_order;
    }
}
