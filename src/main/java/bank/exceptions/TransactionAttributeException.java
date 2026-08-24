package bank.exceptions;

public class TransactionAttributeException extends Exception {
    public TransactionAttributeException(String ausgabe) {
        super(ausgabe);
    }
}
