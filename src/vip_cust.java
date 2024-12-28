// VIPCustomer.java
public class vip_cust extends customer {
    private static final double VIP_FEE = 10.0;

    public vip_cust(String name, String email) {
        super(name, email, true);
    }

    public static double getVipFee() {
        return VIP_FEE;
    }
}
