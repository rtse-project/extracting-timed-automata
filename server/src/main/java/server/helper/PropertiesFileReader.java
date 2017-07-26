package server.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by giovanni on 12/05/2017.
 */
public class PropertiesFileReader {

    private static final Properties properties;

    /** Use a static initializer to read from file. */
    static {
        InputStream inputStream = PropertiesFileReader.class.getResourceAsStream("/buildNumber.properties");
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /** Hide default constructor. */
    private PropertiesFileReader() {}

    /**
     * Gets the Git SHA-1.
     * @return A {@code String} with the Git SHA-1.
     */
    public static String getGitSha1() {
        String v = properties.getProperty("git-sha-1");
        return ( (v != null && !v.equals("")) ? v : "developer-release");
    }
}
