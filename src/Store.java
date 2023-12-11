import java.io.*;
import java.util.ArrayList;

/**
 * Store
 *
 * This class deals with managing the data regarding stores. It has methods to
 * create, edit, and delete stores, as well as access information regarding them.
 *
 * @author Sean Kim, Soham Paik, Yash Patel, CS 18000 Black, lab sec l17
 *
 * @version December 11, 2023
 */
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
        String storeID = null;  // ID of new store
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

        Store newStore = new Store(name, storeID, description);     // the new store
        Store[] stores = Account.getStores(userID);     // all user's stores
        Store[] newStores = new Store[stores.length + 1];   // new all user's stores
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
        Store[] stores = Account.getStores(Account.toUserID(ID));   // all user's stores
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
        Store[] stores = Account.getStores(Account.toUserID(ID));   // all user's stores
        ArrayList<Store> storesList = new ArrayList<>();    // arraylist of user's new all stores
        for (Store store : stores) {
            if (!store.getID().equals(ID)) {
                storesList.add(store);
            }
        }
        Store[] newStores = new Store[storesList.size()];   // array of user's new all stores
        for (int i = 0; i < newStores.length; i++) {
            newStores[i] = storesList.get(i);
        }
        Account.setStores(Account.toUserID(ID), newStores);
    }

    /**
     * Returns an array of all the stores
     * @return an array of all the stores
     */
    public static Store[] getStores() {
        Account[] accounts = Account.readAccountsData();    // accounts data
        ArrayList<Store> storesList = new ArrayList<>();    // arraylist of all the stores
        for (Account account : accounts) {
            for (Store store : account.getStores()) {
                storesList.add(store);
            }
        }
        Store[] stores = new Store[storesList.size()];  // array of all the stores
        for (int i = 0; i < stores.length; i++) {
            stores[i] = storesList.get(i);
        }
        return stores;
    }

    /**
     * Returns the owner of the store
     * @return the owner of the store
     */
    public Account getOwner() {
        return Account.accountWithID(Account.toUserID(ID));
    }

    /**
     * Returns whether the store name exists
     * @param storeName the store name
     * @return whether the store name exists
     */
    public static boolean storeNameExists(String storeName) {
        Account[] accounts = Account.readAccountsData();    // accounts data
        for (Account account : accounts) {
            for (Store store : account.getStores()) {
                if (store.getName().equals(storeName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
