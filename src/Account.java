import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Account
 *
 * Class to deal with account data, such as editing and accessing
 * I used Tutorialspoint to learn how to use json files in java
 *
 * @author Sean Kim, Soham Paik, Yash Patel, lab sec l17
 *
 * @version December 10, 2023
 */
public class Account {
    private String username;        // username
    private String email;           // email
    private String password;        // password
    private String role;            // role
    private String ID;              // ID
    private Store[] stores;         // array of stores owned by account
    private Message[] messages;     // array of messages associated with account
    private String[] blocked;       // array containing ID's of users that this user blocked
    private String[] invisibleTo;   // array containing ID's of users that this user became invisible to
    private static Object accountsDataSync = new Object();      // object to synchronize writing to accountsData.json
    private static Object userIDIncrementSync = new Object();   // object to synchronize writing to userIDIncrement.txt

    /**
     * Instantiates an account object with given fields
     * @param username username
     * @param email email
     * @param password password
     * @param role role
     * @param ID ID
     * @param stores array of stores owned by account
     * @param messages array of messages associated with account
     * @param blocked array containing ID's of users that this user blocked
     * @param invisibleTo array containing ID's of users that this user became invisible to
     */
    public Account(String username, String email, String password, String role, String ID, Store[] stores, Message[] messages, String[] blocked, String[] invisibleTo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.ID = ID;
        this.stores = stores;
        this.messages = messages;
        this.blocked = blocked;
        this.invisibleTo = invisibleTo;
    }

    /**
     * Creates new account with given fields
     * @param username username
     * @param email email
     * @param password password
     * @param role role
     */
    public static void createAccount(String username, String email, String password, String role) {
        JSONObject newAccount = new JSONObject();   // new account json object
        newAccount.put("username", username);
        newAccount.put("email", email);
        newAccount.put("password", password);
        newAccount.put("role", role);
        String ID = "";     // ID of new account
        try (BufferedReader bfr = new BufferedReader(new FileReader("userIDIncrement.txt"))) {
            ID = bfr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("userIDIncrement.txt"))) {
            synchronized (userIDIncrementSync) {
                pw.println(Integer.parseInt(ID) + 2 + "");
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        newAccount.put("ID", ID);
        newAccount.put("stores", new JSONArray());
        newAccount.put("messages", new JSONArray());
        newAccount.put("blocked", new JSONArray());
        newAccount.put("invisibleTo", new JSONArray());

        JSONArray accountsJsonArray = new JSONArray();  // json array to hold all the json account objects
        JSONParser jsonParser = new JSONParser();   // to read the json file
        try (FileReader fr = new FileReader("accountsData.json")) {
            Object obj = jsonParser.parse(fr);  // the main json array
            accountsJsonArray = (JSONArray) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        accountsJsonArray.add(newAccount);

        try (FileWriter fw = new FileWriter("accountsData.json")) {
            synchronized (accountsDataSync) {
                fw.write(accountsJsonArray.toJSONString());
                fw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the json file of account data and returns an array of account objects containing all the data
     * @return an array of account objects containing all the data from the json file
     */
    public static Account[] readAccountsData() {
        ArrayList<Account> accountsList = new ArrayList<>();    // arraylist to hold accounts

        JSONArray accountsJsonArray = new JSONArray();  // json array to hold all json account objects
        JSONParser jsonParser = new JSONParser();   // to read json file
        try (FileReader fr = new FileReader("accountsData.json")) {
            Object obj = jsonParser.parse(fr);  // the main json array
            accountsJsonArray = (JSONArray) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Object objectAccount : accountsJsonArray) {
            JSONObject jsonAccount = (JSONObject) objectAccount;    // json object of account
            String username = (String) jsonAccount.get("username");     // username of account
            String email = (String) jsonAccount.get("email");   // email of account
            String password = (String) jsonAccount.get("password");     // password of account
            String role = (String) jsonAccount.get("role");     // role of account
            String ID = (String) jsonAccount.get("ID");     // ID of account

            ArrayList<String> blockedList = new ArrayList<>();  // blocked list of account
            for (Object objectBlockedID : (JSONArray) jsonAccount.get("blocked")) {
                String blockedID = (String) objectBlockedID;    // blocked ID being read
                blockedList.add(blockedID);
            }
            String[] blocked = new String[blockedList.size()];  // blocked array of account
            for (int i = 0; i < blockedList.size(); i++) {
                blocked[i] = blockedList.get(i);
            }

            ArrayList<String> invisibleToList = new ArrayList<>();  // invisible to list of account
            for (Object objectInvisibleToID : (JSONArray) jsonAccount.get("invisibleTo")) {
                String invisibleToID = (String) objectInvisibleToID;    // invisible to ID being read
                invisibleToList.add(invisibleToID);
            }
            String[] invisibleTo = new String[invisibleToList.size()];  // invisible to array of account
            for (int i = 0; i < invisibleToList.size(); i++) {
                invisibleTo[i] = invisibleToList.get(i);
            }

            ArrayList<Store> storesList = new ArrayList<>();    // list of stores of the account
            for (Object objectStore : (JSONArray) jsonAccount.get("stores")) {
                JSONObject jsonStore = (JSONObject) objectStore;    // store json object being read
                String storeName = (String) jsonStore.get("name");  // name of store
                String storeID = (String) jsonStore.get("ID");      // ID of store
                String storeDescription = (String) jsonStore.get("description");    // description of store
                Store store = new Store(storeName, storeID, storeDescription);      // store object of the json object
                storesList.add(store);
            }
            Store[] stores = new Store[storesList.size()];  // array of stores of the account
            for (int i = 0; i < storesList.size(); i++) {
                stores[i] = storesList.get(i);
            }

            ArrayList<Message> messagesList = new ArrayList<>();    // list of messages of the account
            for (Object objectMessage : (JSONArray) jsonAccount.get("messages")) {
                JSONObject jsonMessage = (JSONObject) objectMessage;    // message json object being read
                String senderID = (String) jsonMessage.get("senderID");     // ID of sender of message
                String recipientID = (String) jsonMessage.get("recipientID");   // ID of recipient of message
                String message = (String) jsonMessage.get("message");   // message
                boolean deletedForSender = (boolean) jsonMessage.get("deletedForSender");   // sender deleted
                boolean deletedForRecipient = (boolean) jsonMessage.get("deletedForRecipient");  // recipient deleted
                long order = (long) jsonMessage.get("order");   // order of message
                Message messageObject = new Message(senderID, recipientID, message, deletedForSender,
                        deletedForRecipient, order);    // message object of the json object
                messagesList.add(messageObject);
            }
            Message[] messages = new Message[messagesList.size()];  // array of messages of the account
            for (int i = 0; i < messagesList.size(); i++) {
                messages[i] = messagesList.get(i);
            }

            Account account = new Account(username, email, password, role, ID, stores, messages, blocked, invisibleTo);
            // account object of the json object
            accountsList.add(account);
        }

        Account[] accounts = new Account[accountsList.size()];  // array of accounts in the json array
        for (int i = 0; i < accountsList.size(); i++) {
            accounts[i] = accountsList.get(i);
        }
        return accounts;
    }

    /**
     * Writes the accounts data json file given an array of accounts
     * @param accounts array of accounts
     */
    public static void writeAccountsData(Account[] accounts) {
        JSONArray accountsJsonArray = new JSONArray();  // new accounts json array
        for (Account account : accounts) {
            JSONObject accountJsonObject = new JSONObject();    // json account object
            accountJsonObject.put("username", account.username);
            accountJsonObject.put("email", account.email);
            accountJsonObject.put("password", account.password);
            accountJsonObject.put("role", account.role);
            accountJsonObject.put("ID", account.ID);

            JSONArray storesArray = new JSONArray();    // json stores array of account
            for (Store store : account.stores) {
                JSONObject storeJsonObject = new JSONObject();  // json store object
                storeJsonObject.put("name", store.getName());
                storeJsonObject.put("ID", store.getID());
                storeJsonObject.put("description", store.getDescription());
                storesArray.add(storeJsonObject);
            }
            accountJsonObject.put("stores", storesArray);

            JSONArray messagesArray = new JSONArray();  // json messages array of account
            for (Message message : account.messages) {
                JSONObject messageJsonObject = new JSONObject();    // json message object
                messageJsonObject.put("senderID", message.getSenderID());
                messageJsonObject.put("recipientID", message.getRecipientID());
                messageJsonObject.put("message", message.getMessage());
                messageJsonObject.put("deletedForSender", message.isDeletedForSender());
                messageJsonObject.put("deletedForRecipient", message.isDeletedForRecipient());
                messageJsonObject.put("order", message.getOrder());
                messagesArray.add(messageJsonObject);
            }
            accountJsonObject.put("messages", messagesArray);

            JSONArray blockedArray = new JSONArray();   // json blocked array of account
            for (String blockedID : account.blocked) {
                blockedArray.add(blockedID);
            }
            accountJsonObject.put("blocked", blockedArray);

            JSONArray invisibleToArray = new JSONArray();   // json invisible to array of account
            for (String invisibleToID : account.invisibleTo) {
                invisibleToArray.add(invisibleToID);
            }
            accountJsonObject.put("invisibleTo", invisibleToArray);

            accountsJsonArray.add(accountJsonObject);
        }

        try (FileWriter fw = new FileWriter("accountsData.json")) {
            synchronized (accountsDataSync) {
                fw.write(accountsJsonArray.toJSONString());
                fw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the json file of accounts data as well as the ID increment txt files
     */
    public static void resetAccountsData() {
        try (FileWriter fw = new FileWriter("accountsData.json")) {
            synchronized (accountsDataSync) {
                fw.write("[]");
                fw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("userIDIncrement.txt"))) {
            pw.println("0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("storeIDIncrement.txt"))) {
            pw.println("1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the account with the given ID
     * @param ID ID
     * @return the account with the given ID
     */
    public static Account accountWithID(String ID) {
        Account found = null;   // account looking for
        Account[] accounts = readAccountsData();    // accounts data
        for (Account account : accounts) {
            if (account.ID.equals(ID)) {
                found = account;
                break;
            }
        }
        return found;
    }

    /**
     * Returns the store with the given ID
     * @param ID ID
     * @return the store with the given ID
     */
    public static Store storeWithID(String ID) {
        Store found = null;     // store looking for
        Account[] accounts = readAccountsData();    // accounts data
        for (Account account : accounts) {
            for (Store store : account.stores) {
                if (store.getID().equals(ID)) {
                    found = store;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Returns the username of the user with the given ID
     * @param ID the given ID
     * @return username
     */
    public static String getUsername(String ID) {
        if (accountWithID(ID) == null && storeWithID(ID) == null) {
            return null;
        }
        if (Integer.parseInt(ID) % 2 == 0) {
            return accountWithID(ID).username;
        } else {
            return storeWithID(ID).getName();
        }
    }

    /**
     * Returns the email of the user with the given ID
     * @param ID the given ID
     * @return email
     */
    public static String getEmail(String ID) {
        return accountWithID(ID).email;
    }

    /**
     * Returns the password of the user with the given ID
     * @param ID the given ID
     * @return password
     */
    public static String getPassword(String ID) {
        return accountWithID(ID).password;
    }

    /**
     * Returns the role of the user with the given ID
     * @param ID the given ID
     * @return role
     */
    public static String getRole(String ID) {
        return accountWithID(ID).role;
    }

    /**
     * Returns the stores of the user with the given ID
     * @param ID the given ID
     * @return stores
     */
    public static Store[] getStores(String ID) {
        return accountWithID(ID).stores;
    }

    /**
     * Returns the messages of the user with the given ID
     * @param ID the given ID
     * @return messages
     */
    public static Message[] getMessages(String ID) {
        if (Integer.parseInt(ID) % 2 == 0) {
            return accountWithID(ID).messages;
        } else {
            Message[] messages = accountWithID(Account.toUserID(ID)).messages;  // messages of account
            ArrayList<Message> storeMessagesList = new ArrayList<>();   // list of messages
            for (Message message : messages) {
                if (message.getSenderID().equals(ID) || message.getRecipientID().equals(ID)) {
                    storeMessagesList.add(message);
                }
            }
            Message[] storeMessages = new Message[storeMessagesList.size()];    // array of messages of the store
            for (int i = 0; i < storeMessages.length; i++) {
                storeMessages[i] = storeMessagesList.get(i);
            }
            return storeMessages;
        }
    }

    /**
     * Returns the blocked of the user with the given ID
     * @param ID the given ID
     * @return blocked
     */
    public static String[] getBlocked(String ID) {
        return accountWithID(Account.toUserID(ID)).blocked;
    }

    /**
     * Returns the invisibleTo of the user with the given ID
     * @param ID the given ID
     * @return invisibleTo
     */
    public static String[] getInvisibleTo(String ID) {
        return accountWithID(Account.toUserID(ID)).invisibleTo;
    }

    /**
     * Changes the username of the user with the given ID to the given username
     * @param ID the given ID
     * @param username the given username
     */
    public static void setUsername(String ID, String username) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        account.username = username;
        writeAccountsData(accounts);
    }

    /**
     * Changes the email of the user with the given ID to the given email
     * @param ID the given ID
     * @param email the given username
     */
    public static void setEmail(String ID, String email) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        account.email = email;
        writeAccountsData(accounts);
    }

    /**
     * Changes the password of the user with the given ID to the given password
     * @param ID the given ID
     * @param password the given password
     */
    public static void setPassword(String ID, String password) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        account.password = password;
        writeAccountsData(accounts);
    }

    /**
     * Adds the given blockID to the blocked list of the user with the given ID
     * @param ID the given ID
     * @param blockID the given blockID
     */
    public static void block(String ID, String blockID) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        String[] newBlocked = new String[account.blocked.length + 1];   // new blocked array
        for (int i = 0; i < newBlocked.length - 1; i++) {
            newBlocked[i] = account.blocked[i];
        }
        newBlocked[newBlocked.length - 1] = blockID;
        account.blocked = newBlocked;
        writeAccountsData(accounts);
    }

    /**
     * Removes the given unblockID to the blocked list of the user with the given ID
     * @param ID the given ID
     * @param unblockID the given unblockID
     */
    public static void unblock(String ID, String unblockID) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        ArrayList<String> blockedList = new ArrayList<>();  // blocked list
        for (String blockedID : account.blocked) {
            if (!blockedID.equals(unblockID)) {
                blockedList.add(blockedID);
            }
        }
        String[] newBlocked = new String[blockedList.size()];   // new blocked array
        for (int i = 0; i < newBlocked.length; i++) {
            newBlocked[i] = blockedList.get(i);
        }
        account.blocked = newBlocked;
        writeAccountsData(accounts);
    }

    /**
     * Adds the given invisibleID to the blocked list of the user with the given ID
     * @param ID the given ID
     * @param invisibleID the given invisibleID
     */
    public static void invisible(String ID, String invisibleID) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        String[] newInvisibleTo = new String[account.invisibleTo.length + 1];   // new invisible to array
        for (int i = 0; i < newInvisibleTo.length - 1; i++) {
            newInvisibleTo[i] = account.invisibleTo[i];
        }
        newInvisibleTo[newInvisibleTo.length - 1] = invisibleID;
        account.invisibleTo = newInvisibleTo;
        writeAccountsData(accounts);
    }

    /**
     * Removes the given uninvisible to the invisibleTo list of the user with the given ID
     * @param ID the given ID
     * @param uninvisibleID the given uninvisibleID
     */
    public static void uninvisible(String ID, String uninvisibleID) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        ArrayList<String> invisibleToList = new ArrayList<>();  // invisible to list
        for (String invisibleToID : account.invisibleTo) {
            if (!invisibleToID.equals(uninvisibleID)) {
                invisibleToList.add(invisibleToID);
            }
        }
        String[] newInvisibleTo = new String[invisibleToList.size()];   // new invisible to array
        for (int i = 0; i < newInvisibleTo.length; i++) {
            newInvisibleTo[i] = invisibleToList.get(i);
        }
        account.invisibleTo = newInvisibleTo;
        writeAccountsData(accounts);
    }

    /**
     * Sets the messages list of the user with the given ID to the given messages list
     * @param ID the given ID
     * @param messages the given messages list
     */
    public static void setMessages(String ID, Message[] messages) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
            for (Store store : accounts[i].stores) {
                if (store.getID().equals(ID)) {
                    account = accounts[i];
                }
            }
        }
        account.messages = messages;
        writeAccountsData(accounts);
    }

    /**
     * Sets the stores list of the user with the given ID to the given stores list
     * @param ID the given ID
     * @param stores the given messages list
     */
    public static void setStores(String ID, Store[] stores) {
        Account[] accounts = readAccountsData();    // accounts data
        Account account = null;     // account looking for
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].ID.equals(ID)) {
                account = accounts[i];
                break;
            }
        }
        account.stores = stores;
        writeAccountsData(accounts);
    }

    /**
     * Returns the ID of the user if the given ID is a store ID
     * @param ID the given ID
     * @return the ID of the user
     */
    public static String toUserID(String ID) {
        if (Integer.parseInt(ID) % 2 == 0) {
            return ID;
        } else {
            Account[] accounts = readAccountsData();    // accounts data
            for (Account account : accounts) {
                for (Store store : account.stores) {
                    if (store.getID().equals(ID)) {
                        return account.ID;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the ID of the user with the username
     * @param username the username
     * @return the ID of the user
     */
    public static String IDofUsername(String username) {
        Account[] accounts = readAccountsData();    // accounts data
        for (Account account : accounts) {
            if (account.username.equals(username)) {
                return account.ID;
            }
        }
        return null;
    }

    /**
     * Returns the ID of the store with the username
     * @param storename the storename
     * @return the ID of the user
     */
    public static String IDofStorename(String storename) {
        Account[] accounts = readAccountsData();    // accounts data
        for (Account account : accounts) {
            for (Store store : account.stores) {
                if (store.getName().equals(storename)) {
                    return store.getID();
                }
            }
        }
        return null;
    }

    /**
     * returns true if userOne is blocked by userTwo
     * @param userOneID user one ID
     * @param userTwoID user two ID
     * @return whether userOne is blocked by userTwo
     */
    public static boolean userBlockedByUser(String userOneID, String userTwoID) {
        for (String ID : getBlocked(userTwoID)) {
            if (ID.equals(Account.toUserID(userOneID))) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if userOne can't see userTwo
     * @param userOneID user one ID
     * @param userTwoID user two ID
     * @return whether userOne can see userTwo
     */
    public static boolean userCantSeeUser(String userOneID, String userTwoID) {
        if (getUsername(userTwoID) == null) {
            return false;
        }
        for (String ID : getInvisibleTo(userTwoID)) {
            if (ID.equals(Account.toUserID(userOneID))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the ID of the user with the email
     * @param email the email
     * @return the ID of the user
     */
    public static String IDofEmail(String email) {
        Account[] accounts = readAccountsData();    // accounts data
        for (Account account : accounts) {
            if (account.email.equals(email)) {
                return account.ID;
            }
        }
        return null;
    }

    /**
     * Deletes account with ID
     * @param ID the ID of the account
     */
    public static void deleteAccount(String ID) {
        Account[] accounts = readAccountsData();    // accounts data
        ArrayList<Account> accountsList = new ArrayList<>();    // list of accounts
        for (Account account : accounts) {
            if (!account.ID.equals(ID)) {
                accountsList.add(account);
            }
        }
        Account[] newAccounts = new Account[accountsList.size()];   // new array of accounts
        for (int i = 0; i < newAccounts.length; i++) {
            newAccounts[i] = accountsList.get(i);
        }
        writeAccountsData(newAccounts);
    }

    /**
     * Returns an array of usernames from searching searchString from the perspective of the user with ID
     * @param ID ID of user
     * @param searchString the String searched for
     * @return array of usernames
     */
    public static String[] searchUsernames(String ID, String searchString) {
        Account[] accounts = readAccountsData();    // accounts data
        ArrayList<String> usernamesList = new ArrayList<>();    // list of usernames
        for (Account account : accounts) {
            if (!(account.ID.equals(ID) || Account.userCantSeeUser(ID, account.ID))) {
                if (account.username.toLowerCase().contains(searchString.toLowerCase()) && !account.role.equals(Account.getRole(ID))) {
                    usernamesList.add(account.username);
                }
            }
        }
        String[] usernames = new String[usernamesList.size()];  // array of matching result usernames
        for (int i = 0; i < usernames.length; i++) {
            usernames[i] = usernamesList.get(i);
        }
        return usernames;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getID() {
        return ID;
    }

    public Store[] getStores() {
        return stores;
    }

    public Message[] getMessages() {
        return messages;
    }

    public String[] getBlocked() {
        return blocked;
    }

    public String[] getInvisibleTo() {
        return invisibleTo;
    }

    public static String[] getConversationsWith(String ID) {
        HashSet<String> IDset = new HashSet<>();    // set of IDs they have exchanged messages with
        Message[] messages = Account.getMessages(ID);   // account's messages
        for (Message message : messages) {
            if (ID.equals(message.getSenderID())) {
                if (!Account.userCantSeeUser(Account.toUserID(ID), Account.toUserID(message.getRecipientID()))) {
                    IDset.add(message.getRecipientID());
                }
            } else if (ID.equals(message.getRecipientID())) {
                if (!Account.userCantSeeUser(Account.toUserID(ID), Account.toUserID(message.getSenderID()))) {
                    IDset.add(message.getSenderID());
                }
            }
        }
        ArrayList<String> deletedIDs = new ArrayList<>();   // IDs of accounts who deleted their accounts
        for (String i : IDset) {
            if (Account.getUsername(i) == null) {
                deletedIDs.add(i);
            }
        }
        for (String i : deletedIDs) {
            IDset.remove(i);
        }
        String[] conversationsWith = new String[IDset.size()];  // array of names of conversations
        int index = 0;
        for (String i : IDset) {
            conversationsWith[index] = Account.getUsername(i);
            index++;
        }
        return conversationsWith;
    }
}