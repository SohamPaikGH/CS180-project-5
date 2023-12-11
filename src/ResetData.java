/**
 * ResetData
 *
 * When the main method is run, it resets all the data of the application, clearing all accounts,
 * stores, messages, and resetting the counters for the user ID's and store ID's
 *
 * @author Sean Kim, Soham Paik, Yash Patel, lab sec l17
 *
 * @version December 10, 2023
 */
public class ResetData {
    public static void main(String[] args) {
        Account.resetAccountsData();
    }
}
