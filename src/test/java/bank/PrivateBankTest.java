/**
 * Testklasse für {@link PrivateBank}.
 * Enthält Tests zu Konstruktoren, Kontoerstellung, Transaktionen,
 * Kontostand, Sortierung, Filterung, equals, toString und Persistenz.
 */
package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PrivateBankTest {

    PrivateBank bank;
    String testDir = "testdata";

    Payment p1;
    IncomingTransfer it;
    OutgoingTransfer ot;

    /**
     * Richtet vor jedem Test eine neue PrivateBank-Instanz
     * und frische Testdaten ein.
     */
    @BeforeEach
    void setup() throws Exception {
        bank = new PrivateBank("TestBank", 0.1, 0.2, testDir);

        p1 = new Payment("2023-01-01", 100, "Salary", 0.1, 0.2);
        it = new IncomingTransfer("2023-01-01", 200, "Gift", "Bob", "Alice");
        ot = new OutgoingTransfer("2023-01-01", 300, "Rent", "Landlord", "Alice");

        File dir = new File(testDir);
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Löscht nach jedem Test die Testdaten.
     */
    @AfterEach
    void cleanup() {
        File dir = new File(testDir);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
            dir.delete();
        }
    }

    /**
     * Testet den Konstruktor von PrivateBank.
     */
    @Test
    void testConstructor() {
        assertEquals("TestBank", bank.getName());
        assertEquals(0.1, bank.getIncomingInterest());
        assertEquals(0.2, bank.getOutgoingInterest());
    }

    /**
     * Testet das Anlegen eines neuen Accounts.
     */
    @Test
    void testCreateAccount() throws Exception {
        assertDoesNotThrow(() -> bank.createAccount("Account1"));

        File f = new File(testDir + "/Account1.json");
        assertTrue(f.exists(), "Account JSON file should be created.");
    }

    /**
     * Testet, ob das erneute Anlegen eines bestehenden Accounts
     * korrekt eine Exception wirft.
     */
    @Test
    void testCreateAccountAlreadyExists() throws Exception {
        bank.createAccount("Acc1");

        assertThrows(AccountAlreadyExistsException.class,
                () -> bank.createAccount("Acc1"));
    }

    /**
     * Testet das Anlegen eines Accounts mit vorhandenen Transaktionen.
     */
    @Test
    void testCreateAccountWithTransactions() throws Exception {
        List<Transaction> list = List.of(p1, it);

        bank.createAccount("Acc2", list);

        assertEquals(2, bank.getTransactions("Acc2").size());
    }

    /**
     * Testet das Hinzufügen einer Transaktion.
     */
    @Test
    void testAddTransaction() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertTrue(bank.containsTransaction("A", p1));

        File f = new File(testDir + "/A.json");
        assertTrue(f.exists());
    }

    /**
     * Testet Hinzufügen zu einem nicht existierenden Account.
     */
    @Test
    void testAddTransactionToMissingAccount() {
        assertThrows(AccountDoesNotExistException.class,
                () -> bank.addTransaction("Missing", p1));
    }

    /**
     * Testet das Verhalten bei doppelten Transaktionen.
     */
    @Test
    void testAddDuplicateTransaction() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertThrows(TransactionAlreadyExistException.class,
                () -> bank.addTransaction("A", p1));
    }

    /**
     * Testet das Entfernen einer Transaktion.
     */
    @Test
    void testRemoveTransaction() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);

        assertDoesNotThrow(() -> bank.removeTransaction("A", p1));
        assertFalse(bank.containsTransaction("A", p1));
    }

    /**
     * Testet Entfernen nicht existierender Transaktionen.
     */
    @Test
    void testRemoveMissingTransaction() throws Exception {
        bank.createAccount("A");

        assertThrows(TransactionDoesNotExistException.class,
                () -> bank.removeTransaction("A", p1));
    }

    /**
     * Testet die Berechnung des Kontostands.
     */
    @Test
    void testGetAccountBalance() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", it);
        bank.addTransaction("A", ot);

        double expected = p1.calculate() + it.calculate() + ot.calculate();
        assertEquals(expected, bank.getAccountBalance("A"));
    }

    /**
     * Testet das Auslesen der Transaktionen eines Accounts.
     */
    @Test
    void testGetTransactions() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", it);

        List<Transaction> list = bank.getTransactions("A");

        assertEquals(2, list.size());
        assertTrue(list.contains(p1));
        assertTrue(list.contains(it));
    }

    /**
     * Testet das Sortieren der Transaktionen.
     */
    @Test
    void testGetTransactionsSorted() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", it);
        bank.addTransaction("A", ot);
        bank.addTransaction("A", p1);

        List<Transaction> asc = bank.getTransactionsSorted("A", true);

        assertEquals(-300, asc.get(0).calculate());
        assertEquals(200, asc.get(2).calculate());
    }

    /**
     * Testet das Filtern nach positiven/negativen Transaktionen.
     */
    @Test
    void testGetTransactionsByType() throws Exception {
        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", it);
        bank.addTransaction("A", ot);

        List<Transaction> positive = bank.getTransactionsByType("A", true);
        List<Transaction> negative = bank.getTransactionsByType("A", false);

        assertEquals(2, positive.size());
        assertEquals(1, negative.size());
    }

    /**
     * Testet die equals()-Methode.
     */
    @Test
    void testEquals() throws Exception {
        PrivateBank b1 = new PrivateBank("BankX", 0.1, 0.2, testDir);
        PrivateBank b2 = new PrivateBank("BankX", 0.1, 0.2, testDir);

        assertEquals(b1, b2);
    }

    /**
     * Testet die toString()-Methode.
     */
    @Test
    void testToString() {
        String s = bank.toString();
        assertTrue(s.contains("TestBank"));
        assertTrue(s.contains("incomingInterest"));
        assertTrue(s.contains("outgoingInterest"));
    }

    /**
     * Testet die Persistenzfunktion: Laden gespeicherter Accounts.
     */
    @Test
    void testPersistenceLoadAccounts() throws Exception {

        bank.createAccount("A");
        bank.addTransaction("A", p1);
        bank.addTransaction("A", it);

        PrivateBank bankReloaded = new PrivateBank("TestBank", 0.1, 0.2, testDir);

        assertTrue(bankReloaded.containsTransaction("A", p1));
        assertTrue(bankReloaded.containsTransaction("A", it));
        assertEquals(bank.getTransactions("A"), bankReloaded.getTransactions("A"));
    }
}
