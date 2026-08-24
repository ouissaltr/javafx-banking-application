package bank;

import bank.exceptions.TransactionAttributeException;

public class OutgoingTransfer extends Transfer {

    public OutgoingTransfer(Transfer t) throws TransactionAttributeException {
        super(t);
    }
    public OutgoingTransfer(String date,double amount,String description,String recipient,String sender)throws TransactionAttributeException
    {
        super(date,amount,description,recipient,sender);
    }
    @Override
    public double calculate() {
        // For outgoing transfers, we subtract the amount (negative)
        return -getAmount();
    }
}
