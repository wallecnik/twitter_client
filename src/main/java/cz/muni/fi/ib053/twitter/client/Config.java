package cz.muni.fi.ib053.twitter.client;

import java.io.IOException;

/**
 * Grouping of methods over a configuration file. Config has to be loaded to get
 * some configuration data and stored to be permanently saved for future use.
 *
 * @author Wallecnik
 * @version 29.3.2015
 */
public interface Config {

    /**
     * Loads a configuration file. If the file does not exist the implementation should
     * log a warning and
     *
     * @throws java.io.IOException if an I/O error occurred
     */
    void load() throws IOException;

    /**
     * Stores the configuration data. If there is no configuration file, this method
     * attempts to create one.
     *
     * @throws java.io.IOException if the data cannot be written or the file cannot be created
     */
    void store() throws IOException;

    /**
     * Getters and setters of configuration data. Each of them can throw an instance
     * of IllegalStateException if the configuration file was not loaded.
     */

    String getUsername();
    void   setUsername(String username);

    String getPassword();
    void   setPassword(String password);

}
