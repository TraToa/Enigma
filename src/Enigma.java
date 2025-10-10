import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * An abstract representation of the logic inside the infamous cipher machine.
 */
class Enigma {

    /**
     * The number of rotors in a single Enigma machine, varying depending on the model.
     */
    private int rotorNumber;

    /**
     * [de: <em>Walzen</em>]
     * 
     * <p> A list comprising the name of the rotors chosen to encrypt messages.
     * 
     * <p> Different rotors have their wiring configured differently, and are named, or marked differently.
     */
    private ArrayList<String> rotors = new ArrayList<>();

    /**
     * [de: <em>Ringstellung</em>]
     * 
     * <p> A list comprising the settings for rotors, typically numbers, which adjust alphabet tyres
     * relative to physical rotor discs.
     */
    private ArrayList<Integer> ringSettings = new ArrayList<>();

    /**
     * <p> An additional list made up for storing wirings details used to encrypt each character from messages.
     * 
     * <p> This includes:
     * <ol>
     * <li> Wirings of the chosen rotors, respectively from left to right, starting at the index 1.
     * <li> Wiring of the chosen reflector [de: <em>Umkehrwalze</em>].
     * </ol>
     */
    private LinkedList<HashMap<Character, Character>> encryptionTables = new LinkedList<>();

    /**
     * [de: <em>Steckerbrett</em>]
     * 
     * <p> A table permitting variable wiring that can be reconfigured,
     * contributing more cryptographic strength than an extra rotor.
     * 
     * <p> This wiring mimics the ability, physically, of a cable placed
     * onto the plugboard to connect letters in pairs, swapping those letters
     * before and after the main rotors' work.
     */
    private HashMap<Character, Character> plugboard = new HashMap<>();

    /**
     * A table mapping rotors to their correct wirings according to their designations and the models
     * as their own names, wirings and the total number of rotors may differ depending on which model is being used.
     */
    private HashMap<String, String> rotorWiringTable;

    /**
     * A table mapping reflectors to the correct wirings according the their and the machine's models designations
     * as they can all differ depeding on which one is being used.
     */
    private HashMap<String, String> reflectorWiringTable;

    /**
     * Enumeration type that indicates the designated model of the machine.
     */
    enum Model {
        M3,
        M4
    }

    /**
     * Static method to create an instance of the Enigma machine according to the given model.
     * 
     * @param   model 
     *          The Model enumeration type matching the model's designation.
     * @return  The corresponding machine object.
     */
    static Enigma returnModel(Model model) {
        switch (model) {
            case Model.M3:
                return new EnigmaM3();

            default:
                return null;
        }
    }

    /**
     * This constructor initializes the machine object, assigning {@code rotorNumber},
     * {@code rotorWiringTable} and {@code reflectorWiringTable} according to the
     * subclass which calls this as a superconstructor.
     * 
     * @param   rotorNumber
     *          The number of rotors used in the machine.
     * @param   rotorWiringTable
     *          The list mapping a rotor to its corresponding wiring.
     * @param   reflectorWiringTable
     *          The list mapping a reflector to its corresponding wiring.
     */
    Enigma(int rotorNumber, HashMap<String, String> rotorWiringTable, HashMap<String, String>reflectorWiringTable) {
        this.setRotorNumber(rotorNumber);
        this.setRotorWiringTable(rotorWiringTable);
        this.setReflectorWiringTable(reflectorWiringTable);
    }

    /**
     * A method setting the number of rotors in the machine object.
     * 
     * @param   rotorNumber
     */
    private void setRotorNumber(int rotorNumber) {
        this.rotorNumber = rotorNumber;
    }

    /**
     * A method setting the table containing designations of the rotors
     * and their respective wiring details.
     * 
     * @param   rotorWiringTable
     */
    private void setRotorWiringTable(HashMap<String, String> rotorWiringTable) {
        this.rotorWiringTable = rotorWiringTable;
    }

    /**
     * A method setting the table containing designations of the reflectors
     * and their respective wiring details
     * 
     * @param   reflectorWiringTable
     */
    private void setReflectorWiringTable(HashMap<String, String> reflectorWiringTable) {
        this.reflectorWiringTable = reflectorWiringTable;
    }

    /**
     * A method mapping the given rotor designation to its corresponding
     * wiring according to {@code this.rotorWiringTable}.
     * 
     * @param   rotor
     *          The given rotor designation.
     * @return  The wiring string if {@code rotor} is present,
     *          a null string if otherwise.
     */
    private String getRotorWiring(String rotor) {
        if (this.rotorWiringTable.containsKey(rotor)) {
            return this.rotorWiringTable.get(rotor);
        } else {
            return null;
        }
    }

    /**
     * A method mapping the given reflector designation to its corresponding
     * wiring according to {@code this.rotorWiringTable}.
     * 
     * @param   reflector
     *          The given reflector designation.
     * @return  The wiring string if {@code reflector} is present,
     *          a null string if otherwise.
     */
    private String getReflectorWiring(String reflector) {
        if (this.reflectorWiringTable.containsKey(reflector)) {
            return this.reflectorWiringTable.get(reflector);
        } else {
            return null;
        }
    }

    /**
     * A method transforming a wiring string into a table mapping characters accordingly.
     * 
     * @param   wiring
     *          The wiring string deduced from a designation of a rotor.
     * @return  The table representing the encryption between charaters in the physical rotor.
     */
    private HashMap<Character, Character> encryptionTable(String wiring) {
        HashMap<Character, Character> table = new HashMap<>();
        for (int i = 0; i < wiring.length(); i++) {
            char input = wiring.charAt(i);
            char output = wiring.charAt(input - 65);
            table.put(input, output);
        }
        return table;
    }

    /**
     * A method getting the number of the rotors used in the machine.
     * 
     * @return  The number of the rotors.
     */
    int getRotorNumber() {
        return this.rotorNumber;
    }

    /**
     * A method extracting a line from the key {@code file} used to configure the machine.
     * 
     * @param   file
     *          A {@code File} object used as the key for the cipher machine.
     *
     * @param   line
     *          The number of the line which is to be read.
     * 
     * @throws  IOException
     */
    private String getKeyLine(File file, int line) throws IOException {
        String lineSequence = Files.lines(file.toPath())
                                        .skip(line - 1)
                                        .findFirst()
                                        .orElse("");
        return lineSequence;
    }

    /**
     * A method reading the rotors from a file at the exact line and
     * call {@code this.setRotors()} method to handle the rest.
     * 
     * @param   file
     *          A {@code File} object containing the key to be read from.
     * 
     * @throws IOException
     */
    void setRotors(ExtendedInputStream input) throws IOException {
        if (input.hasFile()) {
            input.setString(this.getKeyLine(input.getFile(), 1));
        }
        Scanner rotorScn = new Scanner(input);
        for (int i = 0; i < this.rotorNumber; i++) {
            String rotorInput = rotorScn.next().toUpperCase();
            this.rotors.add(rotorInput);
            this.encryptionTables.addLast(this.encryptionTable(this.getRotorWiring(rotorInput)));
        }
        rotorScn.close();
    }

    /**
     * A method reading the ring settings from a file at the exact line and
     * call {@code this.setRingSettings()} to do the rest.
     * 
     * @param   file
     *          A {@code File} object containing the key to be read from.
     * 
     * @throws IOException
     */
    void setRingSettings(ExtendedInputStream input) throws IOException {
        if (input.hasFile()) {
            input.setString(this.getKeyLine(input.getFile(), 2));
        }
        Scanner ringSettingsScn = new Scanner(input);
        for (int i = 0; i < this.rotorNumber; i++) {
            int ringInput = ringSettingsScn.nextInt();
            this.ringSettings.add(ringInput);
        }
        ringSettingsScn.close();
    }

    /**
     * <p> A method manually assigning pairs on the plugboard and
     * add them to {@code this.plugBoard}.
     * 
     * <p> This will alphabetically print a not yet added character
     * and take input of another one to make a pair.
     * 
     * @param   scn
     *          A {@code Scanner} object responsible for character inputting.
     */
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

    /**
     * <p> A method taking preprinted pairs of character from a file
     * and add them to {@code this.plugBoard}.
     * 
     * <p> Characters which are not included in that file will be assigned to be paired with itself.
     * 
     * @param   file
     *          A {@code File} object containing message to be read.
     */
    void setPlugboard(ExtendedInputStream input) throws IOException {
        if (input.hasFile()) {
            input.setString(this.getKeyLine(input.getFile(), 3));
            Scanner plugboardScn = new Scanner(input);
            while (plugboardScn.hasNext()) {
                String pair = plugboardScn.next().toUpperCase();
                this.plugboard.put(pair.charAt(0), pair.charAt(1));
                this.plugboard.put(pair.charAt(1), pair.charAt(0));
            }
            for (char character = 65; character <= 90; character++) {
                if (!this.plugboard.containsKey(character)) {
                    this.plugboard.put(character, character);
                }
            }
            plugboardScn.close();
        } else {
            Scanner scn = new Scanner(System.in);
            for (char pair1 = 65; pair1 <= 90; pair1++) {
                if (!this.plugboard.containsKey(pair1)) {
                    System.out.print(pair1);
                    char pair2 = scn.next().toUpperCase().charAt(0);
                    this.plugboard.put(pair1, pair2);
                    if (pair1 != pair2) {
                        this.plugboard.put(pair2, pair1);
                    }
                }
            }
            scn.close();
        }
    }

    /**
     * A method taking preprinted for a key {@code file} and call {@code this.setReflector()}.
     * 
     * @param   file
     *          A {@code File} object containing the key to be read from.
     * 
     * @throws IOException
     */
    void setReflector(ExtendedInputStream input) throws IOException {
        if (input.hasFile()) {
            input.setString(this.getKeyLine(input.getFile(), 4));
        }
        Scanner reflectorScn = new Scanner(input);
        String reflectorInput = reflectorScn.next().toUpperCase();
        String wiring = this.getReflectorWiring(reflectorInput);
        this.encryptionTables.addFirst(this.encryptionTable(wiring));
        reflectorScn.close();
    }

    String getRotor(int i) {
        return this.rotors.get(i);
    }
    
    ArrayList<String> getRotors() {
        return this.rotors;
    }

    ArrayList<Integer> getRingSettings() {
        return this.ringSettings;
    }

    HashMap<Character, Character> getPlugboard() {
        return this.plugboard;
    }
}