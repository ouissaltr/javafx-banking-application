package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Die Klasse {@code Transfer} repräsentiert eine Banküberweisung.
 * Sie enthält sender und recipient.
 * Diese Klasse erbt von der abstrakten Klasse {@link Transaction}
 * und implementiert das Interface {@link CalculateBill}.
 */
public class Transfer extends Transaction {

    private String recipient;
    private String sender;

    public String getRecipient() { return recipient; }
    public String getSender() { return sender; }

    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setSender(String sender) { this.sender = sender; }

    @Override
    public void setAmount(double amount) throws TransactionAttributeException {
        if (amount >= 0) {
            this.amount = amount;
        } else {
            throw new TransactionAttributeException("Amount darf nicht negativ sein!");
        }
    }

    public Transfer(String date, double amount, String description) throws TransactionAttributeException {
        super(date, amount, description); // call parent constructor
        setAmount(amount);            // now validate using the setter
    }


    public Transfer(String date, double amount, String description, String recipient, String sender)
            throws TransactionAttributeException {
        this(date, amount, description); // call main constructor
        this.recipient = recipient;
        this.sender = sender;
    }

    public Transfer(Transfer other) throws TransactionAttributeException {
        this(other.date, other.amount, other.description, other.recipient, other.sender);
    }

    @Override
    public double calculate() {
        return amount;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nAmount: " + this.calculate() +
                "\nRecipient: " + getRecipient() +
                "\nSender: " + getSender();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) return false;
        if (!(other instanceof Transfer)) return false;
        Transfer tr = (Transfer) other;
        return this.recipient.equals(tr.recipient) && this.sender.equals(tr.sender);
    }
}
