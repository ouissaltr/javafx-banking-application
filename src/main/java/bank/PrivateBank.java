package bank;

import bank.exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;

// Gson imports
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * PrivateBank with JSON persistence
 */
public class PrivateBank implements Bank {

    private String name;
    private double incomingInterest;
    private double outgoingInterest;

    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    // directory for JSON files
    private String directoryName;

    // Gson with adapter um gson.tojson___ from json verwenden können
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Transaction.class, new TransactionAdapter())
            .create();

    // ======== getters used by tests ========

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    public String getName() {
        return name;
    }

    // ======== constructors ========

    // Constructor WITH persistence
    public PrivateBank(String name, double incomingInterest, double outgoingInterest, String directoryName)
            throws IOException {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
        this.directoryName = directoryName;

        readAccounts(); // load accounts from JSON
    }

    // No-persistence constructor
    public PrivateBank(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        this.incomingInterest = incomingInterest;
        this.outgoingInterest = outgoingInterest;
    }

    // Copy constructor
    public PrivateBank(PrivateBank other) {
        this.name = other.name;
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
        this.directoryName = other.directoryName;
        this.accountsToTransactions = new HashMap<>(other.accountsToTransactions);
    }

    // =============== PERSISTENCE ===============

    /**
     * Reads all account JSON files from directoryName into accountsToTransactions.
     */
    private void readAccounts() throws IOException {
        if (directoryName == null)
            return;

        File directory = new File(directoryName);
        if (!directory.exists())
            return;

        File[] jsonFiles = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (jsonFiles == null)
            return;

        for (File file : jsonFiles) {
            String filename = file.getName();
            String accountName = filename.substring(0, filename.length() - 5); // strip ".json"

            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<Transaction>>() {}.getType();
                List<Transaction> transactions = gson.fromJson(reader, listType);
                accountsToTransactions.put(accountName, transactions);
            }
        }
    }

    /**
     * Writes the given account's transaction list to its JSON file.
     * Uses List<Transaction> type so TransactionAdapter is applied.
     */
    private void writeAccount(String account) throws IOException {
        if (directoryName == null)
            return;

        if (!accountsToTransactions.containsKey(account))
            return;

        File directory = new File(directoryName);
        if (!directory.exists())
            directory.mkdirs();

        File file = new File(directory, account + ".json");

        try (Writer writer = new FileWriter(file)) {
            Type listType = new TypeToken<List<Transaction>>() {}.getType();
            gson.toJson(accountsToTransactions.get(account), listType, writer);
        }
    }

    // =============== BANK OPERATIONS ===============

    @Override
    public void createAccount(String account)
            throws AccountAlreadyExistsException, IOException {

        if (accountsToTransactions.containsKey(account))
            throw new AccountAlreadyExistsException("Account already exists: " + account);

        accountsToTransactions.put(account, new ArrayList<>());

        writeAccount(account);
    }

    @Override
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException,
            TransactionAttributeException, IOException {

        if (accountsToTransactions.containsKey(account))
            throw new AccountAlreadyExistsException("Account already exists: " + account);

        List<Transaction> list = new ArrayList<>();

        for (Transaction t : transactions) {
            if (list.contains(t))
                throw new TransactionAlreadyExistException("Duplicate transaction: " + t);
            list.add(t);
        }

        accountsToTransactions.put(account, list);

        writeAccount(account);
    }

    @Override
    public void addTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionAlreadyExistException,
            TransactionAttributeException, IOException {

        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Account not found: " + account);

        List<Transaction> list = accountsToTransactions.get(account);

        if (list.contains(transaction))
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);

        // apply bank interest to Payment transactions
        if (transaction instanceof Payment) {
            Payment p = (Payment) transaction;
            p.setIncomingInterest(incomingInterest);
            p.setOutgoingInterest(outgoingInterest);
        }

        list.add(transaction);

        writeAccount(account);
    }

    @Override
    public void removeTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {

        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("Account not found: " + account);

        List<Transaction> list = accountsToTransactions.get(account);

        if (!list.contains(transaction))
            throw new TransactionDoesNotExistException("Transaction not found: " + transaction);

        list.remove(transaction);

        writeAccount(account);
    }

    // =============== REMAINING METHODS ===============

    @Override
    public boolean containsTransaction(String account, Transaction t) {
        return accountsToTransactions.containsKey(account)
                && accountsToTransactions.get(account).contains(t);
    }

    @Override
    public double getAccountBalance(String account) {
        if (!accountsToTransactions.containsKey(account))
            return 0;

        double sum = 0;
        for (Transaction t : accountsToTransactions.get(account))
            sum += t.calculate();

        return sum;
    }

    @Override
    public List<Transaction> getTransactions(String account) {
        if (!accountsToTransactions.containsKey(account))
            return new ArrayList<>();

        return new ArrayList<>(accountsToTransactions.get(account));
    }

    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> list = getTransactions(account);
        list.sort((a, b) -> Double.compare(a.calculate(), b.calculate()));

        if (!asc)
            Collections.reverse(list);

        return list;
    }

    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> result = new ArrayList<>();

        if (!accountsToTransactions.containsKey(account))
            return result;

        for (Transaction t : accountsToTransactions.get(account)) {
            double v = t.calculate();
            if ((positive && v >= 0) || (!positive && v < 0))
                result.add(t);
        }

        return result;
    }

    @Override
    public String toString() {
        return "PrivateBank{name=" + name +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", accounts=" + accountsToTransactions.keySet() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PrivateBank other)) return false;

        return name.equals(other.name)
                && incomingInterest == other.incomingInterest
                && outgoingInterest == other.outgoingInterest
                && accountsToTransactions.equals(other.accountsToTransactions);
    }
    @Override
    public List<String> getAllAccounts() {
        List<String> list = new ArrayList<>(accountsToTransactions.keySet());
//        Collections.sort(list);   // optional, aber hilfreich für GUI
        return list;
    }
    @Override
    public void deleteAccount(String account)
            throws AccountDoesNotExistException, IOException {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(
                    "Account existiert nicht: " + account);
        }

        // Datei löschen (z. B. JSON oder TXT)
        File file = new File(directoryName + "/" + account + ".json");

        if (file.exists() && !file.delete()) {
            throw new IOException("Konnte Account-Datei nicht löschen: " + file.getName());
        }

        // Aus interner Map löschen
        accountsToTransactions.remove(account);
    }


}
