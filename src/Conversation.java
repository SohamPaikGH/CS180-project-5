import java.io.*;
import java.util.*;

/**
 * Conversation
 *
 * Stores and manages conversation data between users
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
public class Conversation {

    private String seller;      // the seller in the conversation
    private String customer;    // the customer in the conversation
    private File conversation;  // the file containing the conversation

    /**
     * Instantiates a new Conversation with the given fields
     *
     * @param seller the seller in the conversation
     * @param customer the customer in the conversation
     */
    public Conversation(String seller, String customer) {
        this.seller = seller;
        this.customer = customer;

        setConversation();

    }

    /**
     * Returns the seller
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets the seller
     *
     * @param seller the seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Returns the customer
     *
     * @return the customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Sets the customer
     *
     * @param customer the customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * Sends a message from the sender
     * Messages are formatted with two digits,sender: message
     * The first digit is 1 if it should be visible to the Seller, 0 if not
     * The second digit is 1 if it should be visible to the Customer, 0 if not
     * Both are 1 by default
     *
     * @param sender user who sends the message
     * @param message the message
     */
    public void sendMessage(String sender, String message) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(conversation, true))) {
            String output = String.format("11,%s: %s", sender, message);    // the message line to write to file
            writer.println(output);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the conversation file name
     */
    public void setConversation() {
        ArrayList<String[]> usersData = UsersData.readUsersData();   // reads the usersData.txt into an arraylist
        String sellerID = usersData.get(UsersData.indexOfUsername(seller, usersData))[4];   // id of the seller
        String customerID = usersData.get(UsersData.indexOfUsername(customer, usersData))[4];   // id of the customer
        String filename = String.format("%s-%s.txt", sellerID, customerID); // filename of the conversation
        File f = new File(filename);    // the file containing the conversation
        this.conversation = f;

        // this portion adds the conversation to conversationsData.txt if it doesn't already exist
        ArrayList<String[]> conversationsData = readConversationsData();
        // reads conversationsData.txt into an arraylist
        int sellerIndex = UsersData.indexOfUsername(seller, usersData);      // index of seller
        int customerIndex = UsersData.indexOfUsername(customer, usersData);  // index of customer
        boolean sellerHasConversation = Arrays.asList(conversationsData.get(sellerIndex)).contains(filename);
        // if it exists in the seller's data
        boolean customerHasConversation = Arrays.asList(conversationsData.get(customerIndex)).contains(filename);
        // if it exists in the customer's data
        if (!sellerHasConversation || !customerHasConversation) {
            String newFile = "";    // the new contents of the file to replace the old contents
            try (BufferedReader bfr = new BufferedReader(new FileReader("conversationsData.txt"))) {
                String line = bfr.readLine();   // line being read
                int index = 0;  // line number being read
                while (line != null) {
                    if (!sellerHasConversation && index == sellerIndex ||
                            !customerHasConversation && index == customerIndex) {
                        newFile += line + filename + ",\n";
                    } else {
                        newFile += line + "\n";
                    }
                    line = bfr.readLine();
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (PrintWriter pw = new PrintWriter(new FileOutputStream("conversationsData.txt"))) {
                pw.print(newFile);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes the lineNumber-th message sent by the sender in the conversation stored in filename
     *
     * @param filename name of conversation file
     * @param lineNumber lineNumber-th message by sender will be deleted
     * @param sender specifies the sender
     */
    public static void deleteMessage(String filename, int lineNumber, String sender, String role) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(filename))) {
            String newFile = "";    // new file to write
            String line = bfr.readLine();   // line being read
            int lineIndex = 0;      // line number out of messages sent by sender
            int indicatorIndex;     // at which index of the line in the file to see if the message is shown or not
            if (role.equals("Seller")) {
                indicatorIndex = 0;
            } else {
                indicatorIndex = 1;
            }
            while (line != null) {
                if (line.charAt(indicatorIndex) == '1' && line.substring(3, line.indexOf(":")).equals(sender)) {
                    if (lineIndex == lineNumber) {
                        newFile += line.substring(0, indicatorIndex) + "0" +
                                line.substring(indicatorIndex + 1) + "\n";
                    } else {
                        newFile += line + "\n";
                    }
                    lineIndex++;
                } else {
                    newFile += line + "\n";
                }
                line = bfr.readLine();
            }
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(filename))) {
                pw.print(newFile);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces the lineNumber-th message sent by the sender in the conversation stored in filename with message
     *
     * @param filename name of conversation file
     * @param lineNumber lineNumber-th message by sender will be replaced
     * @param sender specifies the sender
     * @param message the new message to replace the old message
     */
    public static void editMessage(String filename, int lineNumber, String sender, String role, String message) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(filename))) {
            String newFile = "";    // new file to write
            String line = bfr.readLine();   // line being read
            int lineIndex = 0;  // line number out of messages sent by sender
            int indicatorIndex;     // at which index of the line in the file to see if the message is shown or not
            if (role.equals("Seller")) {
                indicatorIndex = 0;
            } else {
                indicatorIndex = 1;
            }
            while (line != null) {
                if (line.charAt(indicatorIndex) == '1' && line.substring(3, line.indexOf(":")).equals(sender)) {
                    if (lineIndex == lineNumber) {
                        newFile += line.substring(0, line.indexOf(":")) + ": " + message + "\n";
                    } else {
                        newFile += line + "\n";
                    }
                    lineIndex++;
                } else {
                    newFile += line + "\n";
                }
                line = bfr.readLine();
            }
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(filename))) {
                pw.print(newFile);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * prints the conversation stored in filename
     *
     * @param filename file of the conversation
     */
    public static void printConversation(String filename, String role) {
        ArrayList<String> conversationLines = new ArrayList<>();    // the lines to print
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();    // line being read
            int indicatorIndex;     // at which index of the line in the file to see if the message is shown or not
            if (role.equals("Seller")) {
                indicatorIndex = 0;
            } else {
                indicatorIndex = 1;
            }
            while ( line != null ) {
                if (line.charAt(indicatorIndex) == '1') {
                    conversationLines.add(line.substring(3));
                }
                line = reader.readLine();
            }

            for (String conversationLine: conversationLines) {
                System.out.println(conversationLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * prints the conversation stored in filename, but each of the sender's message is numbered
     *
     * @param filename file of the conversation
     * @param sender specifies the sender
     */
    public static int printNumberedConversation(String filename, String sender, String role) {
        ArrayList<String> conversationLines = new ArrayList<>();    // the lines to print
        int index = 1;  // increments by sender's visible messages
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();    // line being read
            int indicatorIndex;     // at which index of the line in the file to see if the message is shown or not
            if (role.equals("Seller")) {
                indicatorIndex = 0;
            } else {
                indicatorIndex = 1;
            }
            while ( line != null ) {
                if (line.charAt(indicatorIndex) == '1') {
                    if (line.substring(3, line.indexOf(":")).equals(sender)) {
                        conversationLines.add("[" + index + "] " + line.substring(3));
                        index++;
                    } else {
                        conversationLines.add(line.substring(3));
                    }
                }
                line = reader.readLine();
            }

            for (String conversationLine: conversationLines) {
                System.out.println(conversationLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return index - 1;
    }

    /**
     * Reads conversationsData.txt and returns an ArrayList of String arrays representing the data
     *
     * @return an ArrayList of String arrays representing the data
     */
    public static ArrayList<String[]> readConversationsData() {
        ArrayList<String[]> conversationsData = new ArrayList<>();  // the arraylist to contain the data
        try (BufferedReader bfr = new BufferedReader(new FileReader("conversationsData.txt"))) {
            String line = bfr.readLine();   // line of the file being read
            while (line != null) {
                conversationsData.add(line.split(","));
                line = bfr.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conversationsData;
    }

    public List<String> getAllMessages() throws IOException {
        List<String> messages = new ArrayList<>();  // the arraylist of messages

        try (BufferedReader reader = new BufferedReader(new FileReader("conversationsData.txt"))) {
            String line = reader.readLine();

            while (line != null) {
                messages.add(line);
                line = reader.readLine();
            }
        }

        return messages;
    }
}
