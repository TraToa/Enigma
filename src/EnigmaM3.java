import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    @Override
    void setRingSettings(Scanner scn) {
        for (int i = 0; i < super.getRotorNumber(); i++) {
            int ringInput = scn.nextLine().toUpperCase().charAt(0) - 64;
            super.getRingSettings().add(ringInput);
        }
    }
}
