package com.singhpra.masti;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.singhpra.masti.common.Loggeable;
import com.singhpra.masti.modal.Episode;

public class VideoLinkLoader implements Loggeable {

    public final static VideoLinkLoader INS = new VideoLinkLoader();

    private VideoLinkLoader() {
    }

    public Episode get(Episode episode) {
        try {
            final Document document = Jsoup.parse(UTIL.getHTML(episode.getVideoUrl()), episode.getVideoUrl());
            final Elements elements = document.select(".download_link1 .channel_cont ul li a");
            if (elements.isEmpty()) {
                logger().warn("Couldn't find episodes for " + episode + " reason: episodes link not found.");
                return null;
            }
            final String link = elements.get(0).attr("href");
            final String html = UTIL.getHTML(link, List.of(new BasicHeader("Referer", episode.getVideoUrl())));
            int index = html.indexOf("goodyaar.com");
            if (index == -1) {
                index = html.indexOf("bollywoodjoint.com");
                if (index == -1) {
                    logger().warn("Couldn't find goodyaar or bollywoodjoint video in [" + link + "] from [" + episode.getVideoUrl() + "]");
                    return null;
                }
            }
            int x = index, y = index;
            while (x > 0 && html.charAt(x - 1) != '"')
                x--;
            while (y < html.length() && html.charAt(y) != '"')
                y++;

            final String videoUrl = html.substring(x, y);
            logger().info("Successfully got video url for: " + episode + " --> " + videoUrl);

            episode.setVideoUrl(videoUrl);
            episode.setHash(UUID.nameUUIDFromBytes(videoUrl.getBytes()).toString());
            return episode;
        } catch (IOException e) {
            logger().warn("Failed to load video link: " + episode + ": reason " + e.getMessage());
            return null;
        }
    }

}
