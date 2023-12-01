import java.io.*;

public class Store {
    private String name;            // name of store
    private String ID;              // ID of store
    private String description;     // description of store

    /**
     * Instantiates a Store object with the given fields
     * @param name name of store
     * @param ID ID of store
     * @param description description of store
     */
    public Store(String name, String ID, String description) {
        this.name = name;
        this.ID = ID;
        this.description = description;
    }

    /**
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns ID
     * @return ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Returns description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Creates a new store with name and description for user with userID
     * @param userID the user ID
     * @param name the name of the store
     * @param description the description of the store
     */
    public static void createStore(String userID, String name, String description) {
        String storeID = null;
        try (BufferedReader bfr = new BufferedReader(new FileReader("storeIDIncrement.txt"))) {
            storeID = bfr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("storeIDIncrement.txt"))) {
            pw.println(Integer.parseInt(storeID) + 2 + "");
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Store newStore = new Store(name, storeID, description);
        Store[] stores = Account.getStores(userID);
        Store[] newStores = new Store[stores.length + 1];
        for (int i = 0; i < newStores.length - 1; i++) {
            newStores[i] = stores[i];
        }
        newStores[newStores.length - 1] = newStore;
        Account.setStores(userID, newStores);
    }
}
