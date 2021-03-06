package cz.muni.fi.ib053.twitter.client;

import java.io.*;
import java.util.Properties;

/**
 * Grouping of methods over a configuration file. Config has to be loaded to get
 * some configuration data and stored to be permanently saved for future use.
 *
 * @author Wallecnik
 * @version 29.3.2015
 */
public class ConfigImpl implements Config{

    private static final String PROP_USERNAME = "username";
    private static final String PROP_PASSWORD = "password";

    private static final String PROP_NOT_LOADED = "properties file not loaded";

    private static Properties prop = null;
    private static final String configFile = "src/main/resources/config.properties";

    /**
     * Loads a configuration file. If the file does not exist a warning is generated.
     * If the method throws an IOException, the state of config is undefined and should
     * be attempted to be loaded again.
     *
     * @return an instance of Config
     * @throws java.io.IOException if an I/O error occurred
     */
    public void load() throws IOException {
        prop = new Properties();
        try (InputStream is = new FileInputStream(configFile)) {
            prop.load(is);
        } catch (FileNotFoundException fnfe) {
            System.err.println("Config file not present. " +
                    "Method store() will attempt to create `" + configFile + "`");
        }
    }

    /**
     * Stores the configuration data. If there is no configuration file, this method
     * attempts to create one. If the method throws an IOException, the state of config
     * is undefined and should be attempted to be stored again.
     *
     * @throws java.io.IOException if the data cannot be written or the file cannot be created
     */
    public void store() throws IOException {
        Writer output = new FileWriter(configFile);
        prop.store(output, null);
    }

    /**
     * Getters and setters of configuration data. Each of them can throw an instance
     * of IllegalStateException if the configuration file was not loaded.
     */

    public String getUsername() {
        if (prop == null) throw new IllegalStateException(PROP_NOT_LOADED);
        return prop.getProperty(PROP_USERNAME);
    }
    public void   setUsername(String username) {
        if (prop == null) throw new IllegalStateException(PROP_NOT_LOADED);
        prop.setProperty(PROP_USERNAME, username);
    }

    public String getPassword() {
        if (prop == null) throw new IllegalStateException(PROP_NOT_LOADED);
        return prop.getProperty(PROP_PASSWORD);
    }
    public void   setPassword(String password) {
        if (prop == null) throw new IllegalStateException(PROP_NOT_LOADED);
        prop.setProperty(PROP_PASSWORD, password);
    }

}
