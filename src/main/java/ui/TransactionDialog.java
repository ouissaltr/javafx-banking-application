package ui;

import bank.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class TransactionDialog {

    public static Optional<Transaction> show(String accountName) {

        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Neue Transaktion");
        dialog.setHeaderText("Transaktion hinzufügen");

        ButtonType okButton = new ButtonType("Hinzufügen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        //       EINFACHES LAYOUT
        VBox box = new VBox(10);

        ChoiceBox<String> typeBox = new ChoiceBox<>();
        typeBox.getItems().addAll("Payment", "Transfer");
        typeBox.setValue("Payment");

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Betrag (z.B. 50.0)");

        TextField txtDescription = new TextField();
        txtDescription.setPromptText("Beschreibung");

        TextField txtSender = new TextField();
        txtSender.setPromptText("Sender");

        TextField txtRecipient = new TextField();
        txtRecipient.setPromptText("Empfänger");

        // START: am Anfang Sender/Empfänger deaktivieren (Payment)
        txtSender.setDisable(true);
        txtRecipient.setDisable(true);

        // Wenn "Transfer" gewählt wird → Felder aktivieren
        typeBox.setOnAction(e -> {
            boolean isTransfer = typeBox.getValue().equals("Transfer");
            txtSender.setDisable(!isTransfer);
            txtRecipient.setDisable(!isTransfer);
        });

        box.getChildren().addAll(
                new Label("Typ:"), typeBox,
                new Label("Betrag:"), txtAmount,
                new Label("Beschreibung:"), txtDescription,
                new Label("Sender:"), txtSender,
                new Label("Empfänger:"), txtRecipient
        );

        dialog.getDialogPane().setContent(box);


        //LOGIK

        dialog.setResultConverter(button -> {

            if (button != okButton)
                return null; // Abbrechen

            try {
                double amount = Double.parseDouble(txtAmount.getText());
                String desc = txtDescription.getText();

                // Dummy-Datum (OK laut Praktikum)
                String date = "2024-01-01";

                //             PAYMENT
                if (typeBox.getValue().equals("Payment")) {

                    return new Payment(date, amount, desc);
                }

                //             TRANSFER
                String sender = txtSender.getText();
                String recipient = txtRecipient.getText();

                // Automatische Erkennung:
                // - Wenn der aktuelle Account der Sender ist → OUTGOING
                // - Sonst: INCOMING
                if (sender.equalsIgnoreCase(accountName)) {
                    return new OutgoingTransfer(date, amount, desc, sender, recipient);
                } else {
                    return new IncomingTransfer(date, amount, desc, sender, recipient);
                }

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,
                        "Bitte gültige Werte eingeben!").showAndWait();
                return null;
            }
        });

        return dialog.showAndWait();
    }
}
