package intermediateModelHelper.envirorment.temporal;

import java.io.*;

/**
 * Created by giovanni on 07/03/2017.
 */
public abstract class ParseCSV {

    File file;
    String separator = ";";
    InputStream stream;
    boolean splitSignature = false;

    boolean isStream = false;

    public ParseCSV(String path){
        this(new File(path));
    }

    public ParseCSV(File file) {
        this.file = file;
    }

    public ParseCSV(InputStream stream){
        this.isStream = true;
        this.stream = stream;
    }

    public boolean isSplitSignature() {
        return splitSignature;
    }

    public void setSplitSignature(boolean splitSignature) {
        this.splitSignature = splitSignature;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void start(){
        try {
            if (isStream) {
                start(new InputStreamReader(stream));
            } else {
                start(new FileReader(this.file));
            }
        } catch (Exception e){
            //System.err.println("Cannot open file or stream: " + this.file + " -- " + this.stream);
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
                    if(row.length == 3 || splitSignature){
                        String[] signature = row[2].split(",");
                        if(signature.length == 1 && signature[0].equals("")){
                            signature = new String[0];
                        }
                        handleRow(row[0],row[1], signature);
                    } else {
                        if(splitSignature)
                            handleRow(row[0],row[1], new String[0]);
                        else
                            handleRow(row[0],row[1], new String[]{ row[2] }  );
                    }

                }
                else {
                    notHeader = true;
                    handleHeader(row);
                }
            }
        } catch (IOException e) {
           // e.printStackTrace();
        }
    }


    protected abstract void handleHeader(String[] header);
    protected abstract void handleRow(String className, String methodName, String[] signature);



}
