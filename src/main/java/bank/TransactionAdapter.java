package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Diese Klasse dient als benutzerdefinierter Serializer und Deserializer für
 * {@link Transaction}-Objekte. Da Gson ohne zusätzliche Informationen nicht
 * erkennen kann, welche konkrete Unterklasse (Payment, Transfer, IncomingTransfer,
 * OutgoingTransfer) serialisiert oder deserialisiert werden soll, wird hier ein
 * Wrapper-Format eingeführt.
 *
 * <p>Das JSON-Format sieht wie folgt aus:</p>
 *
 * <pre>
 * {
 *   "CLASSNAME": "Payment",
 *   "INSTANCE":  { ... tatsächliche Objektdaten ... }
 * }
 * </pre>
 *
 * <p>CLASSNAME speichert den Klassennamen der konkreten Transaktion,
 * INSTANCE enthält die eigentlichen Attributwerte des Objekts.</p>
 */
public class TransactionAdapter implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    /**
     * Serialisiert ein {@link Transaction}-Objekt in ein JSON-Objekt mit den Feldern
     * "CLASSNAME" und "INSTANCE".
     *
     * @param src         Die zu serialisierende Transaktion.
     * @param typeOfSrc   Der Typ des Objekts (von Gson bereitgestellt).
     * @param context     Der Gson-Kontext, der für die Serialisierung verwendet wird.
     * @return Ein JSON-Objekt, das sowohl den Klassennamen als auch die Instanzdaten enthält.
     */
    @Override
    public JsonElement serialize(Transaction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();

        // Speichert den einfachen Klassennamen (z. B. "Payment").
        root.addProperty("CLASSNAME", src.getClass().getSimpleName());

        // Speichert die eigentlichen Objektattribute.
        JsonElement instance = context.serialize(src);
        root.add("INSTANCE", instance);

        return root;
    }

    /**
     * Deserialisiert ein JSON-Objekt, das im speziellen Format dieses Adapters gespeichert wurde.
     * Es wird anhand des Feldes CLASSNAME entschieden, welche konkrete Transaktionsklasse
     * instanziiert werden soll.
     *
     * @param json     Das JSON-Element, das deserialisiert werden soll.
     * @param typeOfT  Der Zieltyp (von Gson bereitgestellt).
     * @param context  Der Gson-Kontext, der zur Deserialisierung genutzt wird.
     * @return Eine konkrete {@link Transaction}-Unterklasse, abhängig vom gespeicherten Klassennamen.
     * @throws JsonParseException Falls CLASSNAME fehlt, INSTANCE fehlt oder der Klassenname unbekannt ist.
     */
    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject root = json.getAsJsonObject();

        // Sicherheitsprüfungen
        if (!root.has("CLASSNAME") || !root.has("INSTANCE")) {
            throw new JsonParseException("Ungültiges JSON-Format: CLASSNAME oder INSTANCE fehlt");
        }

        String className = root.get("CLASSNAME").getAsString();
        JsonElement instance = root.get("INSTANCE");

        // Auswahl der korrekten Zielklasse
        switch (className) {
            case "Payment":
                return context.deserialize(instance, Payment.class);

            case "IncomingTransfer":
                return context.deserialize(instance, IncomingTransfer.class);

            case "OutgoingTransfer":
                return context.deserialize(instance, OutgoingTransfer.class);

            case "Transfer": // Basis-Klasse
                return context.deserialize(instance, Transfer.class);

            default:
                throw new JsonParseException("Unbekannter CLASSNAME: " + className);
        }
    }
}
