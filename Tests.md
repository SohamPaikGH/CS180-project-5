# Tests
First, the data was reset, then all of these test were done sequentially.

## Test 1: User Sign Up
Steps:
1. User launches the application.
2. User clicks "Sign Up" button.
3. User selects the username text box.
4. User enters "Seller1" via the keyboard.
5. User selects the password text box.
6. User enters "Seller123" via the keyboard.
7. User selects the email text box.
8. User enters "seller1@gmail.com" via the keyboard.
9. User selects the role dropdown.
10. User selects the "Seller" option.
11. User clicks "Register" button.
12. User clicks "Ok" button.
13. User clicks "Sign Up" button again.
14. User selects the username text box.
15. User enters "Customer1" via the keyboard.
16. User selects the password text box.
17. User enters "Customer123" via the keyboard.
18. User selects the email text box.
19. User enters "customer1@gmail.com" via the keyboard.
20. User selects the role dropdown.
21. User selects the "Customer" option.
22. User clicks "Register" button.
23. User clicks "Ok" button.

Expected result: New accounts with the given fields are stored in accountsData.json.

Test Status: Passed

## Test 2: User Sign Up Blank Field Error
Steps:
1. User launches the application.
2. User clicks "Sign Up" button.

Expected result: A panel appears indicating that at least one of the fields is blank.

Test Status: Passed

## Test 3: User Sign Up Duplicate Username Error
Steps:
1. User launches the application.
2. User clicks "Sign Up" button.
3. User selects the username text box.
4. User enters "Seller1" via the keyboard.
5. User selects the password text box.
6. User enters "Seller123" via the keyboard.
7. User selects the email text box.
8. User enters "seller1@gmail.com" via the keyboard.
9. User selects the role dropdown.
10. User selects the "Seller" option.
11. User clicks "Register" button.

Expected result: A panel appears indicating that the username is taken.

Test Status: Passed

## Test 4: User Log In
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.

Expected result: Application verifies the user's username and password and loads the main page.

Test Status: Passed

## Test 5: User Log In Error
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller1234" in the password field.
4. User clicks "Sign In" button.

Expected result: A panel appears indicating that the username or password is incorrect.

Test Status: Passed

## Test 6: Create Store
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the "Create Store" button.
6. User selects the Store Name text box.
7. User enters "Store1" via the keyboard.
8. User selects the Store Description text box.
9. User enters "Seller1's first store" via the keyboard.
10. User clicks "Create Store" button.
11. User clicks "Ok" button.

Expected result: The newly created store is now visible in the store dashboard, and 
the Seller1 account has a new store with the given fields in accountsData.json.

Test Status: Passed

## Test 7: Create Store Duplicate Name Error
Steps:
1. Repeat the same steps as in Step 6.

Expected result: A panel appears indicating that a store with that name already exists.

Test Status: Passed

## Test 8: Store Editing
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button. 
5. User double-clicks the store name cell of Store 1.
6. User enters "2" at the end of the description via the keyboard.
7. User double-clicks the store description cell of Store 1.
8. User enters "!" at the end of the description via the keyboard.
9. User clicks "Save Data" button for the store.

Expected result: The new name and description are saved into the accountsData.json file.

Test Status: Passed

## Test 9: Store Deleting
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button. 
5. User clicks "Delete Store" button for Store 12.

Expected result: The store no longer appears on the dashboard, and the store is gone 
from Seller1's information in accountsData.json.

Test Status: Passed

## Test 10: Contacting Store
Steps:
1. Carry out the steps in Test 6 to create a store again.
2. User launches the application from another device.
3. User enters "Customer1" in the username field.
4. User enters "Customer123" in the password field.
5. User clicks "Sign In" button.
6. User clicks "Contact Store" button for Store1.
7. User selects the message text box.
8. User enters "Hello!" via the keyboard.
9. User clicks "Send" button.
10. User clicks "Ok".
11. User closes the small message window.
12. User clicks the Conversations tab.
13. User clicks the recipient dropdown.
14. User selects "Store 1".
15. User clicks "Select Recipient".

Expected result: The message sent is visible in the message area and new messages are 
added to the data regarding Customer1 and Seller1 in accountsData.json.

Test Status: Passed

## Test 11: Sending Message by Searching User
Steps:
1. User launches the application.
2. User enters "Customer1" in the username field.
3. User enters "Customer123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. User clicks "Search User" button.
7. User selects the text box in the new small window.
8. User enters "Seller1" via the keyboard.
9. User clicks "Search Username" button in the small window.
10. User selects the dropdown in the small window.
11. User selects "Seller1" from the dropdown.
12. User clicks "Select Recipient" button in the small window.
13. User selects the text box.
14. User enters "Hi!" via the keyboard.
15. User clicks "Send" button.

Expected result: The message sent is visible in the message area and new messages are
added to the data regarding Customer1 and Seller1 in accountsData.json.

Test Status: Passed

## Test 12: Blocking
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. User selects the recipient dropdown.
7. User selects "Customer1" from the dropdown.
8. User clicks "Select Recipient" button.
9. User clicks "Toggle Block" button.
10. Carry out the steps in Test 11 on another device.

Expected result: Customer1 can't send a message to Seller1, and when they try, a panel 
appears indicating that they are blocked.

Test Status: Passed

## Test 13: Unblocking
Steps:
1. Carry out the steps in Test 12 again.

Expected result: Customer1 can now message Seller1.

Test Status: Passed

## Test 14: Becoming Invisible
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. User selects the recipient dropdown.
7. User selects "Customer1" from the dropdown.
8. User clicks "Select Recipient" button.
9. User clicks "Toggle Invisibility" button.
10. Carry out steps 1 through 10 of Test 11 on another device.

Expected result: Seller1 does not appear in the search results for Customer1.

Test Status: Passed

## Test 15: Becoming Invisible
Steps:
1. Carry out the steps in Test 14 again.

Expected result: Seller1 now appears in the search results for Customer1.

Test Status: Passed

## Test 16: Editing Messages
Steps:
1. User launches the application.
2. User enters "Customer1" in the username field.
3. User enters "Customer123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. User selects the recipient dropdown.
7. User selects "Seller1" from the dropdown.
8. User clicks "Select Recipient" button.
9. User double-clicks the message text cell of the first message.
10. User enters "!!" at the end of the message via keyboard.
11. User clicks "Edit" button.
12. User launches the application on another device.
13. User enters "Seller1" in the username field.
14. User enters "Seller123" in the password field.
15. User clicks "Sign In" button.
16. User clicks the Conversations tab.
17. User selects the recipient dropdown.
18. User selects "Customer1" from the dropdown.
19. User clicks "Select Recipient" button.

Expected result: Seller1 sees the edited message. The message data for that 
message is edited in accountsData.json.

Test Status: Passed

## Test 16: Editing Messages
Steps:
1. User launches the application.
2. User enters "Customer1" in the username field.
3. User enters "Customer123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. User selects the recipient dropdown.
7. User selects "Seller1" from the dropdown.
8. User clicks "Select Recipient" button.
9. User double-clicks the message text cell of the first message.
10. User enters "!!" at the end of the message via keyboard.
11. User clicks "Edit" button.
12. User launches the application on another device.
13. User enters "Seller1" in the username field.
14. User enters "Seller123" in the password field.
15. User clicks "Sign In" button.
16. User clicks the Conversations tab.
17. User selects the recipient dropdown.
18. User selects "Customer1" from the dropdown.
19. User clicks "Select Recipient" button.

Expected result: Both Customer1 and Seller1 see the edited message.

Test Status: Passed

## Test 17: Deleting Messages
Steps:
1. Carry out steps 1 through 8 in Test 16.
2. User clicks "Delete Message" button.
3. Carry out steps 12 through 19 in Test 16.

Expected result: The message is gone from Customer1's view but not from Seller1's view.

Test Status: Passed

## Test 18: Seller Store View
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Conversations tab.
6. Customer1 is already selected for the recipient dropdown.
7. Select the Select View dropdown.
8. Select "Seller1" from the dropdown.
9. Click "Select View" button.
10. Select the Select View dropdown.
11. Select "Store 1" from the dropdown.
12. Click "Select View" button.

Expected result: Each selected view displays their respective conversations with Customer 1.

Test Status: Passed

## Test 19: Viewing Account Data
Steps:
1. User launches the application.
2. User enters "Seller1" in the username field.
3. User enters "Selller123" in the password field.
4. User clicks "Sign In" button.
5. User clicks the Account tab.

Expected result: Each of the text boxes are filled with the respective data for this acccount.

Test Status: Passed

## Test 20: Editing Account Data
Steps:
1. Carry out the steps in Test 19.
2. User selects the Password text box.
3. User enters "4" to the end of the password via the keyboard.
4. User clicks "Save" button.
5. User exits the application.
6. User launches the application.
7. User enters "Seller1" in the username field.
8. User enters "Selller1234" in the password field.

Expected result: Seller1 can log in with the new password Seller1234.

Test Status: Passed

## Test 21: Deleting Account
Steps:
1. Carry out the steps in Test 19.
2. User clicks "Delete Account" button.
3. User relaunches the application.
4. User enters "Seller1" in the username field.
5. User enters "Selller1234" in the password field.
6. User launches the application on another device.
7. User enters "Customer1" in the username field.
8. User enters "Customer123" in the password field.
9. User clicks "Sign In" button.
10. User clicks the Conversations tab.

Expected result: Seller1 can no longer log into the deleted account. Customer1 can no 
longer see Seller1's store in the dashboard, cannot converse with Seller1 or its stores 
anymore, and the conversation with Seller1 and its store is gone.

Test Status: Passed