package appui;

import logic.Feed;
import logic.Strategy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class Data {
    private static final String CONFIG_FILE_NAME = "config.ini";
    private static final String FEED_COUNT = "feedCount";
    private static final String FEED_URL = "feedUrl";
    private static final String STRATEGY = "strategy";

    private static volatile Data instance;
    private final List<Feed> feeds;
    private Strategy mergeStrategy;

    private Data() {
        feeds = new ArrayList<>();
        Feed af = new Feed("http://localhost");
        af.setTitle("All feeds");
        feeds.add(af);
        mergeStrategy = Strategy.MULTI_CONCURRENT;
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(CONFIG_FILE_NAME));
            mergeStrategy = Strategy.valueOf(config.getProperty(STRATEGY));
            int feedCount = Integer.parseInt(config.getProperty(FEED_COUNT));
            for (int i = 1; i < feedCount; i++) {
                feeds.add(new Feed(config.getProperty(FEED_URL + i)));
            }
        } catch (Exception f) {
            f.printStackTrace();
            mergeStrategy = Strategy.MULTI_CONCURRENT;
        }
    }

    static Data getInstance() {
        if (instance == null) {
            synchronized (Data.class) {
                if (instance == null) {
                    instance = new Data();
                }
            }
        }
        return instance;
    }

    void addFeed(String url) {
        feeds.add(new Feed(url));
    }

    List<Feed> getFeeds() {
        return feeds;
    }

    Strategy getMergeStrategy() {
        return mergeStrategy;
    }

    void writeProperties() {
        Properties config = new Properties();
        config.setProperty(STRATEGY, mergeStrategy.toString());
        config.setProperty(FEED_COUNT, Integer.toString(feeds.size()));
        for (int i = 1; i < feeds.size(); i++) {
            config.setProperty(FEED_URL + i, feeds.get(i).getUrl());
        }
        try {
            config.store(new FileOutputStream(CONFIG_FILE_NAME), null);
        } catch (Exception f) {
            f.printStackTrace();
        }
    }
}
