package trufanov.ao1.data;

import java.util.HashMap;
import java.util.Map;

public class Metrics<T> {
    private Map<T, ProductMetrics> metrics = new HashMap<>();
    private double maxPrice;

    private class ProductMetrics {
        long quantity;
        double maxPrice;

        ProductMetrics(Product product) {
            quantity = 1L;
            maxPrice = product.getPrice();
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }
    }
}
