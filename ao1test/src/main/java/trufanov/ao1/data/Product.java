package trufanov.ao1.data;

public class Product implements Comparable<Product> {
    private String line;
    private long id;
    private double price;

    public static Product parseProduct(String input) {
        return new Product(input,
                Long.parseLong(input.substring(0, input.indexOf(','))),
                Double.parseDouble(input.substring(input.lastIndexOf(',') + 1)));
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
    public int compareTo(Product o) {
        return Double.compare(price, o.price);
    }

    @Override
    public String toString() {
        return line;
    }
}
