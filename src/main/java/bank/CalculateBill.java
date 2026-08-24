package bank;
/**
 * Dieses Interface wird von Klassen verwendet, die einen Betrag berechnen sollen.
 * <p>
 * Die Methode {@code calculate()} gibt den berechneten Betrag einer Transaktion zurück.
 * </p>
 */
public interface CalculateBill {
    /**
     * Berechnet den Betrag einer Transaktion.
     * <p>
     * Bei {@link Payment} werden Zinsen berücksichtigt, bei {@link Transfer} bleibt der Betrag gleich.
     * </p>
     *
     * @return der berechnete Betrag als double
     */
    double calculate();
}
//Zeige den Text calculate() in Code-Format an.