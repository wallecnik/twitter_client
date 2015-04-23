package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;
import org.json.JSONObject;

import java.util.SortedSet;

/**
 * Created by Wallecnik on 14.04.15.
 */
public class TwitterApiImpl implements TwitterApi {

    private Config config;

    public TwitterApiImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean sendTweet(String input) {
        return false;
    }

    @Override
    public SortedSet<Tweet> showTweets(int count) {
        return null;
    }

    public String hello() {
        JSONObject json = new JSONObject();

        return HttpNative.hello();
    }

}
