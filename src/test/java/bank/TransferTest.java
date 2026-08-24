/**
 * Testklasse für {@link Transfer} sowie IncomingTransfer und OutgoingTransfer.
 * Überprüft Konstruktoren, Kopierverhalten, Berechnungsmethoden,
 * equals() und toString().
 */
package bank;

import org.junit.jupiter.api.*;
import bank.exceptions.TransactionAttributeException;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {

    Transfer t1;
    Transfer t2;

    /**
     * Richtet vor jedem Test zwei Transfer-Objekte ein,
     * eines regulär und eines per Copy-Konstruktor.
     */
    @BeforeEach
    void setup() throws TransactionAttributeException {
        // Transfer(String date, double amount, String description, String recipient, String sender)
        t1 = new Transfer("2023-01-01", 300.0, "Salary", "Bob", "Alice");
        t2 = new Transfer(t1);
    }

    /**
     * Nachbereitung nach jedem Test.
     * Transfer besitzt keine Persistenz, daher nichts zu reinigen.
     */
    @AfterEach
    void cleanup() {
        // nothing to clean
    }

    /**
     * Testet den Hauptkonstruktor von Transfer.
     */
    @Test
    void testConstructor() throws TransactionAttributeException {
        Transfer t = new Transfer("2023-01-01", 100, "Gift", "Sarah", "John");

        assertEquals("2023-01-01", t.getDate());
        assertEquals(100, t.getAmount());
        assertEquals("Gift", t.getDescription());
        assertEquals("Sarah", t.getRecipient());
        assertEquals("John", t.getSender());
    }

    /**
     * Testet den Copy-Konstruktor von Transfer.
     */
    @Test
    void testCopyConstructor() throws TransactionAttributeException {
        assertEquals(t1, t2);
    }

    /**
     * Testet die calculate()-Methode eines normalen Transfers.
     * Ein Transfer gibt immer den ursprünglichen Betrag zurück.
     */
    @Test
    void testCalculate() {
        assertEquals(300.0, t1.calculate());
    }

    /**
     * Testet die calculate()-Methode eines IncomingTransfer.
     * Der Betrag bleibt positiv.
     */
    @Test
    void testIncomingTransferCalculate() throws TransactionAttributeException {
        IncomingTransfer it = new IncomingTransfer(
                "2023-01-01",
                400,
                "Refund",
                "Shop",
                "Alice"
        );

        assertEquals(400, it.calculate());
    }

    /**
     * Testet die calculate()-Methode eines OutgoingTransfer.
     * Der Betrag wird negativ zurückgegeben.
     */
    @Test
    void testOutgoingTransferCalculate() throws TransactionAttributeException {
        OutgoingTransfer ot = new OutgoingTransfer(
                "2023-01-01",
                400,
                "Rent",
                "Alice",
                "Landlord"
        );

        assertEquals(-400, ot.calculate());
    }

    /**
     * Testet die equals()-Methode der Transfer-Klasse.
     */
    @Test
    void testEquals() {
        assertEquals(t1, t2);
    }

    /**
     * Testet toString() eines Transfers.
     */
    @Test
    void testToString() {
        String str = t1.toString();

        assertTrue(str.contains("Salary"));
        assertTrue(str.contains("Bob"));   // recipient
        assertTrue(str.contains("Alice")); // sender
    }
}
