package ui;

import bank.PrivateBank;
import bank.exceptions.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainviewController {

    @FXML
    private ListView<String> accountListView;

    @FXML
    private Button createAccountButton;

    private PrivateBank bank;

    //                  Initialisierung

    @FXML
    public void initialize() {
        try {
            // Bank mit Persistence-Verzeichnis "data"
            bank = new PrivateBank("MeineBank", 0.05, 0.1, "data");

            updateList();
            setupContextMenu();

        } catch (Exception e) {
            showError("Fehler beim Laden der Bank:\n" + e.getMessage());
        }
    }

    private void updateList() {
        accountListView.getItems().clear();
        List<String> accounts = bank.getAllAccounts();
        accountListView.getItems().addAll(accounts);
    }

    private void setupContextMenu() {
        MenuItem selectItem = new MenuItem("Auswählen");
        MenuItem deleteItem = new MenuItem("Löschen");

        selectItem.setOnAction(e -> openAccount());
        deleteItem.setOnAction(e -> deleteAccount());

        ContextMenu menu = new ContextMenu(selectItem, deleteItem);
        accountListView.setContextMenu(menu);
    }

    //                  Account anlegen

    @FXML
    private void createAccount() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Neuen Account anlegen");
        dialog.setHeaderText(null);
        dialog.setContentText("Account-Name:");

        dialog.showAndWait().ifPresent(name-> {

            if (name == null || name.isBlank()) {
                showError("Name darf nicht leer sein.");
                return;
            }

            try {
                bank.createAccount(name);
                updateList();
                showInfo("Account '" + name + "' wurde angelegt.");
            } catch (AccountAlreadyExistsException e) {
                showError("Account existiert bereits:\n" + e.getMessage());
            } catch (IOException e) {
                showError("Fehler beim Speichern des Accounts:\n" + e.getMessage());
            } catch (Exception e) {
                showError("Unerwarteter Fehler:\n" + e.getMessage());
            }});

    }

    //                  Account öffnen

    private void openAccount() {
        String account = accountListView.getSelectionModel().getSelectedItem();
        if (account == null) {
            showError("Bitte zuerst einen Account auswählen.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accountview.fxml"));
            Parent root = loader.load();

            AccountviewController ctrl = loader.getController();
            ctrl.setData(bank, account);

            Stage stage = (Stage) accountListView.getScene().getWindow();
            stage.setTitle("Account: " + account);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showError("Fehler beim Laden der Accountview:\n" + e.getMessage());
        } catch (Exception e) {
            showError("Unerwarteter Fehler:\n" + e.getMessage());
        }
    }

    //                  Account löschen (mit Bestätigung)

    @FXML
    private void deleteAccount() {
        String account = accountListView.getSelectionModel().getSelectedItem();
        if (account == null) {
            showError("Bitte zuerst einen Account auswählen.");
            return;
        }

        // Bestätigungsdialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Account löschen");
        confirm.setHeaderText("Willst du den Account wirklich löschen?");
        confirm.setContentText("Account: " + account);
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
            // Benutzer hat abgebrochen
            return;
        }

        // Benutzer hat bestätigt → löschen
        try {
            bank.deleteAccount(account);
            updateList();
            showInfo("Account '" + account + "' wurde gelöscht.");
        } catch (AccountDoesNotExistException e) {
            showError("Account existiert nicht:\n" + e.getMessage());
        } catch (IOException e) {
            showError("Fehler beim Löschen der Account-Datei:\n" + e.getMessage());
        } catch (Exception e) {
            showError("Unerwarteter Fehler:\n" + e.getMessage());
        }
    }

    //                  Hilfsfunktionen

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}
