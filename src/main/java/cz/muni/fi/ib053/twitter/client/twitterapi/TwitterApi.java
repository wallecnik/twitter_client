package cz.muni.fi.ib053.twitter.client.twitterapi;

import java.util.List;

/**
 * Created by Wallecnik on 14.04.15.
 */
public interface TwitterApi {

    String hello();

    void sendTweet(String input);

    List<String> showTweets(int count);

    void requestSignIn();

    void signIn(String username, String password);

    void sendPin(String pin);

    boolean isSignedIn();

}
