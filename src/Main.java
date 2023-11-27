import java.io.*;
import java.util.*;

/**
 * Main
 * 
 * Initializes application and handles all the prompts and user inputs
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  // scanner to handle inputs
        String input = "";  // will be used to store user inputs
        System.out.println("Welcome to Program!");
        ArrayList<String[]> usersData = UsersData.readUsersData();  // ArrayList of the users data
        Account account;  // account the user logs in to

        while (true) {
            System.out.println("[1] Log in\n[2] Sign up");
            input = scanner.nextLine();

            if (input.equals("1")) {  // log in option
                int userIndex = -1;  // the line that the user's data is on; -1 if the user isn't found
                while (userIndex == -1) {  // prompt username
                    System.out.print("Username: ");
                    input = scanner.nextLine();  // input is username
                    userIndex = UsersData.indexOfUsername(input, usersData);
                    if (userIndex == -1) {  // if it couldn't find username
                        System.out.println("Username does not exist; please enter a valid username.");
                    }
                }
                account = new Account(usersData.get(userIndex)[0], usersData.get(userIndex)[1],
                        usersData.get(userIndex)[2], usersData.get(userIndex)[3], usersData.get(userIndex)[4],
                        userIndex, UsersData.createBlockedList(userIndex), UsersData.createInvisibleList(userIndex));
                // sets account info
                while (true) {  // prompt password
                    System.out.print("Password: ");
                    input = scanner.nextLine();
                    if (input.equals(account.getPassword())) {
                        break;
                    }
                    System.out.println("Incorrect password; try again.");
                }
                break;
            } else if (input.equals("2")) {  // sign in option
                String email;  // user's email
                String username;  // user's username
                String password;  // user's password
                String role;  // user's role

                while (true) {  // prompt email
                    System.out.print("Enter email address: ");
                    email = scanner.nextLine();  // input is email address
                    if (UsersData.indexOfEmail(email, usersData) != -1) {  // if email already exists
                        System.out.println("Email address is already in use.");
                    } else {
                        if (email.isEmpty()) {
                            System.out.println("Email address cannot be empty.");
                        } else if (email.contains(",")) {  // email can't contain comma
                            System.out.println("Email address cannot contain comma.");
                        } else {
                            break;
                        }
                    }
                }

                while (true) {  // prompt username
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();  // input is username
                    if (UsersData.indexOfUsername(username, usersData) != -1) {  // if username already exists
                        System.out.println("Username is already taken.");
                    } else {
                        if (username.isEmpty()) {
                            System.out.println("Username cannot be empty.");
                        } else if (username.contains(",") || username.contains(":")) {  // username can't contain comma
                            System.out.println("Username cannot contain comma or colon.");
                        } else {
                            break;
                        }
                    }
                }

                while (true) {  // prompt password
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();  // input is password
                    if (password.isEmpty()) {
                        System.out.println("Password cannot be empty.");
                    } else if (password.contains(",")) {  // password can't contain comma
                        System.out.println("Password cannot contain comma.");
                    } else {
                        break;
                    }
                }

                while (true) {  // prompts user if they are customer or seller
                    System.out.println("Are you a Customer or Seller?" +
                            "\n[1] Customer" +
                            "\n[2] Seller");
                    input = scanner.nextLine();
                    try {  // parses user input as integer
                        if (Integer.parseInt(input) == 1) {
                            role = "Customer";
                            break;
                        } else if (Integer.parseInt(input) == 2) {
                            role = "Seller";
                            break;
                        } else {
                            System.out.println("Please enter one of the options.");
                        }
                    } catch (Exception e) {  // prints error and asks prompt again
                        System.out.println("Please enter one of the options.");
                    }
                }
                String userID = "";
                try (BufferedReader bfr = new BufferedReader(new FileReader("userIDIncrement.txt"))) {
                    userID = bfr.readLine();
                    try (PrintWriter pw = new PrintWriter("userIDIncrement.txt")) {
                        pw.println("" + (Integer.parseInt(userID) + 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UsersData.addUser(email, username, password, role, userID);

                account = new Account(email, username, password, role, userID, usersData.size(),
                        new ArrayList<String>(), new ArrayList<String>());
                usersData = UsersData.readUsersData();  // update usersData after adding the new user
                break;
            } else {
                System.out.println("Please enter a valid option.");
            }
        }

         // at this point the user has logged in
        while (true) {
            System.out.println("Main Menu:");
            System.out.println("[1] Conversations");
            System.out.println("[2] Statistics");
            System.out.println("[3] Stores");
            System.out.println("[4] Blacklist");
            System.out.println("[5] Account Settings");
            System.out.println("[6] Exit Program");
            input = scanner.nextLine();
            if (input.equals("1")) {  // conversations option
                while (true) {
                    System.out.println("Conversations");
                    System.out.println("[1] View Conversations");
                    System.out.println("[2] Edit Conversations");
                    System.out.println("[3] Message someone");
                    System.out.println("[4] Back to Main Menu");
                    input = scanner.nextLine();
                    if (input.equals("1") || input.equals("2")) {  // view conversations or edit conversations option
                        ArrayList<String[]> conversationsData = Conversation.readConversationsData();  // data of convos
                        if (conversationsData.get(account.getUserIndex())[0].isEmpty()) {
                            System.out.println("No conversations.");
                        } else {
                            System.out.println("List of Conversations");
                            ArrayList<String> recipients = new ArrayList<>();  // list of the recipients of each convo
                            if (account.getRole().equals("Customer")) {
                                for (String textFile : conversationsData.get(account.getUserIndex())) {
                                    int recipientIndex = UsersData.indexOfUserID(textFile.substring(0,
                                            textFile.indexOf("-")), usersData);
                                    // index in usersData of recipient
                                    recipients.add(usersData.get(recipientIndex)[1]);
                                }
                            } else {
                                for (String textFile : conversationsData.get(account.getUserIndex())) {
                                    int recipientIndex = UsersData.indexOfUserID(
                                            textFile.substring(textFile.indexOf("-") + 1, textFile.indexOf(".")),
                                            usersData);  // index in usersData of recipient
                                    recipients.add(usersData.get(recipientIndex)[1]);
                                }
                            }
                            int inputThreshold = printOptions(recipients);  // max valid number for input
                            if (input.equals("1")) {  // view conversation
                                while (true) {
                                    input = scanner.nextLine();
                                    try {
                                        if (Integer.parseInt(input) > 0 && Integer.parseInt(input) <= inputThreshold) {
                                            Conversation.printConversation(conversationsData.get(
                                                    account.getUserIndex())[Integer.parseInt(input) - 1],
                                                    account.getRole());
                                            break;
                                        } else {
                                            System.out.println("Please enter a valid option.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Please enter a valid option.");
                                    }
                                }
                            } else {  // edit conversation
                                while (true) {
                                    input = scanner.nextLine();
                                    try {
                                        if (Integer.parseInt(input) > 0 && Integer.parseInt(input) <= inputThreshold) {
                                            String textFile = conversationsData.get(
                                                    account.getUserIndex())[Integer.parseInt(input) - 1];
                                            // filename of selected convo
                                            System.out.println("Select message to edit");
                                            inputThreshold = Conversation.printNumberedConversation(textFile,
                                                    account.getUsername(), account.getRole());
                                            while (true) {
                                                input = scanner.nextLine();
                                                try {
                                                    if (Integer.parseInt(input) > 0
                                                            && Integer.parseInt(input) <= inputThreshold) {
                                                        int index = Integer.parseInt(input) - 1;
                                                        // number of the message
                                                        while (true) {
                                                            System.out.println("[1] Edit message");
                                                            System.out.println("[2] Delete message");
                                                            input = scanner.nextLine();
                                                            if (input.equals("1")) {  // edit message option
                                                                System.out.print("Enter new message: ");
                                                                input = scanner.nextLine();
                                                                Conversation.editMessage(textFile, index,
                                                                        account.getUsername(),
                                                                        account.getRole(), input);
                                                                System.out.println("Message edited.");
                                                                break;
                                                            } else if (input.equals("2")) {  // delete message option
                                                                Conversation.deleteMessage(textFile, index,
                                                                        account.getUsername(),
                                                                        account.getRole());
                                                                System.out.println("Message deleted.");
                                                                break;
                                                            } else {
                                                                System.out.println("Please enter a valid option.");
                                                            }
                                                        }
                                                        break;
                                                    } else {
                                                        System.out.println("Please enter a valid option.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Please enter a valid option.");
                                                }
                                            }
                                            break;
                                        } else {
                                            System.out.println("Please enter a valid option.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Please enter a valid option.");
                                    }
                                }
                            }
                        }
                        break;
                    } else if (input.equals("3")) {  // message someone option
                        String recipient;  // user who will receive the message
                        int recipientIndex;  // index of the recipient
                        boolean userBlocked = false;
                        while (true) {
                            System.out.print("Enter user who you want to message: ");
                            recipient = scanner.nextLine();
                            recipientIndex = UsersData.indexOfUsername(recipient, usersData);
                            if (recipientIndex == -1) {
                                System.out.println("User does not exist.");
                            } else if (account.getRole().equals(usersData.get(recipientIndex)[3])) {
                                // if the recipient is the same role
                                if (account.getRole().equals("Customer")) {
                                    System.out.println("This user is a Customer; you can only message Sellers.");
                                } else {
                                    System.out.println("This user is a Seller; you can only message Customers.");
                                }
                            } else {
                                ArrayList<String> blockedList = UsersData.createBlockedList(recipientIndex);
                                ArrayList<String> invisibleList = UsersData.createInvisibleList(recipientIndex);
                                if (invisibleList.contains(account.getUsername())) {
                                    System.out.println("User does not exist.");
                                    userBlocked = true;
                                } else if (blockedList.contains(account.getUsername())) {
                                    System.out.println("This user has blocked you; you may not message them.");
                                    userBlocked = true;
                                }
                                break;
                            }
                        }
                        if (userBlocked) {
                            break;
                        }
                        System.out.print("Enter your message: ");
                        String message = scanner.nextLine();  // input is message
                        Conversation conversation;
                        if (account.getRole().equals("Seller")) {
                            conversation = new Conversation(account.getUsername(), recipient);
                        } else {
                            conversation = new Conversation(recipient, account.getUsername());
                        }
                        conversation.sendMessage(account.getUsername(), message);
                        break;
                    } else {
                        System.out.println("Please enter a valid option.");
                    }
                }

            } else if (input.equals("2")) { // user statistics option
                if (account.getRole().equals("Seller")) { // checks if user is seller
                    System.out.println("Your Stores:");
                    Stores.printMyStores(account.getUserID()); // prints all stores owned by seller
                    while (true) {
                        System.out.println("[1] Get Statistics for a Store");
                        System.out.println("[2] Return to Main Menu");
                        input = scanner.nextLine();


                        if (input.equals("1")) { // opens statistics menu for stores
                            while (true) {
                                System.out.println("Enter store name : "); // seller inputs store name
                                input = scanner.nextLine();
                                boolean storeAlreadyExists = Stores.verifyIfAlreadyExistingStoreExists(input);
                                if (!storeAlreadyExists) { // tells user if the store name they entered doesn't exist
                                    System.out.println("There is no store called " + input);
                                    break;
                                } else { // checks if the store name entered is valid
                                    System.out.println("How would you like to sort the dashboard?");
                                    System.out.println("[1] Ascending Order");
                                    System.out.println("[2] Descending Order");
                                    String order = scanner.nextLine(); // user can choose how to sort the statistics
                                    boolean ascending;
                                    if (order.equals("1")) {
                                        // will print number of messages sent and top 3 common words
                                        // in ascending order
                                        ascending = true;
                                        Stores.displayStoreStatistics(account.getUserID(), input, ascending);
                                        break;
                                    } else if (order.equals("2")) {
                                        // will print number of messages sent and top 3 common words
                                        // in descending order
                                        ascending = false;
                                        Stores.displayStoreStatistics(account.getUserID(), input, ascending);
                                        break;
                                    } else { // checks if user entered a valid option for sorting
                                        System.out.println("Please enter a valid option!");
                                    }
                                }
                            }
                        } else if (input.equals("2")) { // goes back to main menu
                            break;
                        } else { // repeats prompt until user chooses a valid option
                            continue;
                        }
                    }
                } else {
                    boolean ascending;
                    while(true) {
                        System.out.println("How would you like to sort the dashboard?");
                        System.out.println("[1] Ascending Order");
                        System.out.println("[2] Descending Order");
                        String order = scanner.nextLine(); // user can choose how to sort the statistics

                        if (order.equals("1")) {
                            ascending = true;
                            break;
                        } else if (order.equals("2")) {
                            ascending = false;
                            break;
                        } else {
                            System.out.println("Please select a valid option!");
                        }

                    }

                    System.out.println("Messages Sent:");
                    // prints list of stores by number of messages sent
                    Stores.getMessagesSent(account.getUsername(), ascending);
                    // prints list of stores by number of messages received
                    System.out.println("\nMessages Received:");
                    Stores.getMessagesReceived(account.getUserID(), account.getUsername(), ascending);
                    System.out.println();

                }
            } else if (input.equals("3")) {  // stores option

                if (account.getRole().equals("Seller")) {  // if user is a seller
                    while (true) {
                        // Prints options for user
                        System.out.println("[1] View Stores");
                        System.out.println("[2] Add Store");
                        System.out.println("[3] Delete Store");
                        System.out.println("[4] Edit Store");
                        System.out.println("[5] Return to Main Menu");
                        input = scanner.nextLine();

                        if (input.equals("1")) {  // prints user's stores
                            System.out.println("Your Stores:");
                            Stores.printMyStores(account.getUserID());  // Calls Stores class to print all user's stores
                        } else if (input.equals("2")) {  // adds a specific store
                            while (true) {
                                System.out.println("Enter store name: ");
                                input = scanner.nextLine();
                                // checks if the store already exists
                                boolean storeAlreadyExists = Stores.verifyIfAlreadyExistingStoreExists(input);
                                if (input.contains(",")) {  // prevents user from entering commas
                                    System.out.println("The store name cannot have any commas!");
                                } else {  // if store doesn't exist, it is added to seller's stores file
                                    if (!storeAlreadyExists) {
                                        Stores.addStore(account.getUserID(), input);  // calls stores class to add store
                                        break;
                                    } else {  // if store does exist, the store is not added
                                        System.out.println("Store already exists!");
                                        break;
                                    }
                                }
                            }
                        } else if (input.equals("3")) {  // deletes specific store
                            while (true) {
                                System.out.println("Enter store name: ");
                                input = scanner.nextLine();
                                if (input.contains(",")) {  // prevents user from entering names with commas
                                    System.out.println("The store name cannot have any commas!");
                                } else {
                                    Stores.removeStore(account.getUserID(), input);  // calls Stores class to delete
                                    // store
                                    break;
                                }
                            }
                        } else if (input.equals("4")) {  // allows user to edit names of pre-existing stores
                            while (true) {
                                System.out.println("Enter store name: ");
                                String originalStoreName = scanner.nextLine();
                                if (originalStoreName.contains(",")) {  // user cannot enter commas
                                    System.out.println("The store name cannot have any commas!");
                                } else {
                                    System.out.println("Enter new store name: ");
                                    String newStoreName = scanner.nextLine();
                                    // Verifies that no store with the new name exists
                                    boolean storeAlreadyExists = Stores
                                            .verifyIfAlreadyExistingStoreExists(newStoreName);
                                    if (!storeAlreadyExists) {
                                        // if there are no stores with the new name, then the original store is changed
                                        Stores.editStore(account.getUserID(), originalStoreName, newStoreName);
                                        break;
                                    } else {
                                        System.out.println("Store already exists!");
                                        break;
                                    }
                                }
                            }
                        } else if (input.equals("5")) {  // exits program
                            break;
                        } else {  // tells user to enter valid input
                            System.out.println("Please enter valid input!");
                        }
                    }

                } else {  // if user is a customer
                    while (true) {
                        System.out.println("[1] View all stores");
                        System.out.println("[2] Contact store");
                        System.out.println("[3] Exit");
                        input = scanner.nextLine();

                        if (input.equals("1")) {  // prints all stores
                            Stores.printAllStores();  // calls Stores class
                        } else if (input.equals("2")) {  // contacts store
                            System.out.println("Enter store you want to contact: ");
                            String storeName = scanner.nextLine();
                            // Verifies store exists
                            boolean storeExists = Stores.verifyIfAlreadyExistingStoreExists(storeName);
                            if (storeExists) {
                                // if the store user wants to contact exists,
                                // then the user can send the message
                                System.out.println("Enter your message: ");
                                String message = scanner.nextLine();
                                Stores.contactStore(account.getUsername(), storeName, message);
                                break;
                            } else {  // if the store doesn't exist, the user is told the store was not found
                                System.out.println("Store not found!");
                            }
                        } else if (input.equals("3")) {  // exits the stores menu
                            break;
                        } else {  // tells the user to enter a valid option if they enter invalid input
                            System.out.println("Please enter a valid option.");
                        }

                    }

                }
            } else if (input.equals("4")) {  // blacklist option
                System.out.println("[1] Block user");
                System.out.println("[2] Become invisible to user");
                while (true) {
                    input = scanner.nextLine();
                    if (input.equals("1")) {  // block user option
                        System.out.println("Enter the username of the user you would like to block");
                        while (true) {
                            String username = scanner.nextLine();  // user to block
                            int userIndex = UsersData.indexOfUsername(username, usersData);  // index of user to block
                            if (userIndex == -1) {
                                System.out.println("User does not exist. Please enter a valid user:");
                            } else {
                                UsersData.addBlockedUser(account.getUserIndex(), username, 0);
                                break;
                            }
                        }
                        break;
                    } else if (input.equals("2")) {  // become invisible to user option
                        System.out.println("Enter the username of the user you would like to become invisible to:");
                        while (true) {
                            String username = scanner.nextLine();  // user to become invisible to
                            int userIndex = UsersData.indexOfUsername(username, usersData);
                            // index of user to become invisible to
                            if (userIndex == -1) {
                                System.out.println("User does not exist. Please enter a valid user:");
                            } else {
                                UsersData.addBlockedUser(account.getUserIndex(), username, 1);
                                break;
                            }
                        }
                        break;
                    } else {
                        System.out.println("Please enter valid input:");
                    }
                }

            } else if (input.equals("5")) {  // account settings option
                while (true) {
                    System.out.println("Account Settings");
                    System.out.println("[1] Change Username");
                    System.out.println("[2] Change Password");
                    System.out.println("[3] Change Email Address");
                    System.out.println("[4] Delete Account");
                    System.out.println("[5] Return to Main Menu");
                    input = scanner.nextLine();
                    if (input.equals("1")) {  // edit username option
                        while (true) {
                            System.out.print("Enter new username: ");
                            input = scanner.nextLine();  // input is username
                            if (UsersData.indexOfUsername(input, usersData) != -1) {  // if username already exists
                                System.out.println("Username is already taken.");
                            } else {
                                if (input.isEmpty()) {
                                    System.out.println("Username cannot be empty.");
                                } else if (input.contains(",")) {  // username can't contain comma
                                    System.out.println("Username cannot contain comma.");
                                } else {
                                    account.setUsername(input);
                                    usersData = UsersData.readUsersData();  // update usersData
                                    System.out.println("Username updated.");
                                    break;
                                }
                            }
                        }
                    } else if (input.equals("2")) {  // edit password option
                        while (true) {
                            System.out.print("Enter new password: ");
                            input = scanner.nextLine();  // input is password
                            if (input.isEmpty()) {
                                System.out.println("Password cannot be empty.");
                            } else if (input.contains(",")) {  // password can't contain comma
                                System.out.println("Password cannot contain comma.");
                            } else {
                                account.setPassword(input);
                                usersData = UsersData.readUsersData();  // update usersData
                                System.out.println("Password updated.");
                                break;
                            }
                        }
                    } else if (input.equals("3")) {  // change email address option
                        while (true) {
                            System.out.print("Enter new email address: ");
                            input = scanner.nextLine();  // input is email address
                            if (UsersData.indexOfEmail(input, usersData) != -1) {  // if email already exists
                                System.out.println("Email address is already in use.");
                            } else {
                                if (input.isEmpty()) {
                                    System.out.println("Email address cannot be empty.");
                                } else if (input.contains(",")) {  // username can't contain comma
                                    System.out.println("Email address cannot contain comma.");
                                } else {
                                    account.setEmail(input);
                                    usersData = UsersData.readUsersData();  // update usersData
                                    System.out.println("Email address updated.");
                                    break;
                                }
                            }
                        }
                    } else if (input.equals("4")) {  // delete account option
                        System.out.println("Enter password to confirm account deletion.");
                        input = scanner.nextLine();
                        if (input.equals(account.getPassword())) {
                            UsersData.deleteUser(account.getUserIndex());
                            usersData = UsersData.readUsersData();  // update usersData
                            System.out.println("Account has been deleted.");
                            return;
                        }
                    } else if (input.equals("5")) {  // return to main menu option
                        break;
                    } else {
                        System.out.println("Please enter a valid option.");
                    }
                }
            } else if (input.equals("6")) {  // exit program option
                System.out.println("Thank you for using Program.");
                break;
            } else {
                System.out.println("Please enter a valid option.");
            }
        }
    }

    /**
     * Takes an arraylist of options and prints it as a list of options
     * 
     * @param options ArrayList of options
     */
    public static int printOptions(ArrayList<String> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, options.get(i));
        }
        return options.size();
    }

}