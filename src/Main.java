import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

class Main {
    public static void main(String[] args) {
        Enigma enigma = Enigma.returnModel(Enigma.Model.M3);
        File key = new File("key.txt");
        try {
            ExtendedInputStream input = new ExtendedInputStream(key);
            enigma.setRotors(input);
            enigma.setRingSettings(input);
            enigma.setPlugboard(input);
            enigma.setReflector(input);
        } catch (NoSuchFileException e) {
            System.out.println("File could not be found.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(enigma.getRotors());
            System.out.println(enigma.getRingSettings());
            System.out.println(enigma.getPlugboard());
        }
    }
}