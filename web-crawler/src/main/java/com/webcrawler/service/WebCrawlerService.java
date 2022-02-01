package com.webcrawler.service;


import com.webcrawler.model.PageInfo;

import java.util.List;
import java.util.Optional;

public interface WebCrawlerService {

    public void deepCrawl(final String url,
                          final String keyWord,
                          final int depth,
                          final int pageLimit,
                          final List<String> processedUrls);

    public Optional<PageInfo> crawl(String url, String keyWords);
}
