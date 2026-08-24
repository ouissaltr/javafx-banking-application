package bank;

import bank.exceptions.TransactionAttributeException;

public class IncomingTransfer extends Transfer {

    public IncomingTransfer(Transfer t) throws TransactionAttributeException
            {
        super(t);  // call copy-constructor from Transfer
    }
    public IncomingTransfer(String date,double amount,String description,String recipient,String sender)throws TransactionAttributeException
        {
        super(date,amount,description,recipient,sender);
        }
    @Override
    public double calculate() {
        // For outgoing transfers, we subtract the amount (negative)
        return getAmount();
    }
}
