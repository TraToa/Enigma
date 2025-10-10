import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

/**
 * A customized subclass of {@code InputStream} used in a {@code Scanner} object to read
 * the key to the cipher machine either from an external file or from console by choice.
 */
class ExtendedInputStream extends InputStream {

    /**
     * A {@code File} object containing the key with the exact order.
     */
    private File file;

    /**
     * A {@code String} line specialized in a certain part of the key,
     * including rotors, ring settings, plugboard pairings and reflector;
     * defined only when the gettter methods are called to retrieve values of the key.
     */
    private String string;

    /**
     * A {@code boolean} variable indicating if there is a file designated to the object
     * and if the user chooses to use the machine with manual console input or reading from file.
     */
    private boolean hasFile = false;

    /**
     * A variable dedicated for the position used for file reading for the {@code read()} method.
     */
    private int position;

    /**
     * Creates an {@code ExtendedInputStream} using reading from file option.
     * 
     * @param   file
     *          The {@code File} object containing the key.
     * @throws  NoSuchFileException
     *          Throws if the file does not exist and can not be found.
     */
    ExtendedInputStream(File file) throws NoSuchFileException {
        this.file = file;
        this.hasFile = true;
    }

    /**
     * Creates an {@code ExtendedInputStream} using reading from console option.
     */
    ExtendedInputStream() {
        this.hasFile = false;
    }

    /**
     * A getter method for the {@code File} object.
     * 
     * @return  {@code this.file}.
     */
    File getFile() {
        return this.file;
    }

    /**
     * A method indicating if object uses a file.
     * 
     * @return  {@code this.hasFile}, true if the object uses a file
     * and file reading option, false if otherwise.
     */
    boolean hasFile() {
        return this.hasFile;
    }

    /**
     * A setter method assigning the string needed to be read with file reading option,
     * as well as set the {@code this.position} back to 0 as the initial reading position.
     * 
     * @param   string
     *          The string to be read.
     */
    void setString(String string) {
        this.position = 0;
        this.string = string;
    }

    /**
     * Overrides the abstract method {@code read()} from the superclass
     * {@code InputStream}, capable of reading both manual input from console
     * and the key in a file, depending on the user's choice.
     * 
     * @return  The corresponding character from the key input.
     */
    @Override
    public int read() throws IOException {
        if (this.hasFile) {
            if (position >= string.length()) {
                return -1;
            }
            return string.charAt(position++);
        } else {
            int returnResult = System.in.read();
            if (returnResult == 10) {
                return -1;
            }
            return returnResult;
        }
    }
}
