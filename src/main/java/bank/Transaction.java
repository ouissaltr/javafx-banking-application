package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Abstrakte Basisklasse Transaction.
 * Repräsentiert eine allgemeine Transaktion mit Datum, Betrag und Beschreibung.
 * Payment und Transfer erben von dieser Klasse.
 */

public abstract class Transaction implements CalculateBill{
    protected String description;
    protected double amount;
    protected String date;
    /**
     * Konstruktor mit allen Attributen.
     * @param date Datum der Transaktion
     * @param amount Betrag der Transaktion
     * @param description Beschreibung der Transaktion
     */
    public Transaction(String date, double amount, String description) throws TransactionAttributeException {
        this.date = date;

        setAmount(amount);
        this.description = description;
    }/**
     * Setzt die Beschreibung der Transaktion.
     * @param D neue Beschreibung
     */
     public void setDescription(String D){
        this.description = D;
    }
    /**
     * Setzt das Datum der Transaktion.
     * @param d neues Datum
     */
    public void setDate(String d){
        this.date = d;
    }
    /**
     * Setzt den Betrag der Transaktion.
     * @param a neuer Betrag
     */
    public void setAmount(double a) throws TransactionAttributeException{

        this.amount = a;
    }
    /**
     * Gibt die Beschreibung der Transaktion zurück.
     * @return Beschreibung
     */
    public String getDescription(){
        return this.description;

    }
    /**
     * Gibt das Datum der Transaktion zurück.
     * @return Datum
     */
    public double getAmount(){
        return this.amount;

    }
    /**
     * Gibt das Datum der Transaktion zurück.
     * @return Datum
     */
    public String getDate(){
        return this.date;
    }/**
     * Copy-Konstruktor.
     * Erstellt eine neue Transaktion als Kopie einer anderen.
     * @param other die zu kopierende Transaktion
     */
   public Transaction(Transaction other){
        this.date = other.date;
        this.amount = other.amount;
        this.description = other.description;
}
    /**
     * Gibt eine lesbare Darstellung der Transaktion zurück.
     * Überschreibt Object.toString().
     * @return String-Darstellung der Transaktion
     */
    @Override
    public String toString(){
        return "date: " + date +
              "amount: " +this.calculate()+
                "\ndescription: " + description;
    }
    /**
     * Vergleicht diese Transaktion mit einem anderen Objekt.
     * Überschreibt Object.equals().
     * @param other das zu vergleichende Objekt
     * @return true, wenn beide Transaktionen gleich sind, sonst false
     */
    @Override
    public boolean equals(Object other){
        if(other == null) return false;//das Vergleichsobjekt null ist,
        if(other == this) return true;//ein Objekt mit sich selbst vergleichen,
        if (getClass() != other.getClass()) return false;//sicherstellen, dass beide Objekte von genau derselben Klasse sind.
        Transaction t = (Transaction)other;
        return this.date.equals(t.date)
                && this.description.equals(t.description)&& this.amount == t.amount;


    }



    }
