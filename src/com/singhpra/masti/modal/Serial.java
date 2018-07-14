package com.singhpra.masti.modal;

import java.util.List;

public class Serial implements Comparable<Serial> {

    private String key;
    private String title;
    private String href;
    private List<Episode> episodes;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        return "Serial [key=" + key + ", title=" + title + ", href=" + href + ", episodes="
                + (episodes != null ? episodes.size() : null) + "]";
    }

    @Override
    public int compareTo(Serial that) {
        return this.title.compareTo(that.title);
    }

}
