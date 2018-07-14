package com.singhpra.masti.main;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.LogFactory;

import com.singhpra.masti.Crawler;
import com.singhpra.masti.common.Loggeable;

public class Task implements Loggeable {

    public static final String FILE_NAME = "./data/serials.json";

    private final long waitTime;

    public Task(int waitTime) {
        this.waitTime = waitTime * 60 * 60 * 1000L;
    }

    public void run() throws Exception {
        logger().info("Starting crawler task with wait time: " + readable(waitTime));

        while (true) {
            final File file = new File(FILE_NAME);
            if (file.exists()) {
                logger().info("File was last modified at: " + new Date(file.lastModified()));
            } else {
                logger().warn("File doesn't exist.");
            }

            final long lastModified = file.exists() ? file.lastModified() : 0L;
            final long diff = System.currentTimeMillis() - lastModified;
            if (diff >= waitTime) {
                logger().info("Last modified was earlier than " + readable(waitTime) + ", so crawling now.");
                new Crawler().start();
            } else {
                logger().info("File was last updated: " + readable(diff) + " ago.");
                logger().info("Let's wait for " + readable(waitTime - diff) + " before we start again.");
                Thread.sleep(waitTime - diff);
            }
        }
    }

    private String readable(final long timestamp) {
        double time = timestamp * 1.0 / (1000 * 60);
        if (time <= 60)
            return String.format("%2.2f minutes", time);
        else
            return String.format("%d hours, %2.2f minutes", (int) time / 60, time % 60);
    }

    public static void main(String[] args) {
        try {
            final int waitTime;
            if (args.length > 0) {
                waitTime = Integer.parseInt(args[0]);
            } else {
                waitTime = 6;
            }
            new Task(waitTime).run();
        } catch (Exception e) {
            LogFactory.getLog(Task.class).error(e);
        }
    }

}
