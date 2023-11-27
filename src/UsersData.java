import java.util.*;
import java.io.*;

/**
 * UsersData
 *
 * Deals primarily with reading to and writing from the usersData.txt file
 * to retrieve information about users and to update their information.
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
public class UsersData {

    /**
     * Reads the usersData.txt file and returns an ArrayList of the data
     * Each element is a String array of email,username,password,role,userID
     *
     * @return An ArrayList of the users data, element is a String array of email,username,password,role,userID
     */
    public static ArrayList<String[]> readUsersData() {
        ArrayList<String[]> usersData = new ArrayList<>();  // the arraylist to contain the data
        try (BufferedReader bfr = new BufferedReader(new FileReader("usersData.txt"))) {
            String line = bfr.readLine();   // line in the usersData.txt file being read
            while (line != null) {
                usersData.add(line.split(","));
                line = bfr.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usersData;
    }

    /**
     * Returns the index of usersData that contains the username; returns -1 if it is not found
     *
     * @param username The username to look for
     * @param usersData An ArrayList of the users data
     * @return The index of usersData that contains the username; -1 if it is not found
     */
    public static int indexOfUsername(String username, ArrayList<String[]> usersData) {
        if (usersData.isEmpty()) {
            return -1;
        }
        int userIndex = -1;     // index of the username
        for (int i = 0; i < usersData.size(); i++) {    // finds which line this user is on
            if (usersData.get(i)[1].equals(username)) {
                userIndex = i;
            }
        }
        return userIndex;
    }

    /**
     * Returns the index of usersData that contains the email; returns -1 if it is not found
     *
     * @param email The email to look for
     * @param usersData An ArrayList of the users data
     * @return The index of usersData that contains the email; -1 if it is not found
     */
    public static int indexOfEmail(String email, ArrayList<String[]> usersData) {
        if (usersData.isEmpty()) {
            return -1;
        }
        int userIndex = -1;     // index of the email
        for (int i = 0; i < usersData.size(); i++) {    // finds which line this user is on
            if (usersData.get(i)[0].equals(email)) {
                userIndex = i;
            }
        }
        return userIndex;
    }

    /**
     * Returns the index of usersData that contains the userID; returns -1 if it is not found
     *
     * @param userID The userID to look for
     * @param usersData An ArrayList of the users data
     * @return The index of usersData that contains the userID; -1 if it is not found
     */
    public static int indexOfUserID(String userID, ArrayList<String[]> usersData) {
        if (usersData.isEmpty()) {
            return -1;
        }
        int userIndex = -1;     // index of the email
        for (int i = 0; i < usersData.size(); i++) {    // finds which line this user is on
            if (usersData.get(i)[4].equals(userID)) {
                userIndex = i;
            }
        }
        return userIndex;
    }

    /**
     * Returns the password of the user at the given index of usersData
     *
     * @param userIndex Index of usersData that we want to look at
     * @param usersData An ArrayList of the users data
     * @return the password of the user at the given index of usersData
     */
    public static String getPassword(int userIndex, ArrayList<String[]> usersData) {
        return usersData.get(userIndex)[2];
    }

    /**
     * Adds data of a new user in usersData.txt with the given information
     *
     * @param email email of the new user
     * @param username username of the new user
     * @param password password of the new user
     * @param role role of the new user
     * @return true if it successfully added user
     */
    public static boolean addUser(String email, String username, String password, String role, String userID) {
        try (PrintWriter usersDataWriter = new PrintWriter((new FileOutputStream("usersData.txt", true)));
             PrintWriter conversationsDataWriter = new PrintWriter((new FileOutputStream("conversationsData.txt",
                     true)))) {
            usersDataWriter.println(email + "," + username + "," + password + "," + role + "," + userID);
            usersDataWriter.flush();
            conversationsDataWriter.println("");
            conversationsDataWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Edits data of a new user in usersData.txt with the given information
     *
     * @param userIndex Index of the user to edit the data of
     * @param email new email of the user
     * @param username new username of the user
     * @param password new password of the user
     * @return true if it successfully edited the data
     */
    public static boolean editUser(int userIndex, String email, String username, String password) {
        ArrayList<String[]> usersData = UsersData.readUsersData();  // arraylist of users data
        try (BufferedReader bfr = new BufferedReader(new FileReader("usersData.txt"))) {
            String newFile = "";    // hold the data for the new file with edited user info
            String line = bfr.readLine();   // line of usersData.txt being read
            int index = 0;      // line number
            while (line != null) {
                if (index == userIndex) {   // rewrite if line number is userIndex
                    if (usersData.get(index)[3].equals("Seller")) {
                        newFile += email + "," + username + "," + password + ",Seller," + line.substring(line.indexOf(",Seller,") + 8) + "\n";
                    } else {
                        newFile += email + "," + username + "," + password + ",Customer," + line.substring(line.indexOf(",Customer,") + 10) + "\n";
                    }
                } else {
                    newFile += line + "\n";
                }
                line = bfr.readLine();
                index++;
            }
            // write the new data onto the file
            try (PrintWriter pw = new PrintWriter((new FileOutputStream("usersData.txt")))) {
                pw.print(newFile);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addBlockedUser(int userIndex, String userToBlock, int check) {
        try (BufferedReader bfr = new BufferedReader(new FileReader("usersData.txt"))) {
            String newFile = "";    // hold the data for the new file with edited user info
            String line = bfr.readLine();   // line of usersData.txt being read
            int index = 0;      // line number
            while (line != null) {
                if (index == userIndex) {   // rewrite if line number is userIndex
                    newFile += line + "," + check + userToBlock + "\n";
                } else {
                    newFile += line + "\n";
                }
                line = bfr.readLine();
                index++;
            }
            // write the new data onto the file
            try (PrintWriter pw = new PrintWriter((new FileOutputStream("usersData.txt")))) {
                pw.print(newFile);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> createBlockedList(int index) {
        String[] usersData = readUsersData().get(index);    // the data of the specific user at index
        ArrayList<String> blockedList = new ArrayList<String>();    // the arraylist to contain the blocked users
        for (int i = 5; i < usersData.length; i++) {
            if (usersData[i].startsWith("0")) {
                blockedList.add(usersData[i].substring(1));
            }
        }
        return blockedList;
    }

    public static ArrayList<String> createInvisibleList(int index) {
        String[] usersData = readUsersData().get(index);    // the data of the specific user at index
        ArrayList<String> invisibleList = new ArrayList<String>();  // the arraylist to contain the invisible to users
        for (int i = 5; i < usersData.length; i++) {
            if (usersData[i].startsWith("1")){
                invisibleList.add(usersData[i].substring(1));
            }
        }
        return invisibleList;
    }


    /**
     * Deletes the user at a given index
     *
     * @param userIndex index of the user to delete
     * @return true if it successfully deleted the user
     */
    public static boolean deleteUser(int userIndex) {
        try (BufferedReader usersDataReader = new BufferedReader(new FileReader("usersData.txt"));
             BufferedReader conversationsDataReader = new BufferedReader(
                     new FileReader("conversationsData.txt"))) {
            String newUsersData = "";    // hold the data for the new usersData.txt
            String usersDataLine = usersDataReader.readLine();   // line of usersData.txt being read
            String newConversationsData = "";  // hold the data for the new conversationsData.txt
            String conversationsDataLine = conversationsDataReader.readLine();  // line of conversationsData.txt
            int index = 0;      // line number
            while (usersDataLine != null) {
                if (index != userIndex) {   // write if line number isn't userIndex
                    newUsersData += usersDataLine + "\n";
                    newConversationsData += conversationsDataLine + "\n";
                }
                usersDataLine = usersDataReader.readLine();
                conversationsDataLine = conversationsDataReader.readLine();
                index++;
            }
            // write the new data onto the file
            try (PrintWriter usersDataWriter = new PrintWriter((new FileOutputStream("usersData.txt")));
                 PrintWriter conversationsDataWriter = new PrintWriter((
                         new FileOutputStream("conversationsData.txt")))) {
                usersDataWriter.print(newUsersData);
                usersDataWriter.flush();
                conversationsDataWriter.print(newConversationsData);
                conversationsDataWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * resets the usersData.txt, userIDIncrement.txt, and conversationsData.txt files to default
     * and deletes all conversation files
     */
    public static void resetData() {
        ArrayList<String[]> conversationsData = Conversation.readConversationsData(); // arraylist of conversations
        for (String[] line : conversationsData) {
            for (String filename : line) {
                File file = new File(filename);  // conversation file name
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        try (PrintWriter usersDataPW = new PrintWriter(new FileOutputStream("usersData.txt"));
             PrintWriter conversationsDataPW = new PrintWriter(new FileOutputStream("conversationsData.txt"));
             PrintWriter userIDIncrementPW = new PrintWriter(new FileOutputStream("userIDIncrement.txt"));) {
            usersDataPW.print("");
            usersDataPW.flush();
            userIDIncrementPW.println("0");
            userIDIncrementPW.flush();
            conversationsDataPW.print("");
            conversationsDataPW.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
