package cz.muni.fi.ib053.twitter.client.twitterapi;

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
            String method,
            String protocol,
            String path,
            String host,
            String userAgent,
            String authorization,
            String content
    ) throws BadResponseCodeException;

}
