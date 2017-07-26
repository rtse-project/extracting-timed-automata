package intermediateModel.interfaces;

/**
 * Created by giovanni on 14/07/2017.
 */
public interface IASTToken {
    int getLine();
    int getStart();
    int getEnd();
    String getCode();
}
