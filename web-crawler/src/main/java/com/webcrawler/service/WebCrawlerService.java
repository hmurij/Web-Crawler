package com.webcrawler.service;


import com.webcrawler.model.PageInfo;

import java.util.List;
import java.util.Optional;

public interface WebCrawlerService {

    /**
     * Recursively visits links on web page up to maximum depth and visted page limit.
     * @param url seed url.
     * @param keyWord comma delimited keywords to search for.
     * @param depth maximum depth to crawl.
     * @param pageLimit maximum visited pages limit.
     * @param processedUrls list of visited web pages.
     */
    public void deepCrawl(final String url,
                          final String keyWord,
                          final int depth,
                          final int pageLimit,
                          final List<String> processedUrls);

    /**
     * Get page information packed to @{@link PageInfo} object.
     * @param url web page url.
     * @param keyWords comma delimited keywords to search for.
     * @return Optional page info - url, title, links, keyword hits statistics per page.
     */
    public Optional<PageInfo> crawl(String url, String keyWords);
}
