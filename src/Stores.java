import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores
 *
 * Class to handle data for each seller's stores.
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
public class Stores {

    public static void addStore(String userID, String storeName) {
        try {
            // Add store name to stores file for seller
            String storesFile = String.format("%s-Stores.txt", userID);
            PrintWriter printWriter = new PrintWriter(new FileWriter(storesFile, true));
            printWriter.println(storeName);
            printWriter.flush();

            createStoreInfoFile(userID, storeName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeStore(String userID, String storeName) {
        try {
            ArrayList<String> originalFileLines = new ArrayList<String>();
            String storesFile = String.format("%s-Stores.txt", userID);
            File f = new File(storesFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

            String line = bufferedReader.readLine();

            while (line != null) {
                originalFileLines.add(line);
                line = bufferedReader.readLine();
            }

            f.delete();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(storesFile));

            for (String fileLine: originalFileLines) {
                if ( !fileLine.equals(storeName) ) {
                    bufferedWriter.write(fileLine + '\n');
                } else {
                    continue;
                }
            }

            deleteStoreInfoFile(userID, storeName);

            bufferedWriter.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void editStore(String userID, String originalStoreName, String newStoreName) {
        try {
            ArrayList<String> originalFileLines = new ArrayList<String>();
            String storesFile = String.format("%s-Stores.txt", userID);
            File f = new File(storesFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

            String line = bufferedReader.readLine();

            while (line != null) {
                originalFileLines.add(line);
                line = bufferedReader.readLine();
            }

            f.delete();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(storesFile));

            for (String fileLine: originalFileLines) {
                if ( !fileLine.equals(originalStoreName) ) {
                    bufferedWriter.write(fileLine + '\n');
                } else {
                    bufferedWriter.write(newStoreName + '\n');
                }
            }

            editStoreInfoFile(userID, originalStoreName, newStoreName);

            bufferedWriter.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyIfAlreadyExistingStoreExists(String newStoreName) {
        try {
            File f = new File("usersData.txt");
            BufferedReader usersDataFileReader = new BufferedReader(new FileReader(f));

            String line = usersDataFileReader.readLine();

            while ( line != null ) {
                String[] lineElements = line.split(",");
                if ( lineElements[3].equals("Seller") ) {
                    String storeDataFileName = String.format("%s-Stores.txt", lineElements[4]);
                    BufferedReader storeDataFileReader = new BufferedReader(new FileReader(storeDataFileName));
                    String storeDataFileLine = storeDataFileReader.readLine();
                    while ( storeDataFileLine != null ) {
                        if (storeDataFileLine.equals(newStoreName)) {
                            storeDataFileReader.close();
                            return true;
                        }
                        storeDataFileLine = storeDataFileReader.readLine();
                    }
                    storeDataFileReader.close();
                }
                line = usersDataFileReader.readLine();
            }
            usersDataFileReader.close();

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Prints all stores of a specific seller
    public static void printMyStores(String userID) {
        try {
            String storesFile = String.format("%s-Stores.txt", userID);
            File f = new File(storesFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

            String line = bufferedReader.readLine();

            while (line != null) {
                System.out.println(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("No stores found!");
        }
    }

    // Prints all stores in the system
    public static void printAllStores() {
        try {
            File f = new File("usersData.txt");
            BufferedReader usersDataFileReader = new BufferedReader(new FileReader(f));

            String line = usersDataFileReader.readLine();

            while ( line != null ) {
                String[] lineElements = line.split(",");
                if ( lineElements[3].equals("Seller") ) {
                    String sellerID = lineElements[4];
                    System.out.println(lineElements[1] + "'s Stores: ");
                    printMyStores(sellerID);
                }
                line = usersDataFileReader.readLine();
            }

            usersDataFileReader.close();

        } catch (Exception e) {
            System.out.println("No stores found!");
        }
    }

    public static void contactStore(String username, String storeName, String message) {
        System.out.println("Store contacted!");
        try {
            File f = new File("usersData.txt");
            BufferedReader usersDataFileReader = new BufferedReader(new FileReader(f));

            String line = usersDataFileReader.readLine();

            while ( line != null ) {
                String[] lineElements = line.split(",");

                if ( lineElements[3].equals("Seller") ) {
                    String storeDataFileName = String.format("%s-Stores.txt", lineElements[4]);
                    BufferedReader storeDataFileReader = new BufferedReader(new FileReader(storeDataFileName));
                    String storeDataFileLine = storeDataFileReader.readLine();

                    while ( storeDataFileLine != null ) {
                        if (storeDataFileLine.equals(storeName)) {
                            // Send message to seller who owns the store
                            Conversation conversation = new Conversation(lineElements[1], username);
                            conversation.sendMessage(username, message);
                            contactStoreInfoFile(username, lineElements[4], storeName);
                        }
                        storeDataFileLine = storeDataFileReader.readLine();
                    }
                    storeDataFileReader.close();
                }

                line = usersDataFileReader.readLine();
            }
            usersDataFileReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void displayStoreStatistics(String userID, String storeName, boolean ascending) {
        try {
            String storesDataFileName = String.format("%s-Stores-%s.txt", userID, storeName);

            // Retrieve messages from customers
            List<String> customerMessages = getCustomerMessages(userID, storesDataFileName);

            // Calculate and display statistics
            displayMessageStatistics(customerMessages, ascending);
            displayCommonWordsStatistics(customerMessages, userID, storeName, ascending);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createStoreInfoFile(String userID, String storeName) {
        try {
            // Add store name to stores file for seller
            String storesFile = String.format("%s-Stores-%s.txt", userID, storeName);
            PrintWriter printWriter = new PrintWriter(new FileWriter(storesFile, true));
            printWriter.println();
            printWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteStoreInfoFile(String userID, String storeName) {
        try {
            String storesFile = String.format("%s-Stores-%s.txt", userID, storeName);
            File file = new File(storesFile);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editStoreInfoFile(String userID, String storeName, String newStoreName) {
        try {
            String originalfileName = String.format("%s-Stores-%s.txt", userID, storeName);
            File originalFile = new File(originalfileName);

            String newFileName = String.format("%s-Stores-%s.txt", userID, newStoreName);
            File newFile = new File(newFileName);

            originalFile.renameTo(newFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void contactStoreInfoFile(String username, String sellerID, String storeName) {
        try {
            String storeInfoFile = String.format("%s-Stores-%s.txt", sellerID, storeName);
            File f = new File(storeInfoFile);
            BufferedReader fileReader = new BufferedReader(new FileReader(f));
            String line = fileReader.readLine();
            ArrayList<String> originalFileLines = new ArrayList<>();
            boolean userNameFound = false;

            while ( line != null ) {
                originalFileLines.add(line);
                line = fileReader.readLine();
            }

            f.delete();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(storeInfoFile));
            for (String fileLine: originalFileLines) {
                if (!fileLine.contains(username)) {
                    bufferedWriter.write(fileLine + '\n');
                } else {
                    userNameFound = true;
                    String[] lineElements = fileLine.split(":");
                    int messagesSent = Integer.parseInt(lineElements[1]);
                    messagesSent += 1;
                    String output = username + ":" + Integer.toString(messagesSent);
                    bufferedWriter.write(output + '\n');
                }
            }

            if ( !userNameFound ) {
                String output = username + ":" + 1;
                bufferedWriter.write(output + '\n');
            }

            fileReader.close();
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Helper method to retrieve messages from customers
    // Helper method to retrieve messages from customers
private static ArrayList<String> getCustomerMessages(String userID, String storeDataFileName) throws IOException {
    ArrayList<String> totalLine = new ArrayList<>();
    try {
        BufferedReader bfr = new BufferedReader(new FileReader(storeDataFileName));
        String line = bfr.readLine();
        while (line != null) {
            if (!line.equals("")) {
                totalLine.add(line);
            }
            line = bfr.readLine();
        }
    } catch (Exception e) {
        e.printStackTrace();

    }
    return totalLine;
}


    // Helper method to display message statistics
    private static void displayMessageStatistics(List<String> customerMessages, boolean ascending) {
        int totalMessageCount = 0;

        if (ascending) {
            Collections.sort(customerMessages);
        } else {
            Collections.sort(customerMessages, Collections.reverseOrder());
        }

        // Display individual customer message counts
        System.out.println("Customer Message Counts:");
        for (String message : customerMessages) {
            String[] arr = message.split(":");
            String user = arr[0];
            String messageCount = arr[1];
            totalMessageCount += Integer.parseInt(messageCount);
            System.out.println(user + ": " + messageCount + " message(s)");
        }

        // Display total message count
        System.out.println("Total Messages: " + totalMessageCount);
    }

    // Helper method to display most common words statistics
    private static void displayCommonWordsStatistics(List<String> customerMessages, String userID, String storeName,
     boolean ascending) throws IOException {

        // 1: Open the store information file
        // 2: Get each username
        // 3: Find the conversation file for each username
        // 4: Open the conversation file
        // 5: Read all messages
        // 6: Add all messages to the array list
        // 7: Find the most common word in the array list

        // Stores customer id of every customer who messages the store
        ArrayList<String> customerIds = new ArrayList<>();

        // Stores username of every customer who messages the store
        ArrayList<String> customerUsernames = new ArrayList<>();

        // Stores messages of every customer who messaged the store
        ArrayList<String> allMessageStrings = new ArrayList<>();

        // Stores each word in all messages
        ArrayList<String> allWordStrings = new ArrayList<>();

        for (String message : customerMessages) {
            String[] arr = message.split(":");
            String username = arr[0];

            BufferedReader bfr = new BufferedReader(new FileReader("usersData.txt"));
            String usersDataReaderLine = bfr.readLine();

            while (usersDataReaderLine != null) {
                String[] usersDatareaderLineStrings = usersDataReaderLine.split(",");
                if (usersDatareaderLineStrings[1].equals(username)) {
                    customerIds.add(usersDatareaderLineStrings[4]);
                    customerUsernames.add(usersDatareaderLineStrings[1]);
                }
                usersDataReaderLine = bfr.readLine();
            }

            bfr.close();
        }

        // Find the conversation file of each customer who messaged the store
        // Read all the messages sent by the customer
        // Then add them to the allMessageStrings array list
        for (int i = 0; i < customerIds.size(); i++) {
            String s = String.format("%s-%s.txt", userID, customerIds.get(i));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(s));
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] split1 = line.split(": ");
                String[] split2 = split1[0].split(",");
                String split2String = split2[1];

                if ( split2String.equals(customerUsernames.get(i)) ) {
                    allMessageStrings.add(split1[1]);
                }

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }

        // Split message strings into words
        for (String messageString: allMessageStrings) {
            messageString = messageString.replaceAll("[^a-zA-Z0-9\s\n\t\f]", "");
            String[] wordsInMessageString = messageString.split(" ");
            allWordStrings.addAll(Arrays.asList(wordsInMessageString));
        }

        // Count occurrences of each word
         Map<String, Long> wordCounts = allWordStrings.stream()
                 .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

        // Display the 3 most common words based on ascending/descending order
        ArrayList<String> threeMostCommonWords = new ArrayList<>();
        wordCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .forEach(entry -> threeMostCommonWords.add(entry.getKey() + ": " + entry.getValue()));

        if (ascending) {
            Collections.sort(threeMostCommonWords);
        } else {
            Collections.sort(threeMostCommonWords, Collections.reverseOrder());
        }

        for (String word: threeMostCommonWords) {
            System.out.println(word);
        }

    }

    public static void getMessagesSent(String customerName, boolean ascending) {
        try {
            File usersDataFile = new File("usersData.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(usersDataFile));
            String line = bfr.readLine();
            ArrayList<String> outputMessages = new ArrayList<>();

            while (line != null) {
                if (line.contains("Seller")) {
                    // Find each seller and get their ID
                    String[] lineElements = line.split(",");
                    String sellerID = lineElements[4];

                    // Access all store names associated with seller
                    String sellerStoresFile = String.format("%s-Stores.txt", sellerID);
                    BufferedReader storeFileReader = new BufferedReader(new FileReader(sellerStoresFile));

                    // Read each store name
                    String storeFileLine = storeFileReader.readLine();
                    while (storeFileLine != null) {
                        String storeInfoFile = String.format("%s-Stores-%s.txt", sellerID, storeFileLine);

                        // Count the number of messages sent by the customer
                        String outputLine = readStoreInfoFile(storeInfoFile, customerName) + "to " + storeFileLine;

                        // add the output to the output messages arraylist
                        outputMessages.add(outputLine);

                        storeFileLine = storeFileReader.readLine();
                    }

                }
                line = bfr.readLine();
            }

            // Order the output messages based on the sorting option selected by the user
            if (ascending) { // sort the output messages in ascending order
                Collections.sort(outputMessages);
            } else { // sort the output messages in descending order
                Collections.sort(outputMessages, Collections.reverseOrder());
            }

            // print the output messages
            for (String outputMessage: outputMessages) {
                System.out.println(outputMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readStoreInfoFile(String filename, String customerName) {
        try {
            // Open the file storing the information about the store
            // because it contains all the usernames of customers who contacted the store
            // as well as the number of messages each customer sent
            BufferedReader bfr = new BufferedReader(new FileReader(filename));
            StringBuilder output = new StringBuilder();
            String line = bfr.readLine();

            // get the number of messages the customer sent and append to the string builder
            while (line != null) {
                if (line.contains(customerName)) {
                    String[] lineElements = line.split(":");
                    output.append("Sent " + lineElements[1] + " message(s) ");
                }
                line = bfr.readLine();
            }

            // return number of messages customer sent
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getMessagesReceived(String customerID, String customerName, boolean ascending) {
        try {
            File usersDataFile = new File("usersData.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(usersDataFile));
            String line = bfr.readLine();
            ArrayList<String> outputMessages = new ArrayList<>();

            while (line != null) {
                if (line.contains("Seller")) {
                    // Find each seller and get their ID
                    String[] lineElements = line.split(",");
                    String sellerID = lineElements[4];

                    // Access all store names associated with seller
                    String sellerStoresFile = String.format("%s-Stores.txt", sellerID);
                    BufferedReader storeFileReader = new BufferedReader(new FileReader(sellerStoresFile));

                    // Read each store name
                    String storeFileLine = storeFileReader.readLine();
                    while (storeFileLine != null) {
                        String storeInfoFile = String.format("%s-Stores-%s.txt", sellerID, storeFileLine);

                        // Count the number of messages sent by the store
                        int messagesReceivedCount = countConvoMessagesReceived(customerID, sellerID, customerName);

                        // Create the output string and add it to the outputmessages arraylist
                        String output = String.format("Received %d message(s) from %s", messagesReceivedCount, storeFileLine);
                        outputMessages.add(output);

                        storeFileLine = storeFileReader.readLine();
                    }

                }
                line = bfr.readLine();
            }

            // Order the output messages based on the sorting option selected by the user
            if (ascending) { // sorts output messages in ascending order
                Collections.sort(outputMessages);
            } else { // sorts output messages in descending order
                Collections.sort(outputMessages, Collections.reverseOrder());
            }

            // print out all output messages after sorting them
            for (String outputMessage: outputMessages) {
                System.out.println(outputMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countConvoMessagesReceived(String customerID, String sellerID, String customerName) {
        try {
            // Open the file storing the conversation between the seller and user
            String convoFileName = String.format("%s-%s.txt", sellerID, customerID);
            BufferedReader bfr = new BufferedReader(new FileReader(convoFileName));
            String line = bfr.readLine();
            int messagesReceived = 0;

            // count the number of messages sent by seller
            while ( line != null ) {
                if (!line.contains(customerName)) {
                    messagesReceived += 1;
                }
                line = bfr.readLine();
            }

            // return number of messages sent by seller
            return messagesReceived;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
