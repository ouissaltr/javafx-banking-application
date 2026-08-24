package ui;

import bank.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AccountviewController {

    @FXML
    private Label accountLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private ListView<String> transactionListView;

    private PrivateBank bank;
    private String accountName;

    /**
     * Wird vom MainviewController gesetzt.
     */
    public void setData(PrivateBank bank, String accountName) {
        this.bank = bank;
        this.accountName = accountName;

        accountLabel.setText("Account: " + accountName);
        updateView();
    }

    /**
     * Vollständige Ansicht aktualisieren
     */
    private void updateView() {
        updateBalance();
        loadTransactions();
    }

    private void updateBalance() {
        double balance = bank.getAccountBalance(accountName);
        balanceLabel.setText(String.format("Kontostand: %.2f €", balance));
    }

    private void loadTransactions() {
        transactionListView.getItems().clear();

        List<Transaction> list = bank.getTransactions(accountName);

        for (Transaction t : list) {
            transactionListView.getItems().add(t.toString());
        }
    }

    //                 Buttons für Transaktionen

    @FXML
    private void newTransaction() {
        TransactionDialog.show(accountName).ifPresent(t -> {
            try {
                bank.addTransaction(accountName, t);
                updateView();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    private void deleteTransaction() {
        int index = transactionListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Bitte zuerst eine Transaktion auswählen!");
            return;
        }

        Transaction t = bank.getTransactions(accountName).get(index);

        //  Bestätigungsdialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Transaktion löschen");
        confirm.setHeaderText("Bist du dir sicher?");
        confirm.setContentText("Möchtest du diese Transaktion wirklich löschen?");

        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
            // User hat abgebrochen
            return;
        }

        //  User hat bestätigt → löschen
        try {
            bank.removeTransaction(accountName, t);
            updateView();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }


    //                 Sortieren + Filtern

    @FXML
    private void sortAsc() {
        updateList(bank.getTransactionsSorted(accountName, true));
    }

    @FXML
    private void sortDesc() {
        updateList(bank.getTransactionsSorted(accountName, false));
    }

    @FXML
    private void filterPositive() {
        updateList(bank.getTransactionsByType(accountName, true));
    }

    @FXML
    private void filterNegative() {
        updateList(bank.getTransactionsByType(accountName, false));
    }

    @FXML
    private void showAll() {
        loadTransactions();
    }

    private void updateList(List<Transaction> list) {
        transactionListView.getItems().clear();

        for (Transaction t : list) {
            transactionListView.getItems().add(t.toString());
        }

        updateBalance();
    }

    //                        Zurück Button

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Mainview.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) accountLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Fehler beim Zurückkehren:\n" + e.getMessage());
        }
    }

    //                    Hilfsmethoden

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}
