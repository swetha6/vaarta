package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Feed {
    private final List<Item> items;
    private final String url;
    private LocalDateTime lastRefreshedTime;
    private String title;
    private String description;
    AtomicBoolean lock;

    public Feed(String url) {
        this.url = url;
        items = new ArrayList<>();
        lock = new AtomicBoolean();
    }

    public void emptyItemsList() {
        items.clear();
    }

    public void addItem(Item e) {
        items.add(e);
    }

    public void replaceAll(Collection<Item> l) {
        items.clear();
        items.addAll(l);
        lock.compareAndSet(true, false);
    }

    public List<Item> getItems() {
        return items;
    }

    public void lock() {
        while(lock.get()) {
        }
        lock.getAndSet(true);
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastRefreshedTime() {
        return lastRefreshedTime;
    }

    public void setLastRefreshedTime(LocalDateTime lastRefreshedTime) {
        this.lastRefreshedTime = lastRefreshedTime;
    }

}
