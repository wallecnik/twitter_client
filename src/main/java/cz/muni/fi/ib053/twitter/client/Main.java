package cz.muni.fi.ib053.twitter.client;


import cz.muni.fi.ib053.twitter.client.twitterapi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
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

        config = new ConfigImpl();
        try {
            config.load();
        } catch (IOException e) {
            log.error("Unable to load configuration file.", e);
            System.err.println("Unable to load configuration file. \n" + e);
            System.exit(-1);
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
                    System.err.println("Wrong parameter");
                    break;
                }
                boolean sucess = controller.sendTweet(args[1]);
                if (!sucess) {
                    log.error("Unable to send tweet: " + args[1]);
                    System.err.println("Fail...");
                }
                else {
                    System.err.println("Success!");
                }
                break;
            case "get":
                if (args.length != 2) {
                    System.err.println("Wrong parameter");
                    break;
                }
                if (!Pattern.matches("\\d+", args[1])) {
                    System.err.println("Wrong parameter");
                }
                int count = Integer.valueOf(args[1]);
                SortedSet<Tweet> tweets = controller.showTweets(count);
                if (tweets != null) {
                    if (tweets.size() == 0) {
                        log.trace("Empty collection returned for request of" + count + " tweets");
                        System.err.println("No tweets...");
                    } else {
                        for (Tweet tweet : tweets) {
                            System.out.println(tweet);
                        }
                    }
                }
                break;
            case "hello":
                try {
                    System.out.println(HttpNative.httpRequest("", "", "/post.php?dir=FI_MUNI", "posttestserver.com", "", "string", "", ""));
                } catch (BadResponseCodeException e) {
                    e.printStackTrace();
                }
                break;
            case "exit":
                doContinue = false;
                break;
            default:
                System.err.println("\"" + args[0] + "\" was not recognized as internal command.");
                break;
        }
    }
}
