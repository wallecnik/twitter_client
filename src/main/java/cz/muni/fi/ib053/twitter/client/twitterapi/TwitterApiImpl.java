package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.*;

/**
 * @author Dužinka
 * @version 26.04.2015
 *
 * To be consistent with Twitter parameter name conventions, variables are not camelCased.
 */
public class TwitterApiImpl implements TwitterApi {

    private Config config;

    private static final String CONSUMER_KEY = "ZbuOrAFMIQ4XiEfYcPbR4KMs8";
    private static final String CONSUMER_SECRET = "mLIasoEApiqJqieZZNjn3tt2QyMoeA8swH2M4oGUmlkRGURE0T";

    // Should work only with my account
    private static final String ACCESS_TOKEN = "3131070003-es91G020NqzcYWbXtv3GqSjcMI98qAUaHhiZHUN";
    private static final String ACCESS_TOKEN_SECRET = "3jzcEHoJMXcemXSRjltpXhJ8Rg6safyR3lBRsmO4QHN93";

    public TwitterApiImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean sendTweet(String input) {
        return false;
    }

    /**
     * Get statuses from a timeline.
     * @param count     Number of tweets
     * @return          Sorted set of tweets found
     */
    @Override
    public SortedSet<Tweet> showTweets(int count) {
        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;
        String oauth_signature_method = "HMAC-SHA1";

        // Generate nonce = number used once
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string;

        // Get timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();
        String oauth_timestamp = (new Long(ts/1000)).toString(); // Divide by 1000 to get seconds

        // The parameter string must be in alphabetical order
        String parameter_string =
                "count=" + count +
                "&oauth_consumer_key=" + CONSUMER_KEY +
                "&oauth_nonce=" + oauth_nonce +
                "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp +
                "&oauth_token=" + Encode.encode(oauth_token) +
                "&oauth_version=1.0";
        String endpoint = "https://api.twitter.com/1.1/statuses/home_timeline.json";
        String endpoint_host = "api.twitter.com";
        String endpoint_path = "/1.1/statuses/home_timeline.json";
        String signature_base_string = "GET" + "&"+ Encode.encode(endpoint) + "&" + Encode.encode(parameter_string);

        // Sign base string using CONSUMER_SECRET + "&" + encode(oauth_token_secret)
        String oauth_signature = Encode.hash(signature_base_string, CONSUMER_SECRET, oauth_token_secret);

        String authorization_header_string =
                "OAuth oauth_consumer_key=\"" + CONSUMER_KEY +
                "\",oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce +
                "\",oauth_version=\"1.0\"," +
                "oauth_signature=\"" + Encode.encode(oauth_signature) +
                "\",oauth_token=\"" + Encode.encode(oauth_token) +
                "\"";

        HttpRequestBuilder httpBuilder = new HttpRequestBuilder();
        httpBuilder.method("POST");
        httpBuilder.protocol("HTTP/1.1");       // possibly change to SSL?
        httpBuilder.path(endpoint_path);
        httpBuilder.host(endpoint_host);
        httpBuilder.userAgent("IB053_Project"); // HttpCore/1.1
        httpBuilder.authorization(authorization_header_string);
        httpBuilder.content("?count=" + count);

        String jsonString = null;
        try {
            jsonString = httpBuilder.send();
        }
        catch (BadResponseCodeException brce) {
            brce.printStackTrace();
        }

        if (jsonString == null) {
            System.err.println("Object returned by Twitter is null!");
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();

        // If we don't get JSONArray, there is probably an error
        try {
            jsonArray = new JSONArray(jsonString);
        }
        catch (JSONException jse) {
            json = new JSONObject(jsonString);

            // TODO: Create new Twitter-related exception and pass it to higher level of application
            if (json.has("errors")) {
                String message_from_twitter = json.getJSONArray("errors").getJSONObject(0).getString("message");
                if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
                    System.err.println("Twitter authorization error.");
                else
                    System.err.println(json.getJSONArray("errors").getJSONObject(0).getString("message"));
            }

            return null;
        }

        JSONObject singleTweet;
        SortedSet<Tweet> tweets = new TreeSet<Tweet>();

        Tweet tweet;
        String created;
        String text;
        Long id;
        JSONObject user;
        String username;

        try {
            // Parse the array of tweets
            for (int i = 0; i < jsonArray.length(); i++) {
                singleTweet = jsonArray.getJSONObject(i);
                id = Long.parseLong(singleTweet.get("id").toString());
                text = singleTweet.get("text").toString();
                created = singleTweet.get("created_at").toString();

                user = new JSONObject(singleTweet.get("user").toString());
                username = user.get("screen_name").toString();

                tweet = new Tweet(id, created, username, text);
                tweets.add(tweet);
            }
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }

        return tweets;
    }

    public String hello() {
        JSONObject json = new JSONObject();

        return HttpNative.hello();
    }

    /**
     * Builder class for filling in a http request and calling a native method
     * @author Wallecnik
     */
    private static class HttpRequestBuilder {
        String method, protocol, path, host, userAgent, authorization, content = null;

        public String send() throws BadResponseCodeException {
            String retval = HttpNative.httpRequest(method, protocol, path, host, userAgent, authorization, content);
            method = null;
            protocol = null;
            path = null;
            host = null;
            userAgent = null;
            authorization = null;
            content = null;
            return retval;
        }

        public void method(String method) {
            this.method = method;
        }
        public void protocol(String protocol) {
            this.protocol = protocol;
        }
        public void path(String path) {
            this.path = path;
        }
        public void host(String host) {
            this.host = host;
        }
        public void userAgent(String userAgent) {
            this.userAgent = userAgent;
        }
        public void authorization(String authorization) {
            this.authorization = authorization;
        }
        public void content(String content) {
            this.content = content;
        }

    }
}
