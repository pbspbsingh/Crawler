package com.singhpra.masti;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.singhpra.masti.common.Loggeable;
import com.singhpra.masti.modal.Episode;
import com.singhpra.masti.modal.Serial;

public class EpisodeParser implements Loggeable {

    private final Serial serial;

    public EpisodeParser(Serial serial) {
        this.serial = serial;
    }

    public List<Episode> process() {
        logger().info("Starting processing of serial: " + serial.getTitle() + " url: " + serial.getHref());
        try {
            final Document document = Jsoup.parse(UTIL.getHTML(serial.getHref()), serial.getHref());
            final Elements elements = document.select("ul.full_episode li");
            logger().info("Number of episodes of " + serial.getTitle() + " are " + elements.size());

            final List<Episode> list = new ArrayList<>(elements.size());
            for (Element element : elements) {
                final Episode episode = new Episode();
                episode.setSerialKey(serial.getKey());
                episode.setDate(element.select("a.date_episodes").text());
                episode.setVideoUrl(element.select("a.date_episodes").attr("href"));
                list.add(episode);
            }
            return list;
        } catch (IOException e) {
            logger().error("Error while fetching episodes of: " + serial.getKey() + ", " + serial.getHref(), e);
        }
        return List.of();
    }
   
}
