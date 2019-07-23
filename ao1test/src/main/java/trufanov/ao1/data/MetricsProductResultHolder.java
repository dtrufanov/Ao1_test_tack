package trufanov.ao1.data;

import java.util.*;

public class MetricsProductResultHolder extends ProductResultHolder {
    private LinkedList<Product> products = new LinkedList<>();
    private Map<Long, ProductMetrics> metrics = new HashMap<>();
    private double maxPrice;
    private int maxSize;
    private int maxSameSize;
    private final ProductMetrics EMPTY = new ProductMetrics();

    private static final double NO_PRICE = Double.NEGATIVE_INFINITY;

    public MetricsProductResultHolder(int maxSize, int maxSameSize) {
        this.maxSize = maxSize;
        this.maxSameSize = maxSameSize;
    }

    @Override
    public List<Product> get() {
        return products;
    }

    @Override
    public void add(Product product) {
        if (product == null) {
            return;
        }
        ProductMetrics productMetrics = getMetrics(product);
        boolean noNeedReplace = products.size() < maxSize && productMetrics.getQuantity() < maxSameSize;
        if (noNeedReplace) {
            addProduct(product);
            return;
        }
        if (product.getPrice() > maxPrice || productMetrics.getQuantity() >= maxSameSize && product.getPrice() > productMetrics.getMaxPrice()) {
            return;
        }
        replaceProduct(productMetrics, product);
    }

    private ProductMetrics getMetrics(Product product) {
        ProductMetrics productMetrics = metrics.get(product.getId());
        return productMetrics != null ? productMetrics : EMPTY;
    }

    private void addProduct(Product product) {
        products.add(product);
        Collections.sort(products);
        updateMetrics(product);
    }

    private void replaceProduct(ProductMetrics productMetrics, Product product) {
        Product productToRemove = findExpensiveProduct(productMetrics, product);
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

    private Product findExpensiveProduct(ProductMetrics productMetrics, Product productToAdd) {
        if (productMetrics.getQuantity() < maxSameSize) {
            return products.getLast();
        }
        if (productToAdd.getPrice() < productMetrics.getMaxPrice()){
            return products.stream().filter(p -> p.getId() == productToAdd.getId()).max(Comparator.comparingDouble(Product::getPrice)).orElse(null);
        }
        return null;
    }

    private void updateMetrics(Product product) {
        long id = product.getId();
        ProductMetrics productMetrics = metrics.get(id);
        if (productMetrics == null) {
            metrics.put(id, new ProductMetrics(product));
        } else {
            productMetrics.setQuantity(products.stream().filter(p -> p.getId() == product.getId()).count());
            OptionalDouble optionMax = products.stream().filter(p -> p.getId() == product.getId()).mapToDouble(Product::getPrice).max();
            productMetrics.setMaxPrice(optionMax.isPresent() ? optionMax.getAsDouble() : NO_PRICE);
        }
        maxPrice = products.stream().mapToDouble(Product::getPrice).max().orElse(NO_PRICE);
    }


    private class ProductMetrics {
        long quantity;
        double maxPrice;

        ProductMetrics() {
            this.quantity = 0L;
            this.maxPrice = NO_PRICE;
        }

        ProductMetrics(Product product) {
            quantity = 1L;
            maxPrice = product.getPrice();
        }

        long getQuantity() {
            return quantity;
        }

        void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        double getMaxPrice() {
            return maxPrice;
        }

        void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }
    }
}
