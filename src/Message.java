import java.util.ArrayList;

public class Message {
    private String senderID;        // ID of the sender of the message
    private String recipientID;     // ID of the recipient of the message
    private String message;         // the message
    private boolean deletedForSender;       // whether the sender deleted it
    private boolean deletedForRecipient;    // whether the recipient deleted it
    private long order;             // its order in the conversation it's in

    /**
     * Instantiates a message object with the given fields
     * @param senderID ID of the sender of the message
     * @param recipientID ID of the recipient of the message
     * @param message the message
     * @param deletedForSender true if the sender deleted it
     * @param deletedForRecipient true if the recipient deleted it
     * @param order its order in the conversation it's in
     */
    public Message(String senderID, String recipientID, String message, boolean deletedForSender, boolean deletedForRecipient, long order) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
        this.deletedForSender = deletedForSender;
        this.deletedForRecipient = deletedForRecipient;
        this.order = order;
    }

    /**
     * Returns senderID
     * @return senderID
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * Returns recipientID
     * @return recipientID
     */
    public String getRecipientID() {
        return recipientID;
    }

    /**
     * Returns message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    public boolean isDeletedForSender() {
        return deletedForSender;
    }

    public boolean isDeletedForRecipient() {
        return deletedForRecipient;
    }

    public boolean isDeleted(String ID) {
        return deletedForSender && ID.equals(senderID) || deletedForRecipient && ID.equals(recipientID);
    }

    /**
     * Returns order
     * @return order
     */
    public long getOrder() {
        return order;
    }

    /**
     * Returns the length of the conversation between the users with the given ID's
     * @param senderID given sender ID
     * @param recipientID given recipient ID
     * @return the length of the conversation
     */
    public static long getConversationLength(String senderID, String recipientID) {
        Message[] messages = Account.getMessages(Account.toUserID(senderID));
        long conversationLength = 0;
        for (Message message : messages) {
            if (message.senderID.equals(senderID) && message.recipientID.equals(recipientID) || message.senderID.equals(recipientID) && message.recipientID.equals(senderID)) {
                if (!message.isDeleted(senderID)) {
                    conversationLength++;
                }
            }
        }
        return conversationLength;
    }

    /**
     * Returns an array of all the messages between the sender and the recipient (in order)
     * @param senderID the sender ID
     * @param recipientID the recipient ID
     * @return an array of all the messages between the two
     */
    public static Message[] getConversationArray(String senderID, String recipientID) {
        Message[] messages = Account.getMessages(Account.toUserID(senderID));
        ArrayList<Message> conversationList = new ArrayList<>();
        for (Message message : messages) {
            if (message.senderID.equals(senderID) && message.recipientID.equals(recipientID) || message.senderID.equals(recipientID) && message.recipientID.equals(senderID)) {
                if (!message.isDeleted(senderID)) {
                    conversationList.add(message);
                }
            }
        }

        Message[] conversationArray = new Message[conversationList.size()];
        for (int i = 0; i < conversationList.size(); i++) {
            conversationArray[i] = conversationList.get(i);
        }
        return conversationArray;
    }

    /**
     * Creates a message from senderID to recipientID with content message
     * @param senderID the senderID
     * @param recipientID the recipientID
     * @param message the message
     */
    public static void createMessage(String senderID, String recipientID, String message) {
        Message toSend = new Message(senderID, recipientID, message, false, getConversationLength(senderID, recipientID));

        senderID = Account.toUserID(senderID);
        Message[] senderMessages = Account.getMessages(senderID);
        Message[] newSenderMessages = new Message[senderMessages.length + 1];
        for (int i = 0; i < newSenderMessages.length - 1; i++) {
            newSenderMessages[i] = senderMessages[i];
        }
        newSenderMessages[newSenderMessages.length - 1] = toSend;
        Account.setMessages(senderID, newSenderMessages);

        recipientID = Account.toUserID(recipientID);
        Message[] recipientMessages = Account.getMessages(recipientID);
        Message[] newRecipientMessages = new Message[recipientMessages.length + 1];
        for (int i = 0; i < newRecipientMessages.length - 1; i++) {
            newRecipientMessages[i] = recipientMessages[i];
        }
        newRecipientMessages[newRecipientMessages.length - 1] = toSend;
        Account.setMessages(recipientID, newRecipientMessages);
    }

    /**
     * Edits the contents of the specific message to newMessage
     * @param senderID the sender ID
     * @param recipientID the recipient ID
     * @param order the order of the message
     * @param newMessage the new content of the message
     */
    public static void editMessage(String senderID, String recipientID, long order, String newMessage) {
        Message[] messages = Account.getMessages(Account.toUserID(senderID));
        for (Message message : messages) {
            if ((message.senderID.equals(senderID) && message.recipientID.equals(recipientID) || message.senderID.equals(recipientID) && message.recipientID.equals(senderID))
                    && message.order == order) {
                message.message = newMessage;
                break;
            }
        }
        Account.setMessages(Account.toUserID(senderID), messages);

        messages = Account.getMessages(Account.toUserID(recipientID));
        for (Message message : messages) {
            if ((message.senderID.equals(senderID) && message.recipientID.equals(recipientID) || message.senderID.equals(recipientID) && message.recipientID.equals(senderID))
                    && message.order == order) {
                message.message = newMessage;
                break;
            }
        }
        Account.setMessages(Account.toUserID(recipientID), messages);
    }

    /**
     * Deletes the specific message
     * @param senderID the sender ID
     * @param recipientID the recipient ID
     * @param order the order of the message
     */
    public static void deleteMessage(String senderID, String recipientID, long order) {
        Message[] messages = Account.getMessages(Account.toUserID(senderID));
        for (Message message : messages) {
            if ((message.senderID.equals(senderID) && message.recipientID.equals(recipientID)) && message.order == order) {
                message.deletedForSender = true;
                break;
            } else if (message.senderID.equals(recipientID) && message.recipientID.equals(senderID) && message.order == order) {
                message.deletedForRecipient = true;
                break;
            }
        }
        Account.setMessages(Account.toUserID(senderID), messages);

        messages = Account.getMessages(Account.toUserID(recipientID));
        for (Message message : messages) {
            if ((message.senderID.equals(senderID) && message.recipientID.equals(recipientID)) && message.order == order) {
                message.deletedForSender = true;
                break;
            } else if (message.senderID.equals(recipientID) && message.recipientID.equals(senderID) && message.order == order) {
                message.deletedForRecipient = true;
                break;
            }
        }
        Account.setMessages(Account.toUserID(recipientID), messages);
    }
}
