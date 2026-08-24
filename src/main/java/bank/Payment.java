package bank;// Package wo wir unseren kalssen liegen

import bank.exceptions.TransactionAttributeException;

/**
 * Die Klasse {@code Payment} repräsentiert eine Bankzahlung.
 * Sie enthält das Datum, den Betrag, eine Beschreibung sowie eingehende und ausgehende Zinsen.
 * <p>
 * Diese Klasse erbt von der abstrakten Klasse {@link Transaction}
 * und implementiert das Interface {@link CalculateBill}.
 * Die Methode {@link #calculate()} berechnet den effektiven Betrag nach Zinsen.
 * </p>
 *///anklickbare Verknüpfung (Link) zu einer anderen Klasse, Methode oder Variable herzustellen.
public class Payment extends Transaction  {

    private double incomingInterest;// Zinssatz für eingehende Zahlungen (0 bis 1)
    private double outgoingInterest;// Zinssatz für ausgehende Zahlungen (0 bis 1)
    /**
     * Gibt den Zinssatz für eingehende Zahlungen zurück.
     *
     * @return Zinssatz für eingehende Zahlungen
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }
    /**
     * Gibt den Zinssatz für ausgehende Zahlungen zurück.
     *
     * @return Zinssatz für ausgehende Zahlungen
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Setzt den Zinssatz für eingehende Zahlungen.
     * Der Wert muss zwischen 0 und 1 liegen.
     *
     * @param incomingInterest Zinssatz für eingehende Zahlungen
     */
    public void setIncomingInterest(double incomingInterest)throws TransactionAttributeException {
        if (incomingInterest >= 0 && incomingInterest <= 1) {
            this.incomingInterest = incomingInterest;
        } else {
           throw new TransactionAttributeException("invalid incoming interest");
        }
    }
    /**
     * Setzt den Zinssatz für ausgehende Zahlungen.
     * Der Wert muss zwischen 0 und 1 liegen.
     *
     * @param outgoingInterest Zinssatz für ausgehende Zahlungen
     */
    public void setOutgoingInterest(double outgoingInterest)throws TransactionAttributeException {
        if (outgoingInterest >=0 && outgoingInterest <= 1) {
            this.outgoingInterest = outgoingInterest;
        } else {
            throw new TransactionAttributeException("invalid outgoing interest");
        }
    }
    /**
     * Konstruktor mit Datum, Betrag und Beschreibung.
     *
     * @param date        Datum der Zahlung
     * @param amount      Betrag der Zahlung
     * @param description Beschreibung der Zahlung
     */
    public Payment(String date, double amount, String description) throws TransactionAttributeException{
      super(date, amount, description);
    }

    /**
     * Konstruktor mit date, amount, description, incomingInterest und outgoingInterest.
     *
     * @param date            Datum der Zahlung
     * @param amount          Betrag der Zahlung
     * @param description     Beschreibung der Zahlung
     * @param incomingInterest Zinssatz für eingehende Zahlungen
     * @param outgoingInterest Zinssatz für ausgehende Zahlungen
     */
    public Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest) throws TransactionAttributeException{
        this(date, amount, description); // ruft Konstruktor 1 auf
     setOutgoingInterest(outgoingInterest);
     setIncomingInterest(incomingInterest);

    }

    /**
     * Kopierkonstruktor: erstellt eine Kopie eines vorhandenen Payment-Objekts.
     *
     * @param other das zu kopierende Payment-Objekt
     */
    public Payment(Payment other) throws TransactionAttributeException{
        super(other);
        setIncomingInterest(other.incomingInterest);
        setOutgoingInterest(other.outgoingInterest);
    }
    /**
     * Gibt eine Textdarstellung des Payment-Objekts zurück.
     * Enthält die allgemeinen Transaktionsdaten (von der Oberklasse)
     * sowie den berechneten amount und die Zinsen.
     *
     * @return eine Zeichenkette mit allen wichtigen Informationen über die Zahlung
     */
  @Override
  public String toString() {
      return
       super.toString()+
      "\namount: "+ this.calculate()+
       "\nincomingInterest: " +getIncomingInterest()+
       "\noutgoingIntrest: "+getOutgoingInterest();
  }

    /**
     * Berechnet den endgültigen Betrag nach Anwendung der Zinsen.
     * Wenn amount positiv ist, wird incoming interest abgezogen.
     * Wenn amount negativ ist, wird outgoing interest addiert.
     *
     * @return der berechnete amount
     */
    @Override
    public double calculate() {
        if (amount > 0) {
            return amount -(amount * incomingInterest);
        }
        else {
            return amount + (amount * outgoingInterest);
        }

    }
    /**
     * Überprüft, ob diese Zahlung gleich einem anderen Objekt ist.
     * Zwei Payment-Objekte sind gleich, wenn Datum, Beschreibung,
     * incomingInterest und outgoingInterest identisch sind.
     *
     * @param other das zu vergleichende Objekt
     * @return true, wenn die Objekte gleich sind, sonst false
     */
    @Override
    public boolean equals(Object other) {

        if (!super.equals(other)) return false;//wird zuerst die equals()-Methode der Oberklasse (Transaction) aufgerufen.
        Payment p=(Payment) other;//casten, damit wir auf incomingInterest und outgoingInterest zugreifen können.
        return  this.incomingInterest==p.incomingInterest && this.outgoingInterest==p.outgoingInterest  ;
    }






}

