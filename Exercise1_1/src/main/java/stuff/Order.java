package stuff;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
 * Ez a class a beérkezett rendeléseket hivatott jelölni. Bocsi,
 * nem szeretek magyarul kódot írni (kivéve a kommenteket) :/
 */
public class Order {
    /* A rendelés száma */
    private final int id;
    /* A rendelés időpontja (Forma: yyyy.MM.dd HH:mm) */
    private final String date;
    /* A megrendelő email címe */
    private final String emailAddress;
    /* A megrendelt termékek */
    private final Set<Product> products = new HashSet<>();

    public Order(int id, String date, String emailAddress) {
        this.id = id;
        this.date = date;
        this.emailAddress = emailAddress;
    }

    public int getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void addProduct(Product item) {
        this.products.add(item);
    }

    /*
     * Az itt következő pár function az Object class-ból van örökölve, célszerű őket felülírni.
     * Sokat nem kell velük foglalkozni.
     */
    @Override
    public String toString() {
        return "Order{id=" + this.id + ", date=" + this.date + ", email=" + this.emailAddress + ", items=" + this.products + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return this.id == order.id
                && Objects.equals(this.date, order.date)
                && Objects.equals(this.emailAddress, order.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.date, this.emailAddress);
    }

    /* Egy megrendelt terméket jelöl */
    public static final class Product {
        /* A megrendelt termék azonosítója */
        private final String itemId;
        /* A megrendelt termék mennyisége */
        private final int amount;

        public Product(String itemId, int amount) {
            this.itemId = itemId;
            this.amount = amount;
        }

        public String getItemId() {
            return this.itemId;
        }

        public int getAmount() {
            return this.amount;
        }

        /*
         * Az itt következő pár function az Object class-ból van örökölve, célszerű őket felülírni.
         * Sokat nem kell velük foglalkozni.
         */
        @Override
        public String toString() {
            return "Order$Product{itemId=" + this.itemId + ", amount=" + this.amount + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Product product = (Product) o;
            return this.amount == product.amount && Objects.equals(this.itemId, product.itemId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.itemId, this.amount);
        }
    }
}
