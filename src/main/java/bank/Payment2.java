package bank;

import bank.exceptions.TransactionAttributeException;

public class Payment2 extends Transaction{
    private int priority ;
    Payment2(String date,double amount,String description,int priority)throws TransactionAttributeException {
        super(date,amount,description);
        this.priority=priority;

    }

    @Override
    public double calculate() {
        return amount;
    }
}
