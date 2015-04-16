package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;

import java.util.List;

/**
 * Created by Wallecnik on 14.04.15.
 */
public class TwitterApiImpl implements TwitterApi {
    @Override
    public void sendTweet(String input) {

    }

    @Override
    public List<String> showTweets(int count) {
        return null;
    }

    @Override
    public void requestSignIn() {

    }

    @Override
    public void signIn(String username, String password) {

    }

    @Override
    public void sendPin(String pin) {

    }

    @Override
    public boolean isSignedIn() {
        return false;
    }

    private Config config;

    public TwitterApiImpl(Config config) {
        this.config = config;
    }

    public String hello() {

        return HttpNative.hello();
    }

}
