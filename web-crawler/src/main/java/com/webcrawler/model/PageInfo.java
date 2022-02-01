package com.webcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.select.Elements;

import javax.persistence.*;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class PageInfo implements Comparable<PageInfo> {
    @Id
    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Transient
    @JsonIgnore
    private Elements links;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "stats", joinColumns = @JoinColumn(name = "url"))
    Map<String, Long> keyWordStats;


    public PageInfo() {
    }

    public PageInfo(String url, String title, Elements links, Map<String, Long> keyWordStats) {
        this.url = url;
        this.title = title;
        this.links = links;
        this.keyWordStats = keyWordStats;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Elements getLinks() {
        return links;
    }

    public void setLinks(Elements links) {
        this.links = links;
    }

    public Map<String, Long> getKeyWordStats() {
        return Map.copyOf(keyWordStats);
    }

    public void setKeyWordStats(Map<String, Long> keyWordStats) {
        this.keyWordStats = keyWordStats;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", keyWordStats=" + keyWordStats +
                '}';
    }

    @Override
    public int compareTo(PageInfo pi) {
        return this.keyWordStats.values().stream().collect(Collectors.summingLong(Long::longValue))
                .compareTo(pi.keyWordStats.values().stream().collect(Collectors.summingLong(Long::longValue)));
    }
}
