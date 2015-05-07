package cz.muni.fi.ib053.twitter.client.twitterapi;

import com.sun.istack.internal.NotNull;

/**
 * Created by Wallecnik on 14.04.15.
 */
public class HttpNative {

    public static native String hello();

    /**
     * TODO: fill in
     *
     * @param method required
     * @param path optional 
     * @param protocol required protocol name with version (e.g. HTTP/1.1)
     * @param host required 
     * @param userAgent optional 
     * @param authorization optional 
     * @param content optional 
     * @return response content or null
     * @throws BadResponseCodeException whe other response code than 200 was returned
     */
    public static native String httpRequest (
            @NotNull String method,
            @NotNull String protocol,
            @NotNull String path,
            @NotNull String host,
            String userAgent,
            @NotNull String authorization,
            String content,
            @NotNull String timestamp
    ) throws BadResponseCodeException;

}
