package stuff;

import java.util.HashMap;
import java.util.Map;

/* A "raktár" */
public class StorageDepot {
    /*
     * Emlékeim szerint nem volt szó a mapekről, pedig sok mindent
     * nem lehet nélkülük elképzelni. Tulajdonképpen egy Key -> Value
     * lista, ahol egy adott kulcshoz legfeljebb egy érték tartozhat.
     * Ebben az esetben egy adott Item lesz a kulcs, a hozzá tartozó
     * mennyiség pedig az érték.
     */
    private final Map<Item, Integer> stock;
    private final Map<String, Item> nameCache = new HashMap<>();

    public StorageDepot(Map<Item, Integer> stock) {
        this.stock = stock;
        /* Az itemeket társítjuk ID-hez */
        for (Item item : stock.keySet()) {
            this.nameCache.put(item.getId(), item);
        }
    }

    /*
     * Ha a map-ben megtalálható az item (tehát van ilyen item raktáron),
     * akkor visszaadjuk a hozzá társított számot (vagyis azt, hogy mennyi
     * ilyen item van raktáron), egyébként pedig 0-t.
     */
    public int getInStock(Item item) {
        if (this.stock.containsKey(item)) {
            return this.stock.get(item);
        }

        /* Ha nem található ilyen item a raktárban, akkor 0-t adunk vissza */
        return 0;
    }

    /*
     * Készleten lévő mennyiség módosítása
     */
    public void setInStock(Item item, int amount) {
        if (amount < 1) {
            this.stock.remove(item);
        }

        this.stock.put(item, amount);
    }

    /*
     * Le lehet kérni egy itemet a hozzá kapcsolódó ID alapján.
     * Ez azért hasznos, mert a megrendelésekben csak az ID van
     * meghatározva. Ha nincs ilyen id-vel Item raktáron, akkor
     * null-t adunk vissza.
     */
    public /*@Nullable*/ Item getItemById(String id) {
        return this.nameCache.get(id);
    }

    public Map<Item, Integer> getStock() {
        return this.stock;
    }
}