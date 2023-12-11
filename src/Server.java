import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server
 *
 * Creates threads that each read inputs from one client, carries out the appropriate tasks,
 * and writes responses to that client accordingly.
 *
 * @author Sean Kim, Soham Paik, Yash Patel, lab sec l17
 *
 * @version December 12, 2023
 */
public class Server extends Thread {
    private Socket socket;  // socket to establish connection between server and client

    public Server(Socket socket) {
        super();
        this.socket = socket;
        start();
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            while (true) {
                String command = reader.readLine();     // the first line the client writes in each exchange
                System.out.println(command);
                if (command.equals("Close Socket")) {
                    socket.close();
                } else if (command.equals("Log In")) {
                    String username = reader.readLine();    // username
                    String password = reader.readLine();    // password
                    String ID = Account.IDofUsername(username);     // ID of the username
                    if (ID == null || !Account.getPassword(ID).equals(password)) {
                        writer.println("Failure");
                    } else {
                        writer.println("Success");
                        writer.println(ID);
                        writer.println(Account.getRole(ID));
                    }
                    writer.flush();
                } else if (command.equals("Sign up")) {
                    String username = reader.readLine();    // username
                    String email = reader.readLine();       // email
                    String password = reader.readLine();    // password
                    String role = reader.readLine();        // role
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
                    String ID = reader.readLine();  // ID
                    writer.println(Account.getUsername(ID));
                    writer.println(Account.getEmail(ID));
                    writer.println(Account.getPassword(ID));
                    writer.flush();
                } else if (command.equals("Save Account Data")) {
                    String ID = reader.readLine();  // ID
                    String username = reader.readLine();    // username
                    String email = reader.readLine();   // email
                    String password = reader.readLine();    // password
                    String IDofUsername = Account.IDofUsername(username);   // to check for repeat
                    String IDofEmail = Account.IDofEmail(email);    // to check for repeat
                    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        writer.println("Blank");
                    } else if (IDofUsername != null && !IDofUsername.equals(ID) || Account.IDofStorename(username) != null) {
                        writer.println("Username Taken");
                    } else if (IDofEmail != null && !IDofEmail.equals(ID)) {
                        writer.println("Email Taken");
                    } else {
                        Account.setUsername(ID, username);
                        Account.setEmail(ID, email);
                        Account.setPassword(ID, password);
                        writer.println("Success");
                    }
                    writer.flush();
                } else if (command.equals("Delete Account")) {
                    String ID = reader.readLine();  // ID
                    Account.deleteAccount(ID);
                } else if (command.equals("Customer Dashboard")) {
                    Store[] stores = Store.getStores();     // all stores
                    writer.println("" + stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                        writer.println(store.getDescription());
                        writer.println(store.getOwner().getUsername());
                    }
                    writer.flush();
                } else if (command.equals("Seller Dashboard")) {
                    String ID = reader.readLine();  // ID
                    Store[] stores = Account.getStores(ID);     // all stores owned by this account
                    writer.println("" + stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                        writer.println(store.getDescription());
                    }
                    writer.flush();
                } else if (command.equals("Send Message")) {
                    String ID = reader.readLine();  // ID
                    String recipient = reader.readLine();   // recipient name
                    String recipientID = Account.IDofStorename(recipient);  // ID of the recipient
                    if (recipientID == null) {
                        recipientID = Account.IDofUsername(recipient);
                    }
                    String message = reader.readLine();     // message
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
                    String storeID = reader.readLine();     // ID of store
                    String recipientID = Account.IDofUsername(reader.readLine());   // ID of recipient
                    String message = reader.readLine();     // message
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
                    String ID = reader.readLine();  // ID
                    String recipient = reader.readLine();   // recipient name
                    String recipientID = Account.IDofUsername(recipient);   // recipient ID
                    if (recipientID == null) {
                        recipientID = Account.IDofStorename(recipient);
                    }
                    Message[] messages = Message.getConversationArray(ID, recipientID);     // all message between them
                    writer.println(messages.length);
                    for (Message message : messages) {
                        writer.println(Account.getUsername(message.getSenderID()));
                        writer.println(message.getMessage());
                    }
                    writer.flush();
                } else if (command.equals("Toggle Block")) {
                    String ID = reader.readLine();  // ID
                    String block = reader.readLine();   // name to block
                    String blockID = Account.IDofUsername(block);   // ID to block
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
                    String ID = reader.readLine();  // ID
                    String invisible = reader.readLine();   // name to be invisible to
                    if (!invisible.isEmpty()) {
                        String invisibleID = Account.IDofUsername(invisible);   // ID to be invisible to
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
                    } else {
                        writer.println("");
                    }
                    writer.flush();
                } else if (command.equals("Search Users")) {
                    String ID = reader.readLine();  // ID
                    String searchString = reader.readLine();    // text searched
                    String[] usernames = Account.searchUsernames(ID, searchString);  // all usernames matching search
                    writer.println("" + usernames.length);
                    for (String username : usernames) {
                        writer.println(username);
                    }
                    writer.flush();
                } else if (command.equals("Save Store Data")) {
                    String ID = reader.readLine();  // ID
                    String buttonIndex = reader.readLine();     // index of button
                    String newStoreName = reader.readLine();    // new store name
                    String newStoreDescription = reader.readLine();     // new store description
                    Store[] stores = Account.getStores(ID);     // all stores of the account
                    Store store = stores[Integer.parseInt(buttonIndex)];    // the store referred to be the button
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
                    String ID = reader.readLine();  // ID
                    String buttonIndex = reader.readLine();     // index of button
                    Store[] stores = Account.getStores(ID);     // all stores of the account
                    Store store = stores[Integer.parseInt(buttonIndex)];    // the store referred to by the button
                    Store.deleteStore(store.getID());
                } else if (command.equals("Create Store")) {
                    String ID = reader.readLine();  // ID
                    String newStoreName = reader.readLine();    // new store name
                    String newStoreDescription = reader.readLine();     // new store description
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
                    String ID = reader.readLine();  // ID
                    String[] names = Account.getConversationsWith(ID);  // names with whom they exchanged messages
                    writer.println(names.length);
                    for (String name : names) {
                        writer.println(name);
                    }
                    writer.flush();
                } else if (command.equals("Edit Message") || command.equals("Delete Message")) {
                    String ID = reader.readLine();  // ID
                    String recipient = reader.readLine();   // recipient name
                    String recipientID = Account.IDofUsername(recipient);   // recipient ID
                    if (recipientID == null) {
                        recipientID = Account.IDofStorename(recipient);
                    }
                    int msgIndex = Integer.parseInt(reader.readLine());     // index of selected message
                    Message[] conversation = Message.getConversationArray(ID, recipientID);     // all messages between
                    int index = -1;     // index counter
                    Message selectedMessage = null;     // the specific message selected
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
                        String newMessage = reader.readLine();  // new message
                        Message.editMessage(selectedMessage.getSenderID(), selectedMessage.getRecipientID(), selectedMessage.getOrder(), newMessage);
                    } else {
                        Message.deleteMessage(selectedMessage.getSenderID(), selectedMessage.getRecipientID(), selectedMessage.getOrder());
                    }
                } else if (command.equals("Seller View Options")) {
                    String ID = reader.readLine();  // ID
                    writer.println(Account.getUsername(ID));
                    Store[] stores = Account.getStores(ID);     // all stores of the account
                    writer.println(stores.length);
                    for (Store store : stores) {
                        writer.println(store.getName());
                    }
                    writer.flush();
                } else if (command.equals("Get ID")) {
                    String name = reader.readLine();    // name
                    String ID = Account.IDofUsername(name);     // ID
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
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(4242)) {
                while (true) {
                    Socket socket = serverSocket.accept();  // connection to client
                    new Server(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
