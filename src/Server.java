import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private Socket socket;

    public Server(Socket socket) {
        super();
        this.socket = socket;
        start();
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            while (true) {
                String command = reader.readLine();
                if (command.equals("Close Socket")) {
                    socket.close();
                } else if (command.equals("Log In")) {
                    String username = reader.readLine();
                    String password = reader.readLine();
                    String ID = Account.IDofUsername(username);
                    if (ID == null || !Account.getPassword(ID).equals(password)) {
                        writer.println("Failure");
                    } else {
                        writer.println("Success");
                        writer.println(ID);
                        writer.println(Account.getRole(ID));
                    }
                    writer.flush();
                } else if (command.equals("Sign up")) {
                    String username = reader.readLine();
                    String email = reader.readLine();
                    String password = reader.readLine();
                    String role = reader.readLine();
                    if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                        writer.println("Blank");
                    } else if (Account.IDofUsername(username) != null || Account.IDofStorename(username) != null) {
                        writer.println("Username Taken");
                    } else if (Account.IDofEmail(email) != null) {
                        writer.println("Email Taken");
                    } else {
                        Account.createAccount(username, email, password, role);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Account Data")) {
                    String ID = reader.readLine();
                    writer.println(Account.getUsername(ID));
                    writer.println(Account.getEmail(ID));
                    writer.println(Account.getPassword(ID));
                    writer.flush();
                } else if (command.equals("Save Account Data")) {
                    String ID = reader.readLine();
                    String username = reader.readLine();
                    String email = reader.readLine();
                    String password = reader.readLine();
                    String IDofUsername = Account.IDofUsername(username);
                    String IDofEmail = Account.IDofEmail(email);
                    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        writer.println("Blank");
                    } else if (IDofUsername != null && !IDofUsername.equals(ID) || Account.IDofStorename(username) != null) {
                        writer.println("Username Taken");
                    } else if (IDofEmail != null && !IDofEmail.equals(ID)) {
                        writer.println("Email Taken");
                    } else {
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Delete Account")) {
                    String ID = reader.readLine();
                    Account.deleteAccount(ID);
                } else if (command.equals("Customer Dashboard")) {
                    Store[] stores = Store.getStores();
                    writer.println("" + stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                        writer.println(store.getDescription());
                        writer.println(store.getOwner().getUsername());
                    }
                    writer.flush();
                } else if (command.equals("Seller Dashboard")) {
                    String ID = reader.readLine();
                    Store[] stores = Account.getStores(ID);
                    writer.println("" + stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                        writer.println(store.getDescription());
                    }
                    writer.flush();
                } else if (command.equals("Send Message")) {
                    String ID = reader.readLine();
                    String recipient = reader.readLine();
                    String recipientID = Account.IDofStorename(recipient);
                    if (recipientID == null) {
                        recipientID = Account.IDofUsername(recipient);
                    }
                    String message = reader.readLine();
                    if (recipientID == null) {
                        writer.println("Failure");
                    } else if (Account.userBlockedByUser(ID, Account.toUserID(recipientID))) {
                        writer.println("Blocked");
                    } else {
                        Message.createMessage(ID, recipientID, message);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Send Message As Store")) {
                    String storeID = reader.readLine();
                    String recipientID = Account.IDofUsername(reader.readLine());
                    String message = reader.readLine();
                    if (recipientID == null) {
                        writer.println("Failure");
                    } else if (Account.userBlockedByUser(storeID, recipientID)) {
                        writer.println("Blocked");
                    } else {
                        Message.createMessage(storeID, recipientID, message);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Conversation")) {
                    String ID = reader.readLine();
                    String recipient = reader.readLine();
                    String recipientID = Account.IDofUsername(recipient);
                    if (recipientID == null) {
                        recipientID = Account.IDofStorename(recipient);
                    }
                    Message[] messages = Message.getConversationArray(ID, recipientID);
                    writer.println(messages.length);
                    for (Message message : messages) {
                        writer.println(Account.getUsername(message.getSenderID()));
                        writer.println(message.getMessage());
                    }
                    writer.flush();
                } else if (command.equals("Toggle Block")) {
                    String ID = reader.readLine();
                    String block = reader.readLine();
                    String blockID = Account.IDofUsername(block);
                    if (blockID == null) {
                        blockID = Account.toUserID(Account.IDofStorename(block));
                    }
                    if (Account.userBlockedByUser(blockID, ID)) {
                        Account.unblock(ID, blockID);
                        writer.println("Unblocked");
                    } else {
                        Account.block(ID, blockID);
                        writer.println("Blocked");
                    }
                    writer.flush();
                } else if (command.equals("Toggle Invisible")) {
                    String ID = reader.readLine();
                    String invisible = reader.readLine();
                    String invisibleID = Account.IDofUsername(invisible);
                    if (invisibleID == null) {
                        invisibleID = Account.toUserID(Account.IDofStorename(invisible));
                    }
                    if (Account.userCantSeeUser(invisibleID, ID)) {
                        Account.uninvisible(ID, invisibleID);
                        writer.println("Visible");
                    } else {
                        Account.invisible(ID, invisibleID);
                        writer.println("Invisible");
                    }
                    writer.flush();
                } else if (command.equals("Search Users")) {
                    String ID = reader.readLine();
                    String searchString = reader.readLine();
                    String[] usernames = Account.searchUsernames(ID, searchString);
                    writer.println("" + usernames.length);
                    for (String username : usernames) {
                        writer.println(username);
                    }
                    writer.flush();
                } else if (command.equals("List Users")) {
                    String ID = reader.readLine();
                    String[] usernames = Account.searchUsernames(ID, "");
                    writer.println(usernames.length);
                    for (String username : usernames) {
                        writer.println(username);
                    }
                    writer.flush();
                } else if (command.equals("Save Store Data")) {
                    String ID = reader.readLine();
                    String buttonIndex = reader.readLine();
                    String newStoreName = reader.readLine();
                    String newStoreDescription = reader.readLine();
                    Store[] stores = Account.getStores(ID);
                    Store store = stores[Integer.parseInt(buttonIndex)];
                    if (newStoreName.equals(store.getName())) {
                        Store.editStore(store.getID(), newStoreName, newStoreDescription);
                        writer.println("Success");
                    } else if (Store.storeNameExists(newStoreName)) {
                        writer.println("Name Exists");
                    } else if (newStoreName.isEmpty()) {
                        writer.println("Name Blank");
                    } else {
                        Store.editStore(store.getID(), newStoreName, newStoreDescription);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Delete Store")) {
                    String ID = reader.readLine();
                    String buttonIndex = reader.readLine();
                    Store[] stores = Account.getStores(ID);
                    Store store = stores[Integer.parseInt(buttonIndex)];
                    Store.deleteStore(store.getID());
                } else if (command.equals("Create Store")) {
                    String ID = reader.readLine();
                    String newStoreName = reader.readLine();
                    String newStoreDescription = reader.readLine();
                    if (Store.storeNameExists(newStoreName)) {
                        writer.println("Name Exists");
                    } else if (newStoreName.isEmpty()) {
                        writer.println("Name Blank");
                    } else {
                        Store.createStore(ID, newStoreName, newStoreDescription);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Get Conversations")) {
                    String ID = reader.readLine();
                    String[] names = Account.getConversationsWith(ID);
                    writer.println(names.length);
                    for (String name : names) {
                        writer.println(name);
                    }
                    writer.flush();
                } else if (command.equals("Edit Message") || command.equals("Delete Message")) {
                    String ID = reader.readLine();
                    String recipient = reader.readLine();
                    String recipientID = Account.IDofUsername(recipient);
                    if (recipientID == null) {
                        recipientID = Account.IDofStorename(recipient);
                    }
                    int msgIndex = Integer.parseInt(reader.readLine());
                    Message[] conversation = Message.getConversationArray(ID, recipientID);
                    int index = -1;
                    Message selectedMessage = null;
                    for (Message message : conversation) {
                        if (!(ID.equals(message.getSenderID()) && message.isDeletedForSender() ||
                                ID.equals(message.getRecipientID()) && message.isDeletedForRecipient())) {
                            index++;
                        }
                        if (index == msgIndex) {
                            selectedMessage = message;
                            break;
                        }
                    }
                    if (command.equals("Edit Message")) {
                        String newMessage = reader.readLine();
                        Message.editMessage(selectedMessage.getSenderID(), selectedMessage.getRecipientID(), selectedMessage.getOrder(), newMessage);
                    } else {
                        Message.deleteMessage(selectedMessage.getSenderID(), selectedMessage.getRecipientID(), selectedMessage.getOrder());
                    }
                } else if (command.equals("Seller View Options")) {
                    String ID = reader.readLine();
                    writer.println(Account.getUsername(ID));
                    Store[] stores = Account.getStores(ID);
                    writer.println(stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                    }
                    writer.flush();
                } else if (command.equals("Get ID")) {
                    String name = reader.readLine();
                    String ID = Account.IDofUsername(name);
                    if (ID == null) {
                        ID = Account.IDofStorename(name);
                    }
                    writer.println(ID);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Account.resetAccountsData();
        Account.createAccount("Customer1", "customer1@gmail.com", "Customer1", "Customer"); // ID = 0
        Account.createAccount("Seller1", "seller1@gmail.com", "Seller1", "Seller");  // ID = 2
        Account.createAccount("Seller2", "seller2@gmail.com", "Seller2", "Seller");  // ID = 4
        Account.createAccount("Seller3", "seller3@gmail.com", "Seller3", "Seller");  // ID = 6
        Store.createStore("2", "Store1", "Seller1's first store");      // ID = 1
        Store.createStore("2", "Store2", "Seller1's second store");     // ID = 3
        Store.createStore("2", "Store3", "Seller1's third store");      // ID = 5
        Store.createStore("4", "Store4", "Seller2's store");    // ID = 7
        Store.createStore("6", "Store5", "Seller3's store");    // ID = 9
        Message.createMessage("0", "2", "Hello!");
        Message.createMessage("2", "0", "Hi.");
        Message.createMessage("0", "2", "How are you?");
        Message.createMessage("2", "0", "I'm good.");

        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(4242)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Server(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
