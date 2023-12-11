# CS18000 Project 5
Sean Kim, Soham Paik, Yash Patel

## Compiling and Running Instructions
On the device that will be running the server, download this repository from GitHub. Then, open 
the project in the editor of your choice and compile and run `Server.java`. Then download this 
repository on each device that the program will be run from. Open the project in an editor and 
go to line 25 of `StoreApplication.java`. Change `"localhost"` to the IP address of the device 
on which the server is running. Afterward, run `StoreApplication.java` to get started with using 
the application.

## Submissions
___ submitted the report and video on Brightspace.

___ submitted the Vocareum workspace.

## Classes

### Account
This is the class that deals with most of the data management of the program. It reads and writes 
to the `accountsData.json` file, which stores all the data except for counters for user and store 
ID's, which are stored in the `userIDIncrement.txt` and `storeIDIncrememnt.txt` files. All portions 
of code where the program writes to files is done in a synchronized manner. The 
`accountsData.json` file holds an array of objects, and each object represents an Account object. 
Each Account object holds information regarding the account including its username, email, password, 
role, ID, an array of its Stores, an array of its Messages, an array of the ID's the account has 
blocked, and an array of the ID's the account has become invisible to. The most notable helper methods 
of the Account class are the `readAccountsData()` and `writeAccountsData(Account[] accounts)` methods. 
The former reads the `accountsData.json` file and returns the array of Accounts stored in the file. The 
latter takes an array of Accounts and writes it to the `accountsData.json` file. These two methods allow 
all the other methods to easily read from and write to the `accountsData.json` file. These methods and some 
other helper methods are used in the Message and Store classes so that these classes can change the messages 
and stores associated with the accounts.

### ButtonEditor


### Button Renderer


### Message
This class deals with managing the Messages associated with accounts. It uses the helper methods from the 
Account class to create, edit, and delete Messages. Each Message object has a sender 
ID, a recipient ID, a message, a boolean to indicate if the message is deleted for the sender, a boolean to 
indicate if the message is deleted for the recipient, and a long order which indicates the order in which 
the message appears in the conversation it is part of.

### ResetData
This class contains a main method that carries out the `Account.resetAccountsData()` method when run. 
It resets all the data of the application, clearing all accounts, stores, messages, and resetting the 
counters for the user ID's and store ID's.

### Server
This class runs the server for the application. It creates a ServerSocket with port number 4242, and 
constantly checks to see if there is a connection to accept. Each time
the socket accepts a connection, it creates a new thread which will communicate with and carry out tasks 
for the specific client. The thread's `run()` method repeatedly reads from the client. The first line it 
reads is always a line indicating what inputs it should expect from the client and what tasks to carry out 
in the backend. The following lines are the inputs necessary to carry out the task. Once it is complete, 
it writes back to the client any information the client needs in order to display the results of the task 
to the user. Then it repeats this process again each time the client writes something to the server. 
When the program is closed from the client side, it will send the server a message to close the socket.

### Store
This class deals with managing the Stores associated with accounts. It uses the helper methods from the
Account class to create, edit, and delete Stores. Each Store object has a store name, a store ID, and a 
store description. Since this class creates stores, it is able to write to and read from the
`storeIDIncrememnt.txt` file. Writing to this file is done in a synchronized manner.

### StoreApplication
This class runs the client application that the users will interact with. It connects to the server run 
by the `Server.java` class and leaves all the processing of data to the server. It makes use of Java Swing GUI 
in order to display information to and take inputs from the user. Each time this a client needs to make a 
request to the server to carry out a certain task, the first line it writes indicates 
what the server should do and what inputs it should expect, and then the following lines in provide the 
necessary arguments. In the GUI, the main part of the application is displayed in a single JFrame with several 
tabbed panes, including the Stores tab, the Conversations tab, and the Account tab. Each tab has its own set 
of panels that contain the necessary GUI elements such as buttons and text boxes. In addition, each time the 
user switches tabs, it refreshes the elements in the tab with updates information from the server. For some purposes, 
the program creates new smaller JFrames with a more specific task. For example, there is a separate window for 
the log in and sign up. Messages to the user indicating the success/failure of an operation are often 
displayed through simple JOptionPanes.