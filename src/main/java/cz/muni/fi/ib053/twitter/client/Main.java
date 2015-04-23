package cz.muni.fi.ib053.twitter.client;

import java.io.IOException;

public class Main {

    static {
        System.loadLibrary("HttpNative"); // used for tests. This library in classpath only
    }

    public static void main (String[] args) {

        Config config = new ConfigImpl();
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //TwitterApi controller = new TwitterApiImpl(config);

        //String json = "{param:\"content\"}";

        //JSONObject obj = new JSONObject(json);

        //System.out.println(obj.get("param"));



        //run sth here
        //controller.hello();

        try {
            config.store();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
