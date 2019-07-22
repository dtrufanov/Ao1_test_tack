package trufanov.ao1.data;

import java.util.*;
import java.util.function.Function;

public class PriceBatchResultHolder<T> {
    private LinkedList<Product> products = new LinkedList<>();
    private Map<T, ProductMetrics> metrics = new HashMap<>();
    private double maxPrice;
    private int maxSize;
    private int maxSameSize;
    private Function<Product, T> identification;

    private static final double NO_PRICE = Double.NEGATIVE_INFINITY;

    public PriceBatchResultHolder(int maxSize, int maxSameSize, Function<Product, T> identification) {
        this.maxSize = maxSize;
        this.maxSameSize = maxSameSize;
        this.identification = identification;
    }

    public List<Product> getProducts() {
        return products;
    }

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

    public void merge(PriceBatchResultHolder<T> another) {
        for (Product product : another.getProducts()) {
            add(product);
        }
    }

    private ProductMetrics getMetrics(Product product) {
        ProductMetrics productMetrics = metrics.get(identification.apply(product));
        return productMetrics != null ? productMetrics : new ProductMetrics();
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
            return products.stream().filter(p -> identification.apply(p).equals(identification.apply(productToAdd))).max(Comparator.comparingDouble(Product::getPrice)).orElse(null);
        }
        return null;
    }

    private void updateMetrics(Product product) {
        T id = identification.apply(product);
        ProductMetrics productMetrics = metrics.get(id);
        if (productMetrics == null) {
            metrics.put(id, new ProductMetrics(product));
        } else {
            productMetrics.setQuantity(products.stream().filter(p -> identification.apply(p).equals(identification.apply(product))).count());
            OptionalDouble optionMax = products.stream().filter(p -> identification.apply(p).equals(identification.apply(product))).mapToDouble(Product::getPrice).max();
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
