package instrumentation;

import instrumentation.data.Store;
import instrumentation.data.StoreItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;


/**
 * Created by giovanni on 07/03/2017.
 */
public class ReadConfFile {

    File file;
    String separator = ";";
    InputStream stream;
    Store store = Store.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    boolean isStream = false;

    public ReadConfFile(String path){
        this(new File(path));
    }

    public ReadConfFile(File file) {
        this.file = file;
    }

    public ReadConfFile(InputStream stream){
        this.isStream = true;
        this.stream = stream;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    protected void start(){
        try {
            if (isStream) {
                start(new InputStreamReader(stream));
            } else {
                start(new FileReader(this.file));
            }
        } catch (Exception e){
            //System.err.println("Cannot open file or stream: " + this.file + " -- " + this.stream);
            LOGGER.error("Cannot open conf file: {}",  this.file);
        }
    }

    protected void start(InputStreamReader file){
        boolean notHeader = false;
        try (BufferedReader br = new BufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] row = line.split(separator);
                if(notHeader) {
                    handleRow(row);
                }
                else {
                    notHeader = true;
                    handleHeader(row);
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            LOGGER.error("Cannot read the conf file: {}", this.file);
        }
    }

    private void handleRow(String[] row) {
        if(row.length != 4){
            return;
        }
        String className    = row[0];
        String methodName   = row[1];
        int line            = Integer.parseInt(row[2]);
        String varName      = row[3];
        String javaClass = Utility.convertPkgNametoJava(className);
        StoreItem item = new StoreItem(javaClass, className, methodName, line, varName);
        store.addElement(item);
    }

    private void handleHeader(String[] row) {
    }


}

