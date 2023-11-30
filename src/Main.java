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
        Account.resetAccountsData();
        Account.createAccount("name1", "email1", "pass1", "Seller");
        Account.createAccount("name2", "email2", "pass2", "Customer");
        Account.setUsername("0", "Bob");
        Account.block("0", "1");
        Account.block("0", "2");
        Account.invisible("0", "2");
        Message.createMessage("0", "2", "Hello");
    }
}