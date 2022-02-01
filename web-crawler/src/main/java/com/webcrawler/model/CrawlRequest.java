package com.webcrawler.model;

public class CrawlRequest {
    private String url;
    private String keyWord;
    private int depth;
    private int pageLimit;

    public CrawlRequest() {

    }

    public CrawlRequest(String url, String keyWord, int depth, int pageLimit) {
        this.url = url;
        this.keyWord = keyWord;
        this.depth = depth;
        this.pageLimit = pageLimit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    @Override
    public String toString() {
        return "CrawlRequest{" +
                "url='" + url + '\'' +
                ", keyWord='" + keyWord + '\'' +
                ", depth=" + depth +
                ", pageLimit=" + pageLimit +
                '}';
    }
}
