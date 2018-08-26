package com.singhpra.masti;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.singhpra.masti.common.Loggeable;
import com.singhpra.masti.main.Task;
import com.singhpra.masti.modal.Episode;
import com.singhpra.masti.modal.Serial;

public class Crawler implements Loggeable {

    private static final String URL = "http://apnetv.tv/Hindi-Serials";

    static {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
    }

    public void start() throws Exception {
        final long startTime = System.currentTimeMillis();
        logger().info("Starting crawler with URL: " + URL);
        try {
            final List<String> channels = new ChannelParser(URL).getChannels();
            final List<Serial> serials = channels.parallelStream()
                                                .map(SerialParser::new)
                                                .map(SerialParser::parse)
                                                .flatMap(List::stream)
                                                .collect(Collectors.toList());
            logger().info("Total number of serials fetched: " + serials.size());
            
            UTIL.writeJSONFile(serials, "./data/all.json");
            final Set<String> interested = interesedSerials();
            final List<Episode> episodes = serials.stream()
                                            .filter(s -> interested.isEmpty() || interested.contains(s.getKey()))
                                            .parallel()
                                            .map(EpisodeParser::new)
                                            .map(EpisodeParser::process)
                                            .flatMap(List::stream)
                                            .collect(Collectors.toList());
            logger().info("Total episodes fetched: " + episodes.size());
           
            final List<Episode> cleanEpisodes = episodes
                                                .parallelStream()
                                                .map(VideoLinkLoader.INS::get)
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());
            logger().info("Original episodes: " + episodes.size() + ", video fetched for " + cleanEpisodes.size());
            
            final Map<String, Serial> serialsMap = serials.stream().collect(Collectors.toMap(Serial::getKey, Function.identity()));
            for (Episode episode : cleanEpisodes) {
                final Serial serial = serialsMap.get(episode.getSerialKey());
                if (serial.getEpisodes() == null)
                    serial.setEpisodes(new ArrayList<>());

                episode.setSerialKey(null);
                serial.getEpisodes().add(episode);
            }
            final List<Serial> cleanSerials = serials.stream()
                                                    .filter(s -> !(s.getEpisodes() == null || s.getEpisodes().isEmpty()))
                                                    .collect(Collectors.toList());
            Collections.sort(cleanSerials);
            for (Serial serial : cleanSerials)
                Collections.sort(serial.getEpisodes());
            
            UTIL.writeJSONFile(cleanSerials, Task.FILE_NAME);
        } catch (Exception e) {
            logger().error("Error while crawling.", e);
            throw e;
        }
        logger().info("Time taken to download serials info: " + (System.currentTimeMillis() - startTime));
    }

    private Set<String> interesedSerials() throws IOException {
        final Predicate<String> emptyCheck = String::isEmpty;
        final Path path = Paths.get("./data/serials.txt");
        return path.toFile().exists() 
                ? Files.lines(path)
                        .map(String::trim)
                        .filter(emptyCheck.negate())
                        .collect(Collectors.toUnmodifiableSet()) 
                : Set.of();
    }

    public static void main(String[] args) throws Exception {
        new Crawler().start();
    }
}
