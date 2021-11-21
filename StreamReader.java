import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StreamReader {
    private final InputStream stream;

    public StreamReader(InputStream stream) {
        this.stream = stream;
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private int readOrThrowEOF() throws IOException, EOFException {
        int c = stream.read();
        if (c == -1) {
            throw new EOFException();
        }
        return c;
    }

    private int skipSpaces() throws IOException {
        int c;
        while (Character.isWhitespace(c = readOrThrowEOF())) {
            stream.mark(1);
        }
        return c;
    }

    public int readNextInt() throws IOException {
        int c = skipSpaces();

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF();
        }

        int result = 0;
        do {
            int digit = (c - '0');
            result = (result * 10) + digit;
        } while (isDigit(c = readOrThrowEOF()));

        return result * mult;
    }

    public double readNextDouble() throws IOException {
        int c = skipSpaces();

        int mult = 1;
        if (c == '-') {
            mult = -1;
            c = readOrThrowEOF();
        }

        long integer = 0;
        long decimal = 0;
        double decimalMult = 1;
        do {
            int digit = (c - '0');
            integer = (integer * 10) + digit;
        } while(isDigit(c = readOrThrowEOF()));
        if (c != '.') {
            return integer;
        }
        while (isDigit(c = readOrThrowEOF())) {
            int digit = (c - '0');
            decimal = (decimal * 10) + digit;
            decimalMult *= 0.1;
        }

        return ((double)integer + (decimal * decimalMult)) * mult;
    }

    public String readNextUntilEndOfLine() throws IOException {
        int c = skipSpaces();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(c);
        while ((c = readOrThrowEOF()) != '\n') {
            byteArrayOutputStream.write(c);
        }

        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }
}
