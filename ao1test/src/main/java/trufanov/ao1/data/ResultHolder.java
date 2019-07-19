package trufanov.ao1.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResultHolder {
    private double maxPrice;
    private LinkedList<Product> products = new LinkedList<>();
    private int maxSize;
    private int maxSameSize;

    private Map<Long, ProductMetrics> metrics = new HashMap<>();

    public ResultHolder(int maxSize, int maxSameSize) {
        this.maxSize = maxSize;
        this.maxSameSize = maxSameSize;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void add(Product product) {
        if (products.size() < maxSize && getQuantity(product.getId()) < maxSameSize) {
            products.add(product);
            Collections.sort(products);
            updateMetrics(product);
            return;
        }
        if (product.getPrice() > maxPrice) {
            return;
        }
        Product productToRemove = findExpensiveProduct(product);
        if (productToRemove != null) {
            products.remove(productToRemove);
            products.add(product);
            Collections.sort(products);
            updateMetrics(productToRemove);
            if (product.getId() != productToRemove.getId()) {
                updateMetrics(product);
            }
        }
    }

    private long getQuantity(long id) {
        ProductMetrics productMetrics = metrics.get(id);
        return productMetrics != null ? productMetrics.getQuantity() : 0;
    }

    private Product findExpensiveProduct(Product productToAdd) {
        ProductMetrics productMetrics = metrics.get(productToAdd.getId());
        if (productMetrics != null) {
            if (productMetrics.getQuantity() < maxSameSize) {
                return products.getLast();
            }
            if (productToAdd.getPrice() < productMetrics.getMaxPrice()){
                return products.stream().filter(p -> p.getId() == productToAdd.getId()).max(Comparator.comparingDouble(Product::getPrice)).get();//todo get
            }
        }
        return null;
    }

    private void updateMetrics(Product product) {
        ProductMetrics productMetrics = metrics.get(product.getId());
        if (productMetrics == null) {
            metrics.put(product.getId(), new ProductMetrics(product));
        } else {
            productMetrics.setQuantity(products.stream().filter(p -> p.getId() == product.getId()).count());
            OptionalDouble optionMax = products.stream().filter(p -> p.getId() == product.getId()).mapToDouble(Product::getPrice).max();
            productMetrics.setMaxPrice(optionMax.isPresent() ? optionMax.getAsDouble() : Double.NaN);
        }
        maxPrice = products.stream().mapToDouble(Product::getPrice).max().getAsDouble();
    }


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
