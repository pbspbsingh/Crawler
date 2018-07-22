package com.singhpra.masti.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util implements Loggeable {

    public static final Util UTIL = new Util();

    private final CloseableHttpClient httpClient;
    private final Gson gson;

    private Util() {
        httpClient = HttpClientBuilder
                    .create()
                    .setDefaultHeaders(List.of(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")))
                    .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger().warn("Error while closing http client.", e);
            }
        }));
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String getHTML(String url) throws IOException {
        return getHTML(url, List.of());
    }

    public String getHTML(String url, List<Header> headers) throws IOException {
        final HttpGet get = new HttpGet(url);
        if (!(headers == null || headers.isEmpty())) {
            for (Header header : headers)
                get.addHeader(header);
        }
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            if (response.getStatusLine().getStatusCode() == 403) {
                logger().fatal("Cloudflare is blocking access: " + url);
                // System.exit(-1);
            }
            if (response.getStatusLine().getStatusCode() != 200)
                throw new IOException("Failed to load the page: " + response.getStatusLine().getStatusCode());

            return EntityUtils.toString(response.getEntity());
        }
    }

    public int head(String url) throws IOException {
        return head(url, List.of());
    }

    public int head(String url, List<Header> headers) throws IOException {
        final HttpHead head = new HttpHead(url);
        if (!(headers == null || headers.isEmpty())) {
            for (Header header : headers)
                head.addHeader(header);
        }
        try (CloseableHttpResponse response = httpClient.execute(head)) {
            return response.getStatusLine().getStatusCode();
        }
    }

    public void writeJSONFile(Object src, String file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))) {
            gson.toJson(src, writer);
        }
    }

}
