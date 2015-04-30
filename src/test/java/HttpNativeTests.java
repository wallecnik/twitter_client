import cz.muni.fi.ib053.twitter.client.twitterapi.Tweet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by Wallecnik on 30.04.15.
 */
public class HttpNativeTests {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void fallAtNullPointer() {
        Tweet tweet = null;
        expectedEx.expect(NullPointerException.class);
        tweet.toString();
    }

}
