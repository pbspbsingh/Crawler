package com.singhpra.masti;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.singhpra.masti.common.Loggeable;

public class ChannelParser implements Loggeable {

    private final String url;

    public ChannelParser(String url) {
        this.url = url;
    }

    public List<String> getChannels() {
        logger().info("Trying to fetch the list of channels from url: " + this.url);
        try {
            final Document document = Jsoup.parse(UTIL.getHTML(url), url);
            final Elements elements = document.select(".chennel_list .serial_list .serial_img a");
            logger().info("Number of chennels found: " + elements.size());
            return elements.stream().map(e -> e.attr("href")).collect(Collectors.toList());
        } catch (IOException e) {
            logger().error("Error while fetching channel list", e);
        }
        return List.of();
    }
}
