import java.util.HashMap;
import java.util.Map;

class EnigmaM3 extends Enigma {
    private static final HashMap<String, String> rotorWiringTable = new HashMap<>(Map.of(
        "I", "EKMFLGDQVZNTOWYHXUSPAIBRCJ",
        "II", "AJDKSIRUXBLHWTMCQGZNPYFVOE",
        "III", "BDFHJLCPRTXVZNYEIWGAKMUSQO",
        "IV", "ESOVPZJAYQUIRHXLNFTGKDCMWB",
        "V", "VZBRGITYUPSPNHLXAWMJQOFECK",
        "VI", "JPGVOUMFYQBENHZRDKASXLICTW",
        "VII", "NZJHGRCXMYSWBOUFAIVLPEKQDT",
        "VIII", "FKQHTLXOCBJSPDZRAMEWNIUYGV"
    ));

    private static final HashMap<String, String> reflectorWiringTable = new HashMap<>(Map.of(
        "UKW-B", "YRUHQSLDPXNGOKMIEBFZCWVJAT",
        "UKW-C", "FVPJIAOYEDRZXWGCTKUQSBNMHL"
    ));

    EnigmaM3() {
        super(3, rotorWiringTable, reflectorWiringTable);
    }
}
