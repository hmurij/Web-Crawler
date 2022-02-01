package com.webcrawler.service.impl;

import com.webcrawler.dao.PageInfoRepository;
import com.webcrawler.model.PageInfo;
import com.webcrawler.service.WebCrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequestScope
public class WebCrawlerServiceImpl implements WebCrawlerService {

    private static final Logger log = LoggerFactory.getLogger(WebCrawlerService.class);
    private int pageCount = 0;

    @Autowired
    private PageInfoRepository pageInfoRepository;

    @Override
    public void deepCrawl(final String url, final String keyWord, final int depth, final int pageLimit, final List<String> processedUrls) {


        log.info("Starting crawler on url: {} visited pages: {}", url, pageCount);

        if (depth < 0) {
            log.info("Maximum depth reached, backing out of url: {}", url);
        } else if (pageCount == pageLimit) {
            log.info("Maximum visited pages limit reached, backing out of url: {}", url);
        } else {
            final List<String> updatedProcessedUrls = Optional.ofNullable(processedUrls).orElse(new ArrayList<>());

            if (!updatedProcessedUrls.contains(url)) {
                updatedProcessedUrls.add(url);

                crawl(url, keyWord).ifPresent(pageInfo -> {
                    log.debug("Saving url: {} stats to db", url);
                    pageInfoRepository.save(pageInfo);

                    pageInfo.getLinks().forEach(link -> {

                        deepCrawl(link.attr("abs:href"),
                                keyWord,
                                depth - 1,
                                pageLimit,
                                updatedProcessedUrls);
                    });

                });
            }
        }
    }

    @Override
    public Optional<PageInfo> crawl(String url, String keyWords) {

        log.info("Start fetching data on url: {}", url);

        try {

            final Document document = Jsoup.connect(url).timeout(1000).get();

            pageCount++;

            log.info("url: {}", url);
            String title = document.title();
            log.info("title: {}", title);

            // Count number of occurrences of each keyword on the page
            Map<String, Long> keyWordStats = new HashMap<>();
            Arrays.stream(keyWords.split(", ")).forEach(keyWord -> {

                long count = Pattern.compile(keyWord.toLowerCase())
                        .matcher(document.body().toString().toLowerCase()).results().count();

                log.info("keyWord: {} hits: {}", keyWord, count);
                keyWordStats.put(keyWord, count);
            });

            // Get all links
            final Elements links = document.select("a[href]");
            log.debug("Fetched title: {}, links[{}] for url: {}", title, links.nextAll(), url);

            return Optional.of(new PageInfo(url.length() > 255 ? url.substring(0, 255) : url, title, links, keyWordStats));

        } catch(SocketTimeoutException e){
            log.info("{}", e.getMessage());
            return Optional.empty();
        }
        catch (IOException e) {
            log.info(String.format("Error getting contents of url:%s", url));
            return Optional.empty();
        }
    }
}
