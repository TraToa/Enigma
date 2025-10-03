import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class Enigma {
    enum Model {
        M3,
        M4
    }

    private int rotorNumber;
    private ArrayList<String> rotors = new ArrayList<>();
    private LinkedList<HashMap<Character, Character>> encryptionTables = new LinkedList<>();
    private HashMap<Character, Character> plugboard = new HashMap<>();
    private HashMap<String, String> rotorWiringTable;
    private HashMap<String, String> reflectorWiringTable;

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

    void setRotors(Scanner scn) {
        for (int i = 0; i < this.rotorNumber; i++) {
            String rotorInput = scn.next().toUpperCase();
            this.rotors.add(rotorInput);
            this.encryptionTables.addLast(this.encryptionTable(this.getRotorWiring(rotorInput)));
        }
    }

    void setReflector(Scanner scn) {
        String reflectorInput = scn.next().toUpperCase();
        String wiring = this.getReflectorWiring(reflectorInput);
        this.encryptionTables.addFirst(this.encryptionTable(wiring));
    }

    void setPlugboard(Scanner scn) {
        for (char input = 65; input <= 90; input++) {
            if (!this.plugboard.containsKey(input)) {
                System.out.print(input);
                char output = scn.next().toUpperCase().charAt(0);
                this.plugboard.put(input, output);
                if (input != output) {
                    this.plugboard.put(output, input);
                }
            }
        }
    }

    void setPlugboard(File file) {
        try (Scanner scn = new Scanner(file)) {
            while (scn.hasNext()) {
                String pair = scn.next().toUpperCase();
                this.plugboard.put(pair.charAt(0), pair.charAt(1));
            }
            for (char character = 65; character <= 90; character++) {
                this.plugboard.put(character, character);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap<Character, Character> getPlugboard() {
        return this.plugboard;
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