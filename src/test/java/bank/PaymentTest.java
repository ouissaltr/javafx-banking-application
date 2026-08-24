/**
 * Testklasse für {@link Payment}.
 * Enthält Tests für Konstruktoren, Berechnungen, equals und toString.
 */
package bank;

import bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für Payment.
 */
public class PaymentTest {

    Payment p1;
    Payment p2;

    /**
     * Bereitet vor jedem Test zwei Payment-Objekte vor.
     *
     * @throws TransactionAttributeException falls ungültige Attribute gesetzt werden
     */
    @BeforeEach
    void setup() throws TransactionAttributeException {
        // Your constructor: Payment(String date, double amount, String description, double incomingInterest, double outgoingInterest)
        p1 = new Payment("2023-01-01", 100.0, "Test Payment", 0.1, 0.2);
        p2 = new Payment(p1); // copy constructor
    }

    /**
     * Wird nach jedem Test ausgeführt.
     * Da Payment keine Dateien nutzt, ist hier nichts aufzuräumen.
     */
    @AfterEach
    void cleanup() {
        // Payment has no files → nothing to clean
    }

    /**
     * Testet den Hauptkonstruktor von Payment.
     *
     * @throws TransactionAttributeException falls ungültige Werte übergeben wurden
     */
    @Test
    void testConstructor() throws TransactionAttributeException {
        Payment p = new Payment("2023-01-01", 150, "Example", 0.05, 0.10);

        assertEquals("2023-01-01", p.getDate());
        assertEquals(150, p.getAmount());
        assertEquals("Example", p.getDescription());
        assertEquals(0.05, p.getIncomingInterest());
        assertEquals(0.10, p.getOutgoingInterest());
    }

    /**
     * Testet den Copy-Konstruktor.
     */
    @Test
    void testCopyConstructor() {
        assertEquals(p1, p2);
        assertNotSame(p1,p2);
    }

    /**
     * Testet die Berechnung einer eingehenden Zahlung.
     * Formel: amount - (amount * incomingInterest)
     *
     * @throws TransactionAttributeException falls ungültige Werte verwendet wurden
     */
    @Test
    void testCalculateIncoming() throws TransactionAttributeException {
        Payment incoming = new Payment("2023-01-01", 200, "Incoming", 0.10, 0.20);
        // Formula: amount - (amount * incomingInterest)
        assertEquals(200 - 20, incoming.calculate());
    }

    /**
     * Testet die Berechnung einer ausgehenden Zahlung.
     * Formel: amount + (amount * outgoingInterest)
     *
     * @throws TransactionAttributeException falls ungültige Werte verwendet wurden
     */
    @Test
    void testCalculateOutgoing() throws TransactionAttributeException {
        Payment outgoing = new Payment("2023-01-01", -200, "Outgoing", 0.10, 0.20);
        // Formula: amount + (amount * outgoingInterest)
        assertEquals(-200 + (-200 * 0.20), outgoing.calculate());
    }

    /**
     * Testet die equals()-Methode.
     */
    @Test
    void testEquals() {
        assertEquals(p1, p2);

    }

    /**
     * Testet, ob toString() relevante Informationen enthält.
     */
    @Test
    void testToString() {
        String s = p1.toString();
        assertTrue(s.contains("Test Payment"));
        assertTrue(s.contains("incomingInterest"));
        assertTrue(s.contains("outgoingIntrest")); // note your typo: outgoingIntrest
    }
}