package io.github.sullis.httpclient.example;

import java.util.Properties;

public class AbstractTest {
    static {
        initLogging();
    }

    static void initLogging() {
        Properties props = System.getProperties();

        props.setProperty("jdk.internal.httpclient.debug", "false");

        // use "all" to enable verbose output
        props.setProperty("jdk.httpclient.HttpClient.log", "off");
    }
}
