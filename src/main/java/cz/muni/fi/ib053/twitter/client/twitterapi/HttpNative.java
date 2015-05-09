package cz.muni.fi.ib053.twitter.client.twitterapi;

import org.jetbrains.annotations.NotNull;
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Matej on 9.5.2015.
 */
public class HttpNative {


    /**
     * this method send a request to host, support POST and GET methods, HTTP/1.1 and HTTP/1.0 protocol
     *
     * @param strMethod required
     * @param strPath optional
     * @param strProtocol required protocol name with version (e.g. HTTP/1.1)
     * @param strHost required
     * @param strUserAgent optional
     * @param strAuthorization required
     * @param strContent optional
     * @return response content or empty String
     * @throws BadResponseCodeException when other response code than 200 was returned or when error occure while trying to connect to server
     * @throws IllegalArgumentException when arguments are incorrect
     */
    public static String httpRequest (
            @NotNull String strMethod,
            @NotNull String strProtocol,
            @NotNull String strPath,
            @NotNull String strHost,
            String strUserAgent,
            @NotNull String strAuthorization,
            String strContent,
            @NotNull String strTimestamp
    ) throws BadResponseCodeException, IllegalArgumentException{
        /*
        * checking for null arguments and react accordingly
        */

        if (strMethod == null){
            throw new IllegalArgumentException("argument method is null");
        }else if (!strMethod.matches("POST") && !strMethod.matches("GET")){

            throw new IllegalArgumentException("method is not GET or POST");
        }
        if (strProtocol == null){
            throw new IllegalArgumentException("argument protocol is null");
        }
        if (strPath == null){
            throw new IllegalArgumentException("argument path is null");
        }
        if (strHost == null){
            throw new IllegalArgumentException("argument host is null");
        }
        if (strUserAgent == null){
            strUserAgent = new String("");
        }
        if (strAuthorization == null){
            throw new IllegalArgumentException("argument authorization is null");
        }
        if (strContent == null){
            strContent = new String("");
        }
        if (strTimestamp == null){
            throw new IllegalArgumentException("argument timestamp is null");
        }

        //set variabiles
        String returnString = new String("");
        HttpParams params = new SyncBasicHttpParams();
        HttpProcessor httpproc = new ImmutableHttpProcessor(
                                        new HttpRequestInterceptor[] {
                                                // Required protocol interceptors
                                                new RequestContent(),
                                                new RequestTargetHost(),
                                                // Recommended protocol interceptors
                                                new RequestConnControl(),
                                                new RequestUserAgent(),
                                                new RequestExpectContinue()
                                        }
                                );
        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(strHost ,443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        //set parameters
        if(strProtocol.matches("HTTP/1.1")){
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        }else if(strProtocol.matches("HTTP/1.0")){
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_0);
        }
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, strUserAgent);
        HttpProtocolParams.setUseExpectContinue(params, false);

        //set attributes
        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        //try block because error error while connecting to server can occur
        try {
            //set and start secure connection
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            SSLSocketFactory ssf = sslcontext.getSocketFactory();
            Socket socket = ssf.createSocket();
            socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()), 0);
            conn.bind(socket, params);

            //create and set request to connection
            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(strMethod, strPath + strContent);
            request.setParams(params);

            // always add the Authorization header
            request.addHeader("Authorization", strAuthorization);

            //process, send and post process message
            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor.execute(request, conn, context);
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);

            //check errors
            if (response.getStatusLine().toString().indexOf("500") != -1) {
                throw new BadResponseCodeException(response.getStatusLine().toString());
            } else {
                returnString = EntityUtils.toString(response.getEntity());
            }
            conn.close();
        }catch (Exception exc){
            throw new BadResponseCodeException("error while sending", exc);
        }

        return  returnString;
    }

}
