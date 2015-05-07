package cz.muni.fi.ib053.twitter.client.twitterapi;

import cz.muni.fi.ib053.twitter.client.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;;

/**
 * Created by Wallecnik on 14.04.15.
 * @author Dužinka
 * @version 23.04.2015
 *
 * To be consistent with Twitter parameter name conventions, variables are not camelCased.
 *
 * TODO: Transform code between comments in getStatus() to a new class
 * INPUT: HttpBuilder
 * OUTPUT: response String
 * THROW: BadResponseCodeException
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
    public String callHttpNative() {
        return HttpNative.hello();
    }

    @Override
    public String getMessage() {
        return HttpNative.getMsg("URL get");
    }

    @Override
    public String sendMessage(String body) {
        return HttpNative.sendMsg("URL send", body);
    }



    /**
     * Get statuses from timeline that is bound to this account.
     * @param count     number of tweets
     * @return          JSONObject of found tweets
     */
    @Override
     public JSONObject getStatus(int count)
    {
        JSONObject jsonresponse = new JSONObject();

        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;
        String oauth_signature_method = "HMAC-SHA1";

        // Get timestamp
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string;

        // Get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // The parameter string must be in alphabetical order
        String parameter_string = "count=" + count + "&oauth_consumer_key=" + CONSUMER_KEY + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + Encode.encode(oauth_token) + "&oauth_version=1.0";
        System.out.println("parameter_string = " + parameter_string);
        String endpoint = "https://api.twitter.com/1.1/statuses/home_timeline.json";
        String endpoint_host = "api.twitter.com";
        String endpoint_path = "/1.1/statuses/home_timeline.json";

        //String endpoint = "https://posttestserver.com/post.php?dir=FI_MUNI";
        //String endpoint_host = "https://posttestserver.com";
        //String endpoint_path = "post.php?dir=FI_MUNI";

        String signature_base_string = "GET" + "&"+ Encode.encode(endpoint) + "&" + Encode.encode(parameter_string);
        System.out.println("signature_base_string = " + signature_base_string);

        // Sign base string using CONSUMER_SECRET + "&" + encode(oauth_token_secret)
        String oauth_signature = Encode.hash(signature_base_string, CONSUMER_SECRET, oauth_token_secret);

        /*String authorization_header_string = "OAuth oauth_consumer_key=\"" + CONSUMER_KEY + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + Encode.encode(oauth_signature) + "\",oauth_token=\"" + Encode.encode(oauth_token) + "\"";
*/
        String authorization_header_string =
                "OAuth oauth_consumer_key=\"" + CONSUMER_KEY +
                "\",oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce +
                "\",oauth_version=\"1.0\"," +
                "oauth_signature=\"" + Encode.encode(oauth_signature) +
                "\",oauth_token=\"" + Encode.encode(oauth_token) +
                "\"";
        System.out.println("authorization_header_string = " + authorization_header_string);

        System.out.println("Encode.encode(oauth_signature) = " + Encode.encode(oauth_signature));
        System.out.println("Encode.encode(oauth_token) = " + Encode.encode(oauth_token));
        System.out.println("oauth_timestamp = " + oauth_timestamp);




        /**
         * Transform following code into a class:
         */

        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(endpoint_host,443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);

                // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", endpoint_path + "?count=" + count); // ?parameters= are given by httpBuilder()
                //System.out.println("URL = " + endpoint_path + "?count=" + count);
                request2.setParams(params);
                request2.addHeader("Authorization", authorization_header_string); // always add the Authorization header
                httpexecutor.preProcess(request2, httpproc, context);
                HttpResponse response2 = httpexecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpexecutor.postProcess(response2, httpproc, context);

                if(response2.getStatusLine().toString().indexOf("500") != -1)
                {
                    jsonresponse.put("response_status", "error");       // insted of putting it into JSON object, throw BadCodeException (I'll catch it)
                    jsonresponse.put("message", "Twitter auth error.");
                }
                else
                {
                    String jsonString = EntityUtils.toString(response2.getEntity());

                    /**
                     * End of HTTP code
                     */



                    JSONArray jsonArray = new JSONArray();
                    JSONObject json = new JSONObject();

                    // If we don't get JSONArray, there is probably an error
                    try {
                        System.out.println("Before array");
                        jsonArray = new JSONArray(jsonString);
                        System.out.println("After array");
                    }
                    catch (JSONException jse) {
                        json = new JSONObject(jsonString);

                        if (json.has("errors")) {
                            String message_from_twitter = json.getJSONArray("errors").getJSONObject(0).getString("message");
                            if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
                                System.err.println("Twitter authorization error.");
                            else
                                System.err.println(json.getJSONArray("errors").getJSONObject(0).getString("message"));
                        }
                        else {
                            System.out.println("Response: " + json);
                        }

                        return null;
                    }

                    JSONObject singleTweet;

                    Tweet tweet;
                    //Instant created;
                    String created;
                    String text;
                    Long id;
                    JSONObject user;
                    String username;

                    // Parse the array of tweets
                    for (int i = 0; i < jsonArray.length(); i++) {
                        singleTweet = jsonArray.getJSONObject(i);
                        id = Long.parseLong(singleTweet.get("id").toString());
                        text = singleTweet.get("text").toString();
                        created = singleTweet.get("created_at").toString();
                        user = new JSONObject(singleTweet.get("user").toString());
                        username = user.get("screen_name").toString();
                        tweet = new Tweet(id, created, username, text);
                        jsonresponse.put(i+1 + ". TWEET", singleTweet);
                        System.out.println(tweet.toString());
                    }
                    conn.close();
                }
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets KeyManagementException message=" + kme.getMessage());
            }
            finally {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {
            System.out.println(jsone.getMessage());
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }

        return jsonresponse;
    }

    // This is the update status example
    // Urls (even short ones) will be wrapped and be counted as a certain length defined by Twitter configuration (~22 chars as of this writing). There is a 140 char max, including this limitation
    // INPUT:the user's access_token and the user's access_token_secret and the text of the status update
    // OUTPUT: if successful, the tweet gets posted.

    /**
     * Posts a new tweet.
     * @param text      String text to be posted
     * @return          JSONObject of HTTP response - a tweet that was just created
     */
    @Override
    public JSONObject updateStatus(String text)
    {
        JSONObject jsonresponse = new JSONObject();

        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;

        // generate authorization header
        String oauth_signature_method = "HMAC-SHA1";

        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string;

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();
        String oauth_timestamp = (new Long(ts/1000)).toString();    // convert to seconds

        // the parameter string must be in alphabetical order, "text" parameter added at end
        String parameter_string = "oauth_consumer_key=" + CONSUMER_KEY + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + Encode.encode(oauth_token) + "&oauth_version=1.0&status=" + Encode.encode(text);
        System.out.println("parameter_string = " + parameter_string);

        String endpoint = "https://api.twitter.com/1.1/statuses/update.json";
        String endpoint_host = "api.twitter.com";
        String endpoint_path = "/1.1/statuses/update.json";

        // Converts parameter_string's special characters
        String signature_base_string = "POST" + "&"+ Encode.encode(endpoint) + "&" + Encode.encode(parameter_string);
        System.out.println("signature_base_string = " + signature_base_string);
        String oauth_signature = Encode.hash(signature_base_string, CONSUMER_SECRET, oauth_token_secret);
        System.out.println("oauth_signature = " + oauth_signature);

        String authorization_header_string = "OAuth oauth_consumer_key=\"" + CONSUMER_KEY + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + Encode.encode(oauth_signature) + "\",oauth_token=\"" + Encode.encode(oauth_token) + "\"";
        System.out.println("authorization_header_string=" + authorization_header_string);

        // Following code is copy-pasted to simulate HTTP connection
        // and is meant to be replaced with a native method call

        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(endpoint_host,443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try
        {
            try
            {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("POST", endpoint_path);
                // need to add status parameter to this POST
                request2.setEntity( new StringEntity("status=" + Encode.encode(text), "application/x-www-form-urlencoded", "UTF-8"));
                System.out.println("Parameters = " + "status=" + Encode.encode(text));  // Control check
                request2.setParams(params);
                request2.addHeader("Authorization", authorization_header_string);
                httpexecutor.preProcess(request2, httpproc, context);
                HttpResponse response2 = httpexecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpexecutor.postProcess(response2, httpproc, context);
                String responseBody = EntityUtils.toString(response2.getEntity());
                System.out.println("response=" + responseBody);
                // error checking here. Otherwise, status should be updated.
                jsonresponse = new JSONObject(responseBody);
                conn.close();
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "updateStatus HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "updateStatus NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "updateStatus KeyManagementException message=" + kme.getMessage());
            }
            finally
            {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {
            jsone.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

        return jsonresponse;
    }

    /**
     * Search the feed for any tweet that matches given string
     * @param q     a string to search for
     * @return      JSONObject of HTTP response (all tweets which contain given string)
     */
    @Override
    public JSONObject searchTweets(String q) {
        JSONObject jsonresponse = new JSONObject();

        String oauth_token = ACCESS_TOKEN;
        String oauth_token_secret = ACCESS_TOKEN_SECRET;
        String oauth_signature_method = "HMAC-SHA1";

        // Get timestamp
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string;

        // Get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // The parameter string must be in alphabetical order
        // 3 extra params are added to the request, "lang", "result_type" and "q".
        String parameter_string = "lang=en&oauth_consumer_key=" + CONSUMER_KEY + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + Encode.encode(oauth_token) + "&oauth_version=1.0&q=" + Encode.encode(q) + "&result_type=mixed";
        System.out.println("parameter_string = " + parameter_string);
        String endpoint = "https://api.twitter.com/1.1/search/tweets.json";
        String endpoint_host = "api.twitter.com";
        String endpoint_path = "/1.1/search/tweets.json";
        String signature_base_string = "GET" + "&"+ Encode.encode(endpoint) + "&" + Encode.encode(parameter_string);
        System.out.println("signature_base_string = " + signature_base_string);

        // Sign base string using CONSUMER_SECRET + "&" + encode(oauth_token_secret)
        String oauth_signature = Encode.hash(signature_base_string, CONSUMER_SECRET, oauth_token_secret);

        String authorization_header_string = "OAuth oauth_consumer_key=\"" + CONSUMER_KEY + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + Encode.encode(oauth_signature) + "\",oauth_token=\"" + Encode.encode(oauth_token) + "\"";
        System.out.println("authorization_header_string = " + authorization_header_string);

        // Following code is copy-pasted to simulate HTTP connection
        // and is meant to be replaced with a native method call

        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(endpoint_host,443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);

                // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", endpoint_path + "?lang=en&result_type=mixed&q=" + Encode.encode(q));
                request2.setParams(params);
                request2.addHeader("Authorization", authorization_header_string); // always add the Authorization header
                httpexecutor.preProcess(request2, httpproc, context);
                HttpResponse response2 = httpexecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpexecutor.postProcess(response2, httpproc, context);

                if(response2.getStatusLine().toString().indexOf("500") != -1)
                {
                    jsonresponse.put("response_status", "error");
                    jsonresponse.put("message", "Twitter auth error.");
                }
                else
                {
                    // if successful, the response should be a JSONObject of tweets
                    JSONObject jo = new JSONObject(EntityUtils.toString(response2.getEntity()));
                    if(jo.has("errors"))
                    {
                        jsonresponse.put("response_status", "error");
                        String message_from_twitter = jo.getJSONArray("errors").getJSONObject(0).getString("message");
                        if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
                            jsonresponse.put("message", "Twitter auth error.");
                        else
                            jsonresponse.put("message", jo.getJSONArray("errors").getJSONObject(0).getString("message"));
                    }
                    else
                    {
                        jsonresponse.put("twitter_jo", jo); // this is the full result object from Twitter
                    }

                    conn.close();
                }
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets KeyManagementException message=" + kme.getMessage());
            }
            finally {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {

        }
        catch(IOException ioe)
        {

        }
        return jsonresponse;
    }
}

final class Tweet implements Comparable<Tweet> {

    //private final Instant   created;
    private final String    created;
    private final String    text;
    private final Long      id;
    private final String    username;

    public Tweet(Long id, String created, String username, String text) {
        this.id      = id;
        this.created = created;
        this.username = username;
        this.text = text;
    }

    public String getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Tweet o) {
        return this.created.compareTo(o.created);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}