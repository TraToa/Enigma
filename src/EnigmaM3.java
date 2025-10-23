import java.util.HashMap;
import java.util.Map;

import tableUtility.HashTable;
import tableUtility.Table;

/**
 * A specialized subclass inherited from the Enigma class of the Enigma cipher machine's model M3,
 * used exclusively by the Kriegsmarine of Nazi Germany along with the M1, M2 and the later-impoved M4.
 */

class EnigmaM3 extends Enigma {
    /**
     * The exact number of rotors used in the model.
     */
    private static final int rotorNumber = 3;

    /**
     * The specific table mapping all the eight rotors used in the model to their corresponding wiring.
     */
    private static final HashTable<String, String, String> rotorWiringTable = new HashTable<>(Table.ofEntries(
        Table.entry("I", "EKMFLGDQVZNTOWYHXUSPAIBRCJ", "R")
    ));

    /* Map.of(
        "I", "EKMFLGDQVZNTOWYHXUSPAIBRCJ",
        "II", "AJDKSIRUXBLHWTMCQGZNPYFVOE",
        "III", "BDFHJLCPRTXVZNYEIWGAKMUSQO",
        "IV", "ESOVPZJAYQUIRHXLNFTGKDCMWB",
        "V", "VZBRGITYUPSPNHLXAWMJQOFECK",
        "VI", "JPGVOUMFYQBENHZRDKASXLICTW",
        "VII", "NZJHGRCXMYSWBOUFAIVLPEKQDT",
        "VIII", "FKQHTLXOCBJSPDZRAMEWNIUYGV"
    ) */

    /**
     * The specific table mapping all the two reflectors, which are the UKW-B and UKW-C,
     * used in the model to their exact wiring.
     */
    private static final HashMap<String, String> reflectorWiringTable = new HashMap<>(Map.of(
        "UKW-B", "YRUHQSLDPXNGOKMIEBFZCWVJAT",
        "UKW-C", "FVPJIAOYEDRZXWGCTKUQSBNMHL"
    ));

    /**
     * The constructor calls the superconstructor, assigns the attributes
     * of the superclass with those of this model subclass.
     */
    EnigmaM3() {
        super(rotorNumber, rotorWiringTable, reflectorWiringTable);
    }
}
