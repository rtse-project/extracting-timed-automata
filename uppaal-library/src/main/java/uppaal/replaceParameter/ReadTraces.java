package uppaal.replaceParameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giovanni on 03/05/2017.
 */
public class ReadTraces {
    File file;
    String separator = ",";
    InputStream stream;

    boolean isStream = false;

    List<TraceItem> traces = new ArrayList<>();

    public ReadTraces(String path){
        this(new File(path));
    }

    public ReadTraces(File file) {
        this.file = file;
    }

    public ReadTraces(InputStream stream){
        this.isStream = true;
        this.stream = stream;
    }

    public static List<TraceItem> getTraces(String file){
        return new ReadTraces(file).parse();
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    protected List<TraceItem> parse(){
        try {
            if (isStream) {
                start(new InputStreamReader(stream));
            } else {
                start(new FileReader(this.file));
            }
        } catch (Exception e){
            //System.err.println("Cannot open file or stream: " + this.file + " -- " + this.stream);
        }
        return traces;
    }

    public List<TraceItem> getTraces() {
        return traces;
    }

    protected void start(InputStreamReader file){
        try (BufferedReader br = new BufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] row = line.split(separator);
                handleRow(row);
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }


    void handleHeader(String[] header){}
    void handleRow(String[] row){
        if(row.length == 6){
            int thID            = Integer.parseInt(row[0]);
            String className    = row[1];
            String methodName   = row[2];
            int line            = Integer.parseInt(row[3]);
            String varName      = row[4];
            String varValue     = row[5];
            traces.add(new TraceItem(thID,className,methodName,line,varName,varValue));
        }

    }
}
