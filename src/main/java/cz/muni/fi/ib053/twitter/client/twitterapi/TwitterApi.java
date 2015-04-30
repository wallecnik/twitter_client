package cz.muni.fi.ib053.twitter.client.twitterapi;

import org.json.JSONObject;

import java.util.SortedSet;

/**
 * Created by Wallecnik on 14.04.15.
 */
public interface TwitterApi {

    String hello();

    boolean sendTweet(String input);

    /**
     * Returns SortedSet of <code>count</code> last tweets.
     * Latest tweets are listed first.
     *
     * @param count
     * @return
     */
    SortedSet<Tweet> showTweets(int count);
}
