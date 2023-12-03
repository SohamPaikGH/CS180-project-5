import java.io.*;
import java.util.ArrayList;

public class Store {
    private String name;            // name of store
    private String ID;              // ID of store
    private String description;     // description of store
    private static Object storeIDIncrementSync = new Object();  // object to synchronize writing to storeIDIncrement.txt

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
            synchronized (storeIDIncrementSync) {
                pw.println(Integer.parseInt(storeID) + 2 + "");
                pw.flush();
            }
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

    /**
     * Changes a stores name and description to the new ones given
     * @param ID ID of the store
     * @param name new name of the store
     * @param description new description of the store
     */
    public static void editStore(String ID, String name, String description) {
        Store[] stores = Account.getStores(Account.toUserID(ID));
        for (Store store : stores) {
            if (store.getID().equals(ID)) {
                store.name = name;
                store.description = description;
                break;
            }
        }
        Account.setStores(Account.toUserID(ID), stores);
    }

    /**
     * Deletes the store with the given ID
     * @param ID ID of the store
     */
    public static void deleteStore(String ID) {
        Store[] stores = Account.getStores(Account.toUserID(ID));
        ArrayList<Store> storesList = new ArrayList<>();
        for (Store store : stores) {
            if (!store.getID().equals(ID)) {
                storesList.add(store);
            }
        }
        Store[] newStores = new Store[storesList.size()];
        for (int i = 0; i < newStores.length; i++) {
            newStores[i] = storesList.get(i);
        }
        Account.setStores(Account.toUserID(ID), newStores);
    }
}
