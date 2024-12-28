import java.util.HashMap;
import java.util.Map;

public class CustomerManager {
    private static CustomerManager instance;
    private Map<String, customer> registeredCustomers = new HashMap<>();
    private cust_file_handler fileHandler;
    private cust_hist_mg historyManager;

    private CustomerManager(){
        fileHandler = new cust_file_handler();
        historyManager = new cust_hist_mg();
    }

    public static CustomerManager getInstance(){
        if(instance == null){
            instance = new CustomerManager();
        }
        return instance;
    }


    public customer loginOrRegister(String name, String email, boolean isVIP){
        customer customer = new customer(name, email, isVIP);
        if(historyManager.hasHistory(email)){
            historyManager.loadCustomerHistory(customer);
        }
        return customer;
    }

    public customer getCustomerByEmail(String email) {
        return registeredCustomers.get(email);
    }
}