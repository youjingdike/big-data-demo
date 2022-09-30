package com.xq.tst;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class TestEs {

    private RestHighLevelClient rhClient = null;

    @Before
    public void init() {
        String securityUser = "es-app:ENUJZcBfyR7Jqr5w";
        HttpHost[] httpHosts = {
                new HttpHost("10.69.74.220",19201,"http")
        };

        RestClientBuilder builder = RestClient.builder(httpHosts);
        byte[] encodedAuth = Base64.encodeBase64(securityUser.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        builder.setDefaultHeaders(new BasicHeader[]{new BasicHeader("Authorization", authHeader)});
        rhClient = new RestHighLevelClient(builder);
    }

    @After
    public void close() {
        if (rhClient != null) {
            try {
                rhClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testCreateIndex() {

    }


}
