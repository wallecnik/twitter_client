import java.io.*;
import java.util.Properties;

/**
 * Grouping of methods over a configuration file. Config has to be loaded to get
 * some configuration data and stored to be permanently saved for future use.
 *
 * @author Wallecnik
 * @version 29.3.2015
 */
public class Config {

    private static final String PROP_USERNAME = "username";
    private static final String PROP_PASSWORD = "password";

    private static Properties prop = new Properties();
    private static final String configFile = "config.properties";

    private Config() {}

    /**
     * Factory method for Config objects. Loads a configuration file. If the file does not exist
     * this only returns a new instance
     *
     * @return an instance of Config
     * @throws IOException if an I/O error occurred
     */
    public static Config load() throws IOException {
        try (InputStream is = new FileInputStream(configFile)) {
            prop.load(is);
        } catch (FileNotFoundException fnfe) {
            System.err.println("Config file not present. " +
                    "Method store() will attempt to create `" + configFile + "`");
        }
        return new Config();
    }

    /**
     * Stores the configuration data. If there is no configuration file, this method
     * attempts to create one.
     *
     * @throws IOException if the data cannot be written or the file cannot be created
     */
    public void store() throws IOException {
        Writer output = new FileWriter(configFile);
        prop.store(output, null);
    }

    /**
     * Getters and setters of configuration data
     */

    public String getUsername() {
        return prop.getProperty(PROP_USERNAME);
    }
    public void   setUsername(String username) {
        prop.setProperty(PROP_USERNAME, username);
    }

    public String getPassword() {
        return prop.getProperty(PROP_PASSWORD);
    }
    public void   setPassword(String password) {
        prop.setProperty(PROP_PASSWORD, password);
    }

}
