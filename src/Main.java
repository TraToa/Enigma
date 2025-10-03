import java.io.File;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Enigma enigma = Enigma.returnModel(Enigma.Model.M3);
        File key = new File("schlussel.txt");
        try (Scanner scn = new Scanner(key)) {
            enigma.setRotors(scn);
            // enigma.setReflector(scn);
            enigma.setPlugboard(key);
            System.out.println(enigma.getPlugboard());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}