package stuff;

import java.util.Map;
import java.util.Objects;

/* Egy feldolgozott rendelés. */
public class ProcessedOrder {
    /* A megrendelő email címe */
    private final String emailAddress;
    /* Melyik item-ből mennyi hiányzik (ha egyáltalán) */
    private final Map<String, Integer> missing;
    /* A végösszeg */
    private final double totalCost;

    public ProcessedOrder(String emailAddress, Map<String, Integer> missing, double totalCost) {
        this.emailAddress = emailAddress;
        this.missing = missing;
        this.totalCost = totalCost;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Map<String, Integer> getMissingProducts() {
        return this.missing;
    }

    public double getTotalCost() {
        return this.totalCost;
    }

    @Override
    public String toString() {
        return "ProcessedOrder{email=" + this.emailAddress + ", missing=" + this.missing + ", totalCost=" + this.totalCost + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedOrder that = (ProcessedOrder) o;
        return Objects.equals(this.emailAddress, that.emailAddress)
                && Double.compare(that.totalCost, this.totalCost) == 0
                && Objects.equals(this.missing, that.missing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.emailAddress, this.missing, this.totalCost);
    }
}
