import java.security.MessageDigestSpi;

public class TestMain {
    public static void main(String[] args) {
        Account.resetAccountsData();
        Account.createAccount("User1", "user1@gmail.com", "user1", "Seller");
        Account.createAccount("User2", "user2@gmail.com", "user2", "Customer");
        Store.createStore("2", "Store1", "The best store");
        Store.editStore("1", "notStore1", "The worst store");
        Store.deleteStore("1");
    }
}
