package cz.muni.fi.ib053.twitter.client.twitterapi;

import java.util.SortedSet;

/**
 * Created by Wallecnik on 14.04.15.
 */
public interface TwitterApi {

    String hello();

    boolean sendTweet(String input);

    /**
     * Returns List of <code>count</code> last tweets
     *
     * @param count
     * @return
     */
    SortedSet<Tweet> showTweets(int count);

}
