package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;

/**
 * Created by Wallecnik on 14.04.15.
 */
public class TwitterApiImpl implements TwitterApi {

    private Config config;

    public TwitterApiImpl(Config config) {
        this.config = config;
    }

    public String hello() {
        return HttpNative.hello();
    }

}
