package com.singhpra.masti.main;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.singhpra.masti.Crawler;
import com.singhpra.masti.common.Loggeable;

public class Task implements Loggeable {

    public static final String FILE_NAME = "./data/serials.json";

    private static final long MIN_10 = 10 * 60 * 1000L;

    private final long waitTime;

    public Task(int waitTime) {
        this.waitTime = waitTime * 60 * 60 * 1000L;
    }

    public void run() throws Exception {
        logger().info("Starting crawler task with wait time: " + UTIL.readable(waitTime));

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
                logger().info("Last modified was earlier than " + UTIL.readable(waitTime) + ", so crawling now.");
                new Crawler().start();
            } else {
                logger().info("File was last updated: " + UTIL.readable(diff) + " ago.");
                final long sleepDuration = (waitTime - diff) < MIN_10 ? (waitTime - diff) : MIN_10;
                logger().info("Let's wait for " + UTIL.readable(sleepDuration) + " before we check again.");
                Thread.sleep(sleepDuration);
            }
        }
    }

    private static final Log LOG = LogFactory.getLog(Task.class);

    public static void main(String[] args) {
        try {
            final int waitTime;
            if (args.length > 0) {
                waitTime = Integer.parseInt(args[0]);
            } else {
                waitTime = 6;
            }
            boolean useProxy = true;
            int port = 9050;
            if (args.length > 1)
                useProxy = Boolean.parseBoolean(args[1]);
            if (args.length > 2)
                port = Integer.parseInt(args[2]);

            LOG.info("Use proxy is: " + useProxy + ", with port: " + port);
            if (useProxy) {
                LOG.info("System is set to use socks proxy: 127.0.0.1:" + port);
                System.setProperty("socksProxyHost", "127.0.0.1");
                System.setProperty("socksProxyPort", String.valueOf(port));
            }

            new Task(waitTime).run();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

}
