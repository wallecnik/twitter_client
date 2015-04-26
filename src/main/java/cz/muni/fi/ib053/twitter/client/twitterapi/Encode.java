package cz.muni.fi.ib053.twitter.client.twitterapi;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
// Added this line to gradle: compile 'commons-codec:commons-codec:1.4'
import org.apache.commons.codec.binary.Base64;


/**
 * Created by Dužinka on 15. 4. 2015.
 */
public class Encode {

    /**
     * Takes care of unsupported characters and prepares them for Base64 encoding.
     * @param value     String to be encoded
     * @return          Encoded string
     */
    public static String encode(String value)
    {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuilder buf = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    /**
     * Encodes given string in Base64 encoding so that it can be sent via HTTP protocol.
     * Codes 3 octets of binary data in 4 ASCII characters (64 characters maximum).
     * If the original string doesn't contain 3 octets, adds '=' to match the pattern.
     * Also hashes the whole signature with HmacSHA1 method.
     * @param baseString    signature base string = "POST/GET" + host + parameter string
     * @param keyString     twitter consumer secret hashed against '&' (and access token secret, if we have one)
     * @return              Hashed string encoded in Base64 encoding
     * @throws java.security.GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException
    {
        SecretKey secretKey = null;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
    }

    /**
     * Hashes given signature base string against twitter consumer secret + '&' to allow request for a bearer token.
     * If we already have twitter access token, hashes the signature base string
     * against twitter consumer secret + '&' + access token secret.
     * Relies on computeSignature() method, which does the logic (Base64 encoding and HmacSHA1 hashing).
     * @param signature_base_string     Signature base string = "POST/GET" + host + parameter string
     * @param twitter_consumer_secret   Twitter consumer secret
     * @param oauth_token_secret        Twitter access (oauth) token secret. This parameter may be null
     *                                  if we don't have it yet.
     * @return                          Hashed string encoded in Base64 encoding
     */
    public static String hash(String signature_base_string, String twitter_consumer_secret, String oauth_token_secret) {
        String hashedString = null;
        try {
            // if we used more accounts, this would be necessary for retrieving request bearer tokens (no access token secret yet)
            if (oauth_token_secret == null) {
                hashedString = computeSignature(signature_base_string, twitter_consumer_secret + "&");
            }
            else hashedString = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return hashedString;
    }
}
