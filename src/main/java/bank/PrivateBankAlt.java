package bank;

import bank.exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;

/**
 * Die Klasse {@code PrivateBankAlt} implementiert das Interface {@link Bank} und
 * repräsentiert eine Bank, die Konten, Transaktionen und Zinssätze verwaltet.
 * <p>
 * Jedes Konto wird durch einen eindeutigen Namen identifiziert und kann mehrere
 * {@link Transaction}-Objekte enthalten.
 * </p>
 * <p>
 * Die Klasse bietet Methoden zum Erstellen von Konten, Hinzufügen, Entfernen und
 * Abfragen von Transaktionen sowie zum Berechnen des Kontostands. Transaktionen
 * können nach Wert sortiert oder nach Typ (positiv oder negativ) gefiltert werden.
 * Außerdem verwaltet die Bank eingehende und ausgehende Zinssätze für Zahlungen.
 * </p>
 */
public class PrivateBankAlt implements Bank {

    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    /**
     * Gibt den Namen der Bank zurück.
     *
     * @return der Name der Bank
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen der Bank.
     *
     * @param name der neue Name der Bank
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setzt den Zinssatz für eingehende Zahlungen.
     *
     * @param incomingInterest der Zinssatz (0.0 bis 1.0)
     */
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest >= 0 && incomingInterest <= 1) {
            this.incomingInterest = incomingInterest;
        } else {
            System.out.println("Fehler: incomingInterest muss zwischen 0 und 1 liegen!");
        }
    }

    /**
     * Setzt den Zinssatz für ausgehende Zahlungen.
     *
     * @param outgoingInterest der Zinssatz (0.0 bis 1.0)
     */
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest >= 0 && outgoingInterest <= 1) {
            this.outgoingInterest = outgoingInterest;
        } else {
            System.out.println("Fehler: outgoingInterest muss zwischen 0 und 1 liegen!");
        }
    }

    /**
     * Gibt den Zinssatz für ausgehende Zahlungen zurück.
     *
     * @return der Zinssatz für ausgehende Zahlungen
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Gibt den Zinssatz für eingehende Zahlungen zurück.
     *
     * @return der Zinssatz für eingehende Zahlungen
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Konstruktor zur Erstellung einer neuen Bank mit Name und Zinssätzen.
     *
     * @param name             der Name der Bank
     * @param incomingInterest Zinssatz für eingehende Zahlungen
     * @param outgoingInterest Zinssatz für ausgehende Zahlungen
     */
    public PrivateBankAlt(String name, double incomingInterest, double outgoingInterest) {
        this.name = name;
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * Kopierkonstruktor. Erstellt eine neue Bank basierend auf einer bestehenden Bank.
     *
     * @param other die Bank, die kopiert werden soll
     */
    public PrivateBankAlt(PrivateBankAlt other) {
        this.name = other.name;
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
    }

    @Override
    public String toString() {
        return "PrivateBank{" +
                "name='" + name + '\'' +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", accountsToTransactions=" + accountsToTransactions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateBankAlt other = (PrivateBankAlt) o;
        return this.name.equals(other.name)
                && this.incomingInterest == other.incomingInterest
                && this.outgoingInterest == other.outgoingInterest
                && this.accountsToTransactions.equals(other.accountsToTransactions);
    }

    /**
     * Erstellt ein neues, leeres Konto.
     *
     * @param account der Name des Kontos
     * @throws AccountAlreadyExistsException wenn das Konto bereits existiert
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }
        accountsToTransactions.put(account, new ArrayList<>());
    }

    /**
     * Erstellt ein neues Konto mit einer Liste von Anfangstransaktionen.
     *
     * @param account      der Name des Kontos
     * @param transactions die Anfangstransaktionen
     * @throws AccountAlreadyExistsException    wenn das Konto bereits existiert
     * @throws TransactionAlreadyExistException wenn doppelte Transaktionen vorhanden sind
     * @throws TransactionAttributeException   wenn eine Transaktion null oder ungültig ist
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException {

        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }

        List<Transaction> theList = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t == null) {
                throw new TransactionAttributeException("Transaction darf nicht null sein");
            }

            if (theList.contains(t)) {
                throw new TransactionAlreadyExistException("Transaktion existiert bereits: " + t);
            } else {
                theList.add(t);
            }
        }

        accountsToTransactions.put(account, theList);
    }

    /**
     * Prüft, ob eine bestimmte Transaktion im Konto existiert.
     *
     * @param account     der Name des Kontos
     * @param transaction die zu prüfende Transaktion
     * @return {@code true}, wenn die Transaktion existiert, sonst {@code false}
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        if (!accountsToTransactions.containsKey(account)) {
            return false;
        }
        return accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * Berechnet den Kontostand eines Kontos, unter Berücksichtigung von Payments und Transfers.
     *
     * @param account der Name des Kontos
     * @return der Kontostand, 0 wenn das Konto nicht existiert
     */
    @Override
    public double getAccountBalance(String account) {
        if (!accountsToTransactions.containsKey(account)) {
            return 0.0;
        }

        double balance = 0;
        List<Transaction> accountTransactions = accountsToTransactions.get(account);

        for (Transaction t : accountTransactions) {
            if (t instanceof Payment) {
                balance += t.calculate();
            } else if (t instanceof Transfer) {
                Transfer trans = (Transfer) t;
                if (trans.getSender().equals(account)) {
                    balance -= trans.getAmount();
                } else if (trans.getRecipient().equals(account)) {
                    balance += trans.getAmount();
                }
            }
        }
        return balance;
    }

    /**
     * Gibt alle Transaktionen eines Kontos zurück.
     *
     * @param account der Name des Kontos
     * @return eine Liste der Transaktionen oder eine leere Liste, wenn das Konto nicht existiert
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        if (accountsToTransactions.containsKey(account)) {
            return accountsToTransactions.get(account);
        }
        return new ArrayList<>();
    }

    /**
     * Gibt alle Transaktionen eines Kontos sortiert nach Wert zurück.
     *
     * @param account der Name des Kontos
     * @param asc     {@code true} für aufsteigende Reihenfolge, {@code false} für absteigende Reihenfolge
     * @return eine sortierte Liste der Transaktionen oder eine leere Liste, wenn das Konto nicht existiert
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        if (!accountsToTransactions.containsKey(account)) {
            return new ArrayList<>();
        }

        List<Transaction> sortedList = new ArrayList<>(accountsToTransactions.get(account));
        sortedList.sort((t1, t2) -> Double.compare(t1.calculate(), t2.calculate()));

        if (!asc) {
            Collections.reverse(sortedList);
        }
        return sortedList;
    }

    /**
     * Gibt Transaktionen eines Kontos nach Typ gefiltert zurück (positiv oder negativ).
     *
     * @param account  der Name des Kontos
     * @param positive {@code true} für positive Transaktionen, {@code false} für negative
     * @return eine gefilterte Liste der Transaktionen
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        if (!accountsToTransactions.containsKey(account)) {
            return new ArrayList<>();
        }

        List<Transaction> allTransactions = accountsToTransactions.get(account);
        List<Transaction> filtered = new ArrayList<>();

        for (Transaction t : allTransactions) {
            double value = t.calculate();
            if (positive && value > 0) {
                filtered.add(t);
            } else if (!positive && value < 0) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    /**
     * Fügt eine Transaktion zu einem Konto hinzu.
     *
     * @param account     der Name des Kontos
     * @param transaction die hinzuzufügende Transaktion
     * @throws AccountDoesNotExistException     wenn das Konto nicht existiert
     * @throws TransactionAlreadyExistException wenn die Transaktion bereits existiert
     * @throws TransactionAttributeException   wenn die Transaktion ungültig ist
     */
    @Override
    public void addTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionAlreadyExistException, TransactionAttributeException {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Konto existiert nicht: " + account);
        }

        List<Transaction> list = accountsToTransactions.get(account);

        if (list.contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaktion existiert bereits im Konto: " + account);
        }

        if (transaction instanceof Payment) {
            Payment p = (Payment) transaction;
            p.setIncomingInterest(this.incomingInterest);
            p.setOutgoingInterest(this.outgoingInterest);
            list.add(p);
        } else if (transaction instanceof Transfer) {
            list.add(new Transfer((Transfer) transaction));
        }
    }

    /**
     * Entfernt eine Transaktion aus einem Konto.
     *
     * @param account     der Name des Kontos
     * @param transaction die zu entfernende Transaktion
     * @throws AccountDoesNotExistException    wenn das Konto nicht existiert
     * @throws TransactionDoesNotExistException wenn die Transaktion im Konto nicht existiert
     */
    @Override
    public void removeTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionDoesNotExistException {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Konto existiert nicht: " + account);
        }

        List<Transaction> transactions = accountsToTransactions.get(account);

        if (!transactions.contains(transaction)) {
            throw new TransactionDoesNotExistException("Transaktion existiert nicht im Konto: " + account);
        }

        transactions.remove(transaction);
    }
    @Override
    public List<String> getAllAccounts() {
        // Liste aller Account-Namen aus der Map
        List<String> list = new ArrayList<>(accountsToTransactions.keySet());
        Collections.sort(list); // optional sortiert
        return list;
    }
    @Override
    public void deleteAccount(String account)
            throws AccountDoesNotExistException, IOException {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException(
                    "Account existiert nicht: " + account);
        }

        // Nur aus der Map löschen – keine JSON-Dateien in Alt-Version
        accountsToTransactions.remove(account);
    }

}
