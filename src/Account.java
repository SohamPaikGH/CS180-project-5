import java.util.ArrayList;

/**
 * Account
 *
 * Account object can hold information about a specific account to make it easier to work with the data.
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
public class Account {
    private String email;       // email of this account
    private String username;    // username of this account
    private String password;    // password of this account
    private String role;        // role of this account
    private String userID;      // ID of this account
    private int userIndex;      // account's index in usersData.txt
    private ArrayList<String> blockedBy = null; // list of usernames this user is blocked by
    private ArrayList<String> invisibleTo = null; // list of usernames that are invisible to this user

    /**
     * Instantiates new Account and sets the fields to the given parameters
     *
     * @param email email of this account
     * @param username username of this account
     * @param password password of this account
     * @param role role of this account
     * @param userIndex account's index in usersData.txt
     */
    public Account(String email, String username, String password, String role, String userID, int userIndex, ArrayList<String> blockedBy, ArrayList<String> invisibleTo) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.userID = userID;
        this.userIndex = userIndex;
        this.blockedBy = blockedBy;
        this.invisibleTo = invisibleTo;
    }

    /**
     * returns the email of this account
     *
     * @return the email of this account
     */
    public String getEmail() {
        return email;
    }

    /**
     * returns the username of this account
     *
     * @return the username of this account
     */
    public String getUsername() {
        return username;
    }

    /**
     * returns the password of this account
     *
     * @return the password of this account
     */
    public String getPassword() {
        return password;
    }

    /**
     * returns the role of this account
     *
     * @return the role of this account
     */
    public String getRole() {
        return role;
    }

    /**
     * returns the userID of this account
     *
     * @return the userID of this account
     */
    public String getUserID() {
        return userID;
    }

    /**
     * returns the userIndex of this account
     *
     * @return the userIndex of this account
     */
    public int getUserIndex() {
        return userIndex;
    }

    /**
     * returns the blockedBy of this account
     *
     * @return the blockedBy list of this account
     */
    public ArrayList<String> getBlockedBy() {
        return blockedBy;
    }

    /**
     * returns the invisibleTo of this account
     *
     * @return the invisibleTo list of this account
     */
    public ArrayList<String> getInvisibleTo() {
        return invisibleTo;
    }

    /**
     * sets the email of this account and updates usersData.txt
     *
     * @param email new email of this account
     */
    public void setEmail(String email) {
        this.email = email;
        UsersData.editUser(userIndex, email, username, password);
    }

    /**
     * sets the username of this account and updates usersData.txt
     *
     * @param username new username of this account
     */
    public void setUsername(String username) {
        this.username = username;
        UsersData.editUser(userIndex, email, username, password);
    }

    /**
     * sets the password of this account and updates usersData.txt
     *
     * @param password new password of this account
     */
    public void setPassword(String password) {
        this.password = password;
        UsersData.editUser(userIndex, email, username, password);
    }

    /**
     * adds to the blockedBy list
     *
     * @param username of the user who blocked you
     */
    public void setBlockedBy(String username) {

        blockedBy.add(username);
    }

    /**
     * adds to the invisibleTo list
     *
     * @param username of the user who chose to become invisible to you
     */
    public void setInvisibleTo(String username) {
        invisibleTo.add(username);
    }
}