package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Dužinka
 * @version 07.05.2015
 *
 * To be consistent with Twitter parameter name conventions, variables are not camelCased.
 */
public class TwitterApiImpl implements TwitterApi {

    private Config config;

    public static Logger log = LoggerFactory.getLogger(TwitterApiImpl.class);

    private static final String CONSUMER_KEY = "ZbuOrAFMIQ4XiEfYcPbR4KMs8";
    private static final String CONSUMER_SECRET = "mLIasoEApiqJqieZZNjn3tt2QyMoeA8swH2M4oGUmlkRGURE0T";

    // Should work only with my account
    private static final String ACCESS_TOKEN = "3131070003-es91G020NqzcYWbXtv3GqSjcMI98qAUaHhiZHUN";
    private static final String ACCESS_TOKEN_SECRET = "3jzcEHoJMXcemXSRjltpXhJ8Rg6safyR3lBRsmO4QHN93";

    public TwitterApiImpl(Config config) {
        this.config = config;
    }

    /**
     * Get statuses from a timeline.
     * @param count     Number of tweets
     * @return          Sorted set of tweets found
     *
     * @version 26.04.2015
     * Warning: not tested! SSL implementation required.
     */
    @Override
     public SortedSet<Tweet> showTweets(int count) {
        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;
        String oauth_signature_method = "HMAC-SHA1";

        // Generate nonce and timestamp for SSL protocol
        String oauth_nonce = generateNonce();
        String oauth_timestamp = generateTimestamp();

        // The parameter string must be in alphabetical order,
        // therefore it's not a good idea to make it into a separate method.
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

        // Generate authorization header
        String authorization_header_string = generateAuthorizationHeader(oauth_timestamp, oauth_nonce, oauth_signature, oauth_token);

        // Build the parameters into sending method
        HttpRequestBuilder httpBuilder = new HttpRequestBuilder();
        httpBuilder.method("GET");
        httpBuilder.protocol("HTTP/1.1");
        httpBuilder.path(endpoint_path);
        httpBuilder.host(endpoint_host);
        httpBuilder.userAgent("IB053_Project");
        httpBuilder.authorization(authorization_header_string);
        httpBuilder.content("?count=" + count);
        httpBuilder.timestamp(oauth_timestamp);
        String jsonString = null;

        // Send the request
        try {
            log.info("Sending message: \n" +
                        "Parameter string: " + parameter_string +
                        "Signature base string: " + signature_base_string +
                        "Authorization header: " + authorization_header_string);
            jsonString = httpBuilder.send();
            if (jsonString == null) {
                System.err.println("Null response from the server.");
                log.error("Response from httpBuilder.send() was null.");
                return null;
            }
            else {
                log.info("Object returned from httpBuilder.send(): " + jsonString);
            }
        }
        catch (BadResponseCodeException brce) {
            System.err.println("Bad response code from the server.");
            log.error("Bad response code exception: " + brce.getMessage());
        }

        JSONArray jsonArray;
        JSONObject json;

        // If we don't get JSONArray, there is probably an error
        try {
            jsonArray = new JSONArray(jsonString);
        }
        catch (JSONException jse) {
            json = new JSONObject(jsonString);

            if (json.has("errors")) {
                String message_from_twitter = json.getJSONArray("errors").getJSONObject(0).getString("message");
                if (message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you")) {
                    System.err.println("Twitter authorization error.");
                    log.error("Error with authorization:" + message_from_twitter);
                }
                else {
                    System.err.println(json.getJSONArray("errors").getJSONObject(0).getString("message"));
                    log.error(json.getJSONArray("errors").getJSONObject(0).getString("message"));
                }
            }

            return null;
        }

        JSONObject singleTweet;
        SortedSet<Tweet> tweets = new TreeSet<Tweet>();

        Tweet tweet;
        Instant created;
        String text;
        Long id;
        JSONObject user;
        String username;

        // Parse the array of tweets
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                singleTweet = jsonArray.getJSONObject(i);

                id = Long.parseLong(singleTweet.get("id").toString());
                text = singleTweet.get("text").toString();
                String date = singleTweet.get("created_at").toString();

                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EE MMM dd HH:mm:ss Z yyyy", new Locale("en", "EN"));
                LocalDateTime localDate = LocalDateTime.parse(date, dateFormat);
                ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.of("CET"));
                created = zonedDateTime.toInstant();

                user = new JSONObject(singleTweet.get("user").toString());
                username = user.get("screen_name").toString();

                tweet = new Tweet(id, created, username, text);
                tweets.add(tweet);
            }
        }
        catch (NumberFormatException nfe) {
            System.err.println(nfe.getMessage());
            log.error(nfe.getMessage() + "\n" + nfe.getCause());
        }
        catch (JSONException jse) {
            System.err.println(jse.getMessage());
            log.error(jse.getMessage() + "\n" + jse.getCause());
        }

        return tweets;
    }

    /**
     * Posts tweet with user-defined text.
     * @param input     Text to be posted
     * @return          True if status was updated successfully; false if something went wrong
     *
     * @version 29.04.2015
     * Warning: not tested! SSL implementation required.
     */
    @Override
    public boolean sendTweet(String input) {
        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;
        String oauth_signature_method = "HMAC-SHA1";

        // Generate nonce and timestamp for SSL protocol
        String oauth_nonce = generateNonce();
        String oauth_timestamp = generateTimestamp();

        // The parameter string must be in alphabetical order
        String parameter_string =
                "oauth_consumer_key=" + CONSUMER_KEY +
                "&oauth_nonce=" + oauth_nonce +
                "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp +
                "&oauth_token=" + Encode.encode(oauth_token) +
                "&oauth_version=1.0" +
                "&status=" + Encode.encode(input);

        String endpoint = "https://api.twitter.com/1.1/statuses/update.json";
        String endpoint_host = "api.twitter.com";
        String endpoint_path = "/1.1/statuses/update.json";
        String signature_base_string = "POST" + "&"+ Encode.encode(endpoint) + "&" + Encode.encode(parameter_string);

        // Sign base string using CONSUMER_SECRET + "&" + encode(oauth_token_secret)
        String oauth_signature = Encode.hash(signature_base_string, CONSUMER_SECRET, oauth_token_secret);

        // Generate authorization header
        String authorization_header_string = generateAuthorizationHeader(oauth_timestamp, oauth_nonce, oauth_signature, oauth_token);

        // Build the parameters into sending method
        HttpRequestBuilder httpBuilder = new HttpRequestBuilder();
        httpBuilder.method("POST");
        httpBuilder.protocol("HTTP/1.1");
        httpBuilder.path(endpoint_path);
        httpBuilder.host(endpoint_host);
        httpBuilder.userAgent("IB053_Project");
        httpBuilder.authorization(authorization_header_string);
        httpBuilder.content("?status=" + Encode.encode(input));
        httpBuilder.timestamp(oauth_timestamp);

        String jsonString = null;
        JSONObject json;

        // Send the request
        try {
            log.info("Sending message: \n" +
                    "Parameter string: " + parameter_string +
                    "Signature base string: " + signature_base_string +
                    "Authorization header: " + authorization_header_string);
            jsonString = httpBuilder.send();

            if (jsonString == null) {
                System.err.println("Null response from the server.");
                log.error("Response from httpBuilder.send() was null.");
                return false;
            }
            else {
                log.info("Object returned from httpBuilder.send(): " + jsonString);
            }

            json = new JSONObject(jsonString);

            if (json.has("errors")) {
                String message_from_twitter = json.getJSONArray("errors").getJSONObject(0).getString("message");
                if (message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you")) {
                    System.err.println("Twitter authorization error.");
                    log.error("Error with authorization:" + message_from_twitter);
                    return false;
                }
                else {
                    System.err.println(json.getJSONArray("errors").getJSONObject(0).getString("message"));
                    log.error(json.getJSONArray("errors").getJSONObject(0).getString("message"));
                    return false;
                }
            }

            // Check if returned JSON matches sent input
            if (!json.get("text").toString().equals(input)) {
                System.err.println("Communication error, please try again.");
                log.error("Input and returned string don't match.");
                return false;
            }
        }
        catch (BadResponseCodeException brce) {
            System.err.println("Bad response code from the server.");
            log.error("Bad response code exception: " + brce.getMessage());
            return false;
        }
        catch (JSONException jse) {
            System.err.println(jse.getMessage());
            log.error(jse.getMessage() + "\n" + jse.getCause());
            return false;
        }

        return true;
    }

    /**
     * Nonce = number used once. Required by SSL protocol.
     * @return  String of ciphers
     */
    private String generateNonce() {
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        return uuid_string;
    }

    /**
     * Generate timestamp. Required by SSL protocol.
     * @return  String value of current time in seconds.
     */
    private String generateTimestamp() {
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();
        String timestamp = (new Long(ts/1000)).toString(); // Divide by 1000 to get seconds
        return timestamp;
    }

    /**
     * Generates an authorization header.
     * @param oauth_timestamp
     * @param oauth_nonce
     * @param oauth_signature
     * @param oauth_token
     * @return
     */
    private String generateAuthorizationHeader (String oauth_timestamp, String oauth_nonce,
                                                String oauth_signature, String oauth_token) {
        return  "OAuth oauth_consumer_key=\"" + CONSUMER_KEY +
                "\",oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce +
                "\",oauth_version=\"1.0\"," +
                "oauth_signature=\"" + Encode.encode(oauth_signature) +
                "\",oauth_token=\"" + Encode.encode(oauth_token) +
                "\"";
    }

    /**
     * Builder class for filling in a http request and calling a native method
     * @author Wallecnik
     */
    private static class HttpRequestBuilder {
        String method, protocol, path, host, userAgent, authorization, content, timestamp = null;

        public String send() throws BadResponseCodeException, IllegalArgumentException {
            String retval = HttpNative.httpRequest(method, protocol, path, host, userAgent, authorization, content, timestamp);
            method = null;
            protocol = null;
            path = null;
            host = null;
            userAgent = null;
            authorization = null;
            content = null;
            timestamp = null;
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
        public void timestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
