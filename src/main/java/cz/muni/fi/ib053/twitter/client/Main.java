package cz.muni.fi.ib053.twitter.client;


import cz.muni.fi.ib053.twitter.client.twitterapi.*;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.regex.Pattern;

public class Main {

    private static String platform;

    private static final Logger log;
    private static final Config config;
    private static final TwitterApi controller;
    private static final Scanner scanner;

    private static boolean doContinue = true;

    static {

        log = LoggerFactory.getLogger(Main.class);

        if (SystemUtils.IS_OS_WINDOWS) {
            platform = "win32";
        }
        if (SystemUtils.IS_OS_UNIX) {
            platform = "unix";
        }
        if (SystemUtils.IS_OS_MAC) {
            platform = "darwin";
        }
        try {
            addLibraryPath("build/binaries/debug/" + platform);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        System.loadLibrary("HttpNative"); // used for tests. This library in classpath only


        config = new ConfigImpl();
        try {
            config.load();
        } catch (IOException e) {
            log.error("Unable to load configuration file. \n" + e);
        }

        controller = new TwitterApiImpl(config);

        scanner = new Scanner(new InputStreamReader(System.in));
    }

    public static void main (String[] args) {

        log.info("Application started.");
        System.out.println("Application started, expecting commands");
        System.out.print("> ");

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();
            performAction(input);

            if (!doContinue) break;
            System.out.print("> ");
        }

        log.info("Application ending.");
        System.out.println("Exiting...");

        try {
            config.store();
        } catch (IOException e) {
            log.error("Unable to store configuration data. ");
        }
    }

    private static void performAction(String action) {
        log.trace("Received command: " + action);

        String[] args = action.split("\\s+");
        switch (args[0]) {
            case "send":
                if (args.length != 2) {
                    System.out.println("Wrong parameter");
                    break;
                }
                boolean sucess = controller.sendTweet(args[1]);
                if (!sucess) {
                    log.error("Unable to send tweet: " + args[1]);
                    System.out.println("Fail...");
                }
                else {
                    System.out.println("Success!");
                }
                break;
            case "get":
                if (args.length != 2) {
                    System.out.println("Wrong parameter");
                    break;
                }
                if (!Pattern.matches("\\d+", args[1])) {
                    System.out.println("Wrong parameter");
                }
                int count = Integer.valueOf(args[1]);
                SortedSet<Tweet> tweets = controller.showTweets(count);
                if (tweets != null) {
                    if (tweets.size() == 0) {
                        log.trace("Empty collection returned for request of" + count + " tweets");
                        System.out.println("No tweets...");
                    } else {
                        for (Tweet tweet : tweets) {
                            System.out.println(tweet);
                        }
                    }
                }
                break;
            case "hello":
                try {
                    System.out.println(HttpNative.httpRequest("", "", "/file.txt", "http://tomas.valka.info", "", "", ""));
                } catch (BadResponseCodeException e) {
                    e.printStackTrace();
                }
                break;
            case "exit":
                doContinue = false;
                break;
            default:
                System.out.println("\"" + args[0] + "\" was not recognized as internal command.");
                break;
        }
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     * @author Fahd Shariff
     */
    public static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[])usrPathsField.get(null);

        //check if the path to add is already present
        for(String path : paths) {
            if(path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length-1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

}
