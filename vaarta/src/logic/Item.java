package logic;

import java.time.LocalDateTime;

public class Item implements Comparable<Item> {
    private final LocalDateTime entryTime;
    private final String title;
    private final String description;
    private final String url;

    public Item(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.entryTime = LocalDateTime.now();
        this.url = url;
    }

    public Item(String title, String description, String url, LocalDateTime entryTime) {
        this.title = title;
        this.description = description;
        this.entryTime = entryTime;
        this.url = url;
    }

    @Override
    public int compareTo(Item item) {
        return -1 * entryTime.compareTo(item.entryTime);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getEntryTime() {
        return entryTime.toString();
    }
}
