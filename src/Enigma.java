import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

class Enigma {
    enum Model {
        M3,
        M4
    }

    private int rotorNumber;
    private ArrayList<String> rotors = new ArrayList<>();
    private ArrayList<HashMap<Character, Character>> encryptionTables = new ArrayList<>();
    private HashMap<String, String> rotorWiringTable;
    private HashMap<String, String> reflectorWiringTable;
    private Scanner scn = new Scanner(System.in);

    static Enigma returnModel(Model model) {
        switch (model) {
            case Model.M3:
                return new EnigmaM3();

            default:
                return null;
        }
    }

    Enigma(int rotorNumber, HashMap<String, String> rotorWiringTable, HashMap<String, String>reflectorWiringTable) {
        this.setRotorNumber(rotorNumber);
        this.setRotorWiringTable(rotorWiringTable);
        this.setReflectorWiringTable(reflectorWiringTable);
    }

    private void setRotorNumber(int rotorNumber) {
        this.rotorNumber = rotorNumber;
    }

    int getRotorNumber() {
        return this.rotorNumber;
    }

    void setRotors() {
        for (int i = 0; i < this.rotorNumber; i++) {
            String rotorInput = scn.nextLine();
            this.rotors.add(rotorInput);
            this.encryptionTables.add(this.encryptionTable(this.getRotorWiring(rotorInput)));
        }
    }

    void setReflector() {
        String reflectorInput = scn.nextLine();
        String wiring = this.getReflectorWiring(reflectorInput);
        this.encryptionTables.add(this.encryptionTable(wiring));
    }

    private HashMap<Character, Character> encryptionTable(String rotorWiring) {
        HashMap<Character, Character> table = new HashMap<>();
        for (int i = 0; i < rotorWiring.length(); i++) {
            char input = rotorWiring.charAt(i);
            char output = rotorWiring.charAt(input - 65);
            table.put(input, output);
        }
        return table;
    }

    String getRotor(int i) {
        return this.rotors.get(i);
    }
    
    ArrayList<String> getRotors() {
        return this.rotors;
    }

    private void setRotorWiringTable(HashMap<String, String> rotorWiringTable) {
        this.rotorWiringTable = rotorWiringTable;
    }

    private void setReflectorWiringTable(HashMap<String, String> reflectorWiringTable) {
        this.reflectorWiringTable = reflectorWiringTable;
    }

    private String getRotorWiring(String rotor) {
        if (this.rotorWiringTable.containsKey(rotor)) {
            return rotorWiringTable.get(rotor);
        } else {
            return null;
        }
    }

    private String getReflectorWiring(String reflector) {
        if (this.reflectorWiringTable.containsKey(reflector)) {
            return reflectorWiringTable.get(reflector);
        } else {
            return null;
        }
    }
}