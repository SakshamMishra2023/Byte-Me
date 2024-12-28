import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class food_item implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int price;
    private String category;
    private boolean available;
    private List<review> reviews;

    public food_item(String name, int price, String category, boolean available){
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
        this.reviews = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public double get_price(){
        return price;
    }

    public String get_category(){
        return category;
    }

    public boolean is_available(){
        return available;
    }

    public List<review> get_review(){
        return reviews;
    }

    public void add_review(review review){
        reviews.add(review);
    }

    public void set_pride(int price){
        this.price = price;
    }

    public void set_available(boolean available){
        this.available = available;
    }

}
