package stuff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Exercise1_1 {

    public static void main(String[] args) throws IOException {
        System.out.println("Raktar.csv betoltese...");
        StorageDepot storage = new StorageDepot(loadStorage());
        System.out.println("Betoltve!");

        System.out.println("Megrendelesek betoltese...");
        List<Order> orders = loadOrders();
        System.out.println("Betoltve!");

        System.out.println("Rendelesek feldolgozasa...");
        List<ProcessedOrder> processedOrders = processOrders(storage, orders);
        System.out.println("Kesz!");

        System.out.println("Fajlok letrehozasa, frissitese");
        generateFiles(storage, processedOrders);
        System.out.println("Kesz!");
    }

    /*
     * A levelek.csv, és a beszerzes.csv fájl létrehozása, feltöltése a megfelelő adatokkal,
     * a raktar.csv frissítése az új adatoknak megfelelően.
     */
    private static void generateFiles(StorageDepot storage, List<ProcessedOrder> orders) throws IOException {
        // ====== levelek.csv fájl létrehozása ====== //
        File levelekFile = new File("levelek.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(levelekFile))) {
            for (ProcessedOrder order : orders) {
                writer.append(order.getEmailAddress()).append(';');

                /* Ha nincsenek hiányzó termékek, akkor a szállításra kész a cucc */
                if (order.getMissingProducts().isEmpty()) {
                    writer.append("A rendelését két napon belül szállítjuk. A rendelés értéke: ")
                            .append(String.valueOf(order.getTotalCost()))
                            .append(" Ft");
                } else {
                    writer.append("A rendelése függő állapotba került. Hamarosan értesítjük a szállítás időpontjáról.");
                }

                /* Így a következő entry a következő sorba fog kerülni. */
                writer.append(System.lineSeparator());
            }
        }

        // ====== raktar.csv fájl frissítése ====== //
        File raktarFile = new File("raktar.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(raktarFile))) {
            for (Map.Entry<Item, Integer> entry : storage.getStock().entrySet()) {
                Item item = entry.getKey();

                writer.append(item.getId()).append(';')
                        .append(item.getDescription())
                        .append(';')
                        .append(String.valueOf(item.getCost()))
                        .append(';')
                        // Az item mennyisége, ennyi van most raktáron
                        .append(String.valueOf(entry.getValue()))
                        .append(System.lineSeparator());
            }
        }

        // ====== Hiány kiszámolása ====== //
        Map<String, Integer> totalMissing = new HashMap<>();
        for (ProcessedOrder order : orders) {
            for (Map.Entry<String, Integer> entry : order.getMissingProducts().entrySet()) {
                String itemId = entry.getKey();
                int missing = totalMissing.containsKey(itemId)
                        ? totalMissing.get(itemId) + entry.getValue()
                        : entry.getValue();

                totalMissing.put(itemId, missing);
            }
        }

        // ====== beszerzes.csv fájl létrehozása ====== //
        File beszerzesFile = new File("beszerzes.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(beszerzesFile))) {
            for (Map.Entry<String, Integer> entry : totalMissing.entrySet()) {
                writer.append(entry.getKey()).append(';')
                        .append(String.valueOf(entry.getValue()))
                        .append(System.lineSeparator());
            }
        }
    }

    /*
     * Beérkezett rendelések feldolgozása
     */
    private static List<ProcessedOrder> processOrders(StorageDepot storage, List<Order> orders) {
        List<ProcessedOrder> processedOrders = new ArrayList<>();
        for (Order order : orders) {
            Set<Order.Product> products = order.getProducts();
            if (products.isEmpty()) {
                System.err.println("Hiba! A megrendeles nem tartalmazott semmilyen termeket: " + order);
                continue;
            }

            double totalCost = 0.0D;
            Map<String, Integer> missing = new HashMap<>();

            for (Order.Product product : order.getProducts()) {
                String itemId = product.getItemId();
                Item item = storage.getItemById(itemId);

                if (item == null) {
                    System.err.println("Nem talalhato Item ezzel az azonositoval: " + itemId);
                    continue;
                }

                int purchasedAmount = product.getAmount();
                int inStock = storage.getInStock(item);
                if (inStock < purchasedAmount) {
                    int missingAmount = purchasedAmount - inStock;
                    storage.setInStock(item, 0);
                    missing.put(itemId, missingAmount);
                } else {
                    storage.setInStock(item, inStock - purchasedAmount);
                }

                totalCost += purchasedAmount * item.getCost();
            }

            ProcessedOrder processedOrder = new ProcessedOrder(order.getEmailAddress(), missing, totalCost);
            processedOrders.add(processedOrder);
        }

        return processedOrders;
    }

    /* Betöltjük a rendeles.csv fájl elemeit egy listába */
    private static List<Order> loadOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        File file = new File("rendeles.csv");

        /*
         * Azt, hogy a try után írtam zárójelbe a BufferedReadert,
         * "try-with-resources"-nak hívják. Előnye, hogy a zárójelek
         * között megtalálható objektumokat automatikusan le fogja zárni,
         * amint vége a try blokkban található kódnak, így nincs szükség arra, hogy
         * meghívjam a close() function-t. Természetesen a hagyományos megoldás
         * ugyanúgy működik, az valahogy így nézne ki:
         *
         * BufferedReader reader = new BufferedReader(new FileReader(file));
         * // Itt betöltjük az adatokat.
         *  reader.close();
         */
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            /* Soronként beolvassuk a fájlt */
            while ((line = reader.readLine()) != null) {
                // Ebben az esetben ez egy megrendelés
                if (line.startsWith("M")) {
                    String[] data = line.split(";");
                    /*
                     * A megrendelés adatai az alábbi formában vannak tárolva:
                     * M;<dátum>;<azonosító>;<email> Ha a data nevű tömb hossza
                     * nem négy, akkor hibás a sor.
                     */
                    if (data.length != 4) {
                        System.err.println("Hibas sor: " + line + "... A kapott tomb hossza 4 kell legyen");
                    } else {
                        // A 0. elem az, hogy 'M', ami nekünk nem kell már.
                        String date = data[1];
                        int id;
                        try {
                            id = Integer.parseInt(data[2]);
                        } catch (NumberFormatException ex) {
                            System.err.println("Hibas ID lett megadva itt: " + line + ", " + data[2]);
                            continue;
                        }

                        String emailAddress = data[3];
                        Order order = new Order(id, date, emailAddress);
                        orders.add(order);
                    }

                } else if (line.startsWith("T")) {
                    // Ebben az esetben ez egy tétel
                    if (orders.isEmpty()) {
                        System.err.println("Hiba! A teteleket megrendeleseknek kell megeloznie.");
                    } else {
                        // Na ebben nem vagyok biztos, hogy így a legjobb. Visszakapjuk az utolsó elemet
                        Order order = orders.get(orders.size() - 1);
                        String[] data = line.split(";");
                        // T;<rendelés_id>;<termék_id>;<mennyiség>
                        if (data.length != 4) {
                            System.err.println("Hibas sor itt: " + line);
                        } else {
                            int orderId;
                            try {
                                orderId = Integer.parseInt(data[1]);
                            } catch (NumberFormatException ex) {
                                System.err.println("Hibas megrendeles azonosito itt: " + line + ", " + data[1]);
                                continue;
                            }
                            if (orderId != order.getId()) {
                                System.err.println("Hiba! A kapott megrendelesi azonosito nem egyezik meg az utolso rendeles azonositojaval");
                                continue;
                            }

                            String productId = data[2];
                            int amount;

                            try {
                                amount = Integer.parseInt(data[3]);
                            } catch (NumberFormatException ex) {
                                System.err.println("Hibas mennyiseg lett megadva: " + line + ", " + data[3]);
                                continue;
                            }

                            Order.Product product = new Order.Product(productId, amount);
                            order.addProduct(product);
                        }
                    }
                } else {
                    System.err.println("Hibas sor: " + line);
                }
            }
        }

        return orders;
    }

    /* Betöltjük a raktar.csv fájl elemeit egy Map-be */
    private static Map<Item, Integer> loadStorage() throws IOException {
        /* Ebbe a Map-be töltjük be a fájlban található adatokat */
        Map<Item, Integer> stock = new HashMap<>();
        File file = new File("raktar.csv");

        /*
         * Azt, hogy a try után írtam zárójelbe a BufferedReadert,
         * "try-with-resources"-nak hívják. Előnye, hogy a zárójelek
         * között megtalálható objektumokat automatikusan le fogja zárni,
         * amint vége a try blokkban található kódnak, így nincs szükség arra, hogy
         * meghívjam a close() function-t. Természetesen a hagyományos megoldás
         * ugyanúgy működik, az valahogy így nézne ki:
         *
         * BufferedReader reader = new BufferedReader(new FileReader(file));
         * // Itt betöltjük az adatokat.
         *  reader.close();
         */
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            /* Soronként beolvassuk a fájlt */
            while ((line = reader.readLine()) != null) {
                /* Elválasztjuk a sort a ; karaktereknél */
                String[] data = line.split(";");
                /*
                 * id;leírás;ár;mennyiség formában vannak tárolva a termékek,
                 * a data nevű tömbnek 4 eleme kell legyen. Ha nem ennyi, akkor
                 * hibás a sor. Ez a rész kihagyható, de szerintem érdemes az ilyen
                 * eseteket is kezelni.
                 */
                if (data.length != 4) {
                    System.err.println("Hibas sor: " + line);
                } else {
                    String id = data[0];
                    String description = data[1];
                    double cost;
                    int inStock;

                    try {
                        cost = Double.parseDouble(data[2]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Hibas eladasi ar lett megadva itt: " + line + ", " + data[2]);
                        continue;
                    }

                    try {
                        inStock = Integer.parseInt(data[3]);
                    } catch (NumberFormatException ex) {
                        System.err.println("Hibas mennyiseg lett megadva itt: " + line + ", " + data[3]);
                        continue;
                    }

                    Item item = new Item(id, description, cost);
                    /*
                     * Ez megint kihagyható, ha az adatbázist jól töltötték fel adatokkal,
                     * akkor nem fog lefutni, de én szeretek biztosra menni.
                     */
                    if (stock.containsKey(item)) {
                        System.err.println("Mar talalhato a listaban ilyen item: " + item);
                    } else {
                        stock.put(item, inStock);
                    }
                }
            }
        }

        return stock;
    }
}