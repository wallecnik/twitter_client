package cz.muni.fi.ib053.twitter.client;

import cz.muni.fi.ib053.twitter.client.twitterapi.TwitterApi;
import cz.muni.fi.ib053.twitter.client.twitterapi.TwitterApiImpl;

import java.io.IOException;

public class Main {

    public static void main (String[] args) {

        Config config = new ConfigImpl();
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        TwitterApi controller = new TwitterApiImpl(config);

        //run sth here

        try {
            config.store();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
