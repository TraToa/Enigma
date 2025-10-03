import java.io.File;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Enigma enigma = Enigma.returnModel(Enigma.Model.M3);
        File key = new File("schlussel.txt");
        try (Scanner scn = new Scanner(System.in)) {
            enigma.setRingSettings(scn);
            enigma.setRotors(scn);
            enigma.setReflector(scn);
            enigma.setPlugboard(key);
            System.out.println(enigma.getRingSettings());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}