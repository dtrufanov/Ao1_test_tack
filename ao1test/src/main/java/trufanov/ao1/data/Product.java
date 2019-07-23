package trufanov.ao1.data;

import java.util.logging.Logger;

public class Product {
    private static final Logger logger = Logger.getGlobal();

    private String line;
    private long id;
    private double price;

    public static Product parseProduct(String input) {
        try {
            return new Product(input,
                    Long.parseLong(input.substring(0, input.indexOf(','))),
                    Double.parseDouble(input.substring(input.lastIndexOf(',') + 1)));
        } catch (Exception e) {
            logger.warning("Failed to parse string: " + e.getMessage());
            return null;
        }
    }

    public Product(String line, long id, double price) {
        this.line = line;
        this.id = id;
        this.price = price;
    }

    public String getLine() {
        return line;
    }

    public long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return line;
    }
}
