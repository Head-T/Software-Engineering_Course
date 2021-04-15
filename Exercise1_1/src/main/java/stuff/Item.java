package stuff;

import java.util.Objects;

/*
 * Ez a class egy raktárban megtalálható tételt reprezentál.
 */
public class Item {
    /* Ez az azonosítója az adott terméknek, pl P001 */
    private final String id;
    /* Ez a leírása az adott terméknek, pl 'Póló L-es' */
    private final String description;
    /* Ez az ára a terméknek */
    private final double cost;

    public Item(String id, String description, double cost) {
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public double getCost() {
        return this.cost;
    }

    /*
     * Az itt következő pár function az Object class-ból van örökölve, célszerű őket felülírni.
     * Sokat nem kell velük foglalkozni.
     */
    @Override
    public String toString() {
        return "Item{id=" + this.id + ", description=" + this.description + ", cost=" + this.cost + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.cost, this.cost) == 0
                && Objects.equals(this.id, item.id)
                && Objects.equals(this.description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.description, this.cost);
    }
}
