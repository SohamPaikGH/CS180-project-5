public class Message {
    private String senderID;        // ID of the sender of the message
    private String recipientID;     // ID of the recipient of the message
    private String message;         // the message
    private boolean deleted;        // true if the sender deleted it
    private long order;              // its order in the conversation it's in

    /**
     * Instantiates a message object with the given fields
     * @param senderID ID of the sender of the message
     * @param recipientID ID of the recipient of the message
     * @param message the message
     * @param deleted true if the sender deleted it
     * @param order its order in the conversation it's in
     */
    public Message(String senderID, String recipientID, String message, boolean deleted, long order) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
        this.deleted = deleted;
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

    /**
     * Returns deleted
     * @return deleted
     */
    public boolean isDeleted() {
        return deleted;
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
        Message[] messages = Account.getMessages(senderID);
        long conversationLength = 0;
        for (Message message : messages) {
            if (message.recipientID.equals(recipientID)) {
                conversationLength++;
            }
        }
        return conversationLength;
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
}
