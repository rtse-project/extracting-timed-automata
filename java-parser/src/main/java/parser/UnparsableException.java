package parser;

/**
 * Created by giovanni on 14/03/2017.
 */
public class UnparsableException extends Exception {

    public UnparsableException(String message) {
        super("File cannot be parsed. " + message);
    }
}
