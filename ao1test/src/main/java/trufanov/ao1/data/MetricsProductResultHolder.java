package trufanov.ao1.data;

import java.util.*;

public class MetricsProductResultHolder implements ProductResultHolder {
    private static final Comparator<Product> COMPARATOR = Comparator.comparingDouble(Product::getPrice);

    private LinkedList<Product> products = new LinkedList<>();
    private Map<Long, ProductMetrics> metrics = new HashMap<>();
    private double maxPrice;
    private int maxSize;
    private int maxSameSize;
    private final ProductMetrics emptyMetrics = new ProductMetrics();

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
        return productMetrics != null ? productMetrics : emptyMetrics;
    }

    private void addProduct(Product product) {
        products.add(product);
        products.sort(COMPARATOR);
        updateMetrics(product);
    }

    private void replaceProduct(ProductMetrics productMetrics, Product product) {
        Product productToReplace = findProductToReplace(productMetrics, product);
        if (productToReplace != null) {
            products.remove(productToReplace);
            products.add(product);
            products.sort(COMPARATOR);
            updateMetrics(productToReplace);
            if (product.getId() != productToReplace.getId()) {
                updateMetrics(product);
            }
        }
    }

    private Product findProductToReplace(ProductMetrics productMetrics, Product productToAdd) {
        if (productMetrics.getQuantity() < maxSameSize) {
            return products.getLast();
        }
        if (productToAdd.getPrice() < productMetrics.getMaxPrice()){
            return products.stream().filter(p -> p.getId() == productToAdd.getId()).max(COMPARATOR).orElse(null);
        }
        return null;
    }

    private void updateMetrics(Product product) {
        long id = product.getId();
        ProductMetrics productMetrics = metrics.get(id);
        if (productMetrics == null) {
            metrics.put(id, new ProductMetrics(product));
        } else {
            int count = (int) products.stream().filter(p -> p.getId() == product.getId()).count();
            if (count == 0) {
                metrics.remove(id);
            } else {
                productMetrics.setQuantity(count);
                productMetrics.setMaxPrice(products.stream()
                        .filter(p -> p.getId() == product.getId())
                        .mapToDouble(Product::getPrice)
                        .max()
                        .orElse(NO_PRICE));
            }
        }
        maxPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .max()
                .orElse(NO_PRICE);
    }


    private class ProductMetrics {
        int quantity;
        double maxPrice;

        ProductMetrics() {
            this.quantity = 0;
            this.maxPrice = NO_PRICE;
        }

        ProductMetrics(Product product) {
            quantity = 1;
            maxPrice = product.getPrice();
        }

        long getQuantity() {
            return quantity;
        }

        void setQuantity(int quantity) {
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
