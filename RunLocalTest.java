import org.junit.*;

import java.lang.reflect.Field;

import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * RunLocalTests
 *
 * Tests that all parts of the program are running properly.
 *
 * @author Soham Paik, Sean Kim, Yash Patel, Sanjana Tatavarthi; CS 180 Black Lab Sec L17
 *
 * @version November 13, 2023
 */
@RunWith(Enclosed.class)
public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    /**
     *
     */
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        @Test(timeout = 1000)
        public void A_signUpTest() {
            // Set the input
            // Separate each input with a newline (\n).
            UsersData.resetData();
            String input = "2\ntest1@gmail.com\ntest1\ntest123\n1\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Enter email address: " +
                    "Enter username: " +
                    "Enter password: " +
                    "Are you a Customer or Seller?\n[1] Customer\n[2] Seller\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure sign up works properly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void B_signUpErrorTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "2\ntest1@gmail.com\ntest2@gmail.com\ntest,2\ntest2\n\ntest234\n3\n2\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Enter email address: " +
                    "Email address is already in use.\nEnter email address: " +
                    "Enter username: " +
                    "Username cannot contain comma or colon.\nEnter username: " +
                    "Enter password: " +
                    "Password cannot be empty.\nEnter password: " +
                    "Are you a Customer or Seller?\n[1] Customer\n[2] Seller\n" +
                    "Please enter one of the options.\n" +
                    "Are you a Customer or Seller?\n[1] Customer\n[2] Seller\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure sign up errors work properly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void C_signUpTestData() {
            String[] fileExpected = {"test1@gmail.com", "test1", "test123", "Customer", "0"};
            String[] fileActual = UsersData.readUsersData().get(0);
            assertEquals("Make sure account data persists when signing up", fileExpected, fileActual);
        }

        @Test(timeout = 1000)
        public void D_logInTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure log in works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void E_logInErrorTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ndskafds\ntest1\ntest1\ntest123\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Username does not exist; please enter a valid username.\n" +
                    "Username: Password: " +
                    "Incorrect password; try again.\nPassword: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure invalid username or incorrect password repromts the user.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void F_sendMessageTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n1\n3\nsadfja\ntest2\nHello\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "Enter user who you want to message: " +
                    "User does not exist.\nEnter user who you want to message: " +
                    "Enter your message: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure sending message works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void G_viewConversationsTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n1\n1\n1\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "List of Conversations\n[1] test2\n" +
                    "test1: Hello\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure sending message works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void H_editAndDeleteMessageTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n1\n2\n1\n1\n1\nHi\n1\n1\n1\n1\n2\n1\n1\n2\n1\n1\n1\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "List of Conversations\n[1] test2\n" +
                    "Select message to edit\n[1] test1: Hello\n" +
                    "[1] Edit message\n[2] Delete message\n" +
                    "Enter new message: Message edited.\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "List of Conversations\n[1] test2\n" +
                    "test1: Hi\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "List of Conversations\n[1] test2\n" +
                    "Select message to edit\n[1] test1: Hi\n" +
                    "[1] Edit message\n[2] Delete message\n" +
                    "Message deleted.\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "List of Conversations\n[1] test2\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure editing and deleting message works correctly.",
                    expected.trim(), stuOut.trim());
        }



        @Test(timeout = 1000)
        public void U_blockAndInvisibleTestPart1() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n4\n1\ntest3\ntest2\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "[1] Block user\n[2] Become invisible to user\n" +
                    "Enter the username of the user you would like to block\n" +
                    "User does not exist. Please enter a valid user:\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure blocking and invisible works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void V_blockAndInvisibleTestPart2() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest2\ntest234\n1\n3\ntest1\n4\n2\ntest1\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "Enter user who you want to message: " +
                    "This user has blocked you; you may not message them.\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "[1] Block user\n[2] Become invisible to user\n" +
                    "Enter the username of the user you would like to become invisible to:\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make blocking and invisible works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void W_blockAndInvisibleTestPart3() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n1\n3\ntest2\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Conversations\n[1] View Conversations\n[2] Edit Conversations\n" +
                    "[3] Message someone\n[4] Back to Main Menu\n" +
                    "Enter user who you want to message: " +
                    "User does not exist.\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure sending message works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void X_EditAccountTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest1\ntest123\n5\n1\ntest2\ntest3\n2\n\ntest345\n3\ntest3,\ntest3@gmail.com\n5\n6\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Account Settings\n[1] Change Username\n[2] Change Password\n[3] Change Email Address\n" +
                    "[4] Delete Account\n[5] Return to Main Menu\n" +
                    "Enter new username: Username is already taken.\n" +
                    "Enter new username: Username updated.\n" +
                    "Account Settings\n[1] Change Username\n[2] Change Password\n[3] Change Email Address\n" +
                    "[4] Delete Account\n[5] Return to Main Menu\n" +
                    "Enter new password: Password cannot be empty.\n" +
                    "Enter new password: Password updated.\n" +
                    "Account Settings\n[1] Change Username\n[2] Change Password\n[3] Change Email Address\n" +
                    "[4] Delete Account\n[5] Return to Main Menu\n" +
                    "Enter new email address: Email address cannot contain comma.\n" +
                    "Enter new email address: Email address updated.\n" +
                    "Account Settings\n[1] Change Username\n[2] Change Password\n[3] Change Email Address\n" +
                    "[4] Delete Account\n[5] Return to Main Menu\n" +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Thank you for using Program.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure account editing works correctly.",
                    expected.trim(), stuOut.trim());
        }

        @Test(timeout = 1000)
        public void Y_EditAccountDataTest() {
            String[] fileExpected = {"test3@gmail.com", "test3", "test345", "Customer", "0", "0test2"};
            String[] fileActual = UsersData.readUsersData().get(0);
            assertEquals("Make sure account data persists when editing data", fileExpected, fileActual);
        }

        @Test(timeout = 1000)
        public void Z_DeleteAccountTest() {
            // Set the input
            // Separate each input with a newline (\n).
            String input = "1\ntest3\ntest345\n5\n4\ntest345\n";

            // Pair the input with the expected result
            String expected = "Welcome to Program!\n" +
                    "[1] Log in\n[2] Sign up\n" +
                    "Username: " +
                    "Password: " +
                    "Main Menu:\n[1] Conversations\n[2] Statistics\n[3] Stores\n" +
                    "[4] Blacklist\n[5] Account Settings\n[6] Exit Program\n" +
                    "Account Settings\n[1] Change Username\n[2] Change Password\n[3] Change Email Address\n" +
                    "[4] Delete Account\n[5] Return to Main Menu\n" +
                    "Enter password to confirm account deletion.\n" +
                    "Account has been deleted.\n";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String stuOut = getOutput();

            // Trims the output and verifies it is correct.
            stuOut = stuOut.replace("\r\n", "\n");
            assertEquals("Make sure account editing works correctly.",
                    expected.trim(), stuOut.trim());
        }
    }
}
