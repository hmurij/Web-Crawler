package com.webcrawler.controller;

import com.webcrawler.dao.PageInfoRepository;
import com.webcrawler.model.CrawlRequest;
import com.webcrawler.model.PageInfo;
import com.webcrawler.service.WebCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {

    @Value("${webcrawler.maxdepth}")
    private String maxDepth;

    @Value("${webcrawler.pagelimit}")
    private String maxPageLimit;

    private static Logger log = LoggerFactory.getLogger(RestController.class);

    @Autowired
    WebCrawlerService webCrawlerService;

    @Autowired
    PageInfoRepository pageInfoRepository;

    /**
     * Handles POST request to initiate web crawling on particular page at provided depth
     * and at provided visited pages. If provided depth or page limit greater than default max values,
     * max values are used for web crawling.
     * @param crawlRequest POJO with main parameters required to start web crawler
     * @return string message confirming web crawling request completed
     */
    @PostMapping("/webcrawler")
    public String start(@RequestBody CrawlRequest crawlRequest) {

        log.info("Request for web crawling received on url: {} scraping data for keyword: {}",
                crawlRequest.getUrl(), crawlRequest.getKeyWord());

        int maxDepth = Integer.parseInt(this.maxDepth);
        log.info("maxDept: {}", maxDepth);
        final int newDepth = Integer.max(OptionalInt.of(crawlRequest.getDepth()).orElse(maxDepth),
                maxDepth);


        int maxPageLimit = Integer.parseInt(this.maxPageLimit);
        log.info("pageLimit {}", maxPageLimit);
        final int pageLimit = Integer.min(OptionalInt.of(crawlRequest.getPageLimit()).orElse(maxPageLimit),
                maxPageLimit);

        pageInfoRepository.deleteAll();

        webCrawlerService.deepCrawl(crawlRequest.getUrl(),
                crawlRequest.getKeyWord(),
                newDepth,
                pageLimit,
                null);


        log.info("Web crawl request completed starting on url: {}  scraping on keyword: {} ", crawlRequest.getUrl(), crawlRequest.getKeyWord());

        return "Web crawl request completed starting on url: " + crawlRequest.getUrl() + " scraping on keyword: " + crawlRequest.getKeyWord();
    }

    /**
     * Handles GET request to get web crawl report in CSV format.
     * @return web crawl report in CSV format
     */
    @GetMapping("/webcrawler/csv")
    public @ResponseBody
    byte[] getCsvFile() {

        return convertToCsv(pageInfoRepository.findAll());
    }

    /**
     * Handles GET request to get top n web crawl report pages in CSV format.
     * In case count is greater than size of complete report, complete report is returned.
     * @param count number of pages to be included into sorted report.
     * @return n top pages return in CSV format
     */
    @GetMapping("/webcrawler/csv/{count}")
    public @ResponseBody
    byte[] getCsvFile(@PathVariable("count") int count) {
        List<PageInfo> allStats = pageInfoRepository.findAll();

        allStats.sort(Comparator.reverseOrder());

        return convertToCsv(allStats.subList(0, Math.min(count, allStats.size())));
    }

    /**
     * Converts list of page information records into CSV format string object
     * and returns it as byte array.
     * @param allStats list containing complete statistic of web crawl.
     * @return byte array of web crawl report in CSV format.
     */
    private static byte[] convertToCsv(List<PageInfo> allStats) {
        StringBuilder bf = new StringBuilder();
        if (!allStats.isEmpty()) {

            String heading = "Url,Title," + allStats.get(0).getKeyWordStats().keySet().stream()
                    .map(k -> k.split(" ").length != 1 ? "\"" + k + "\"" : k)
                    .collect(Collectors.joining(",")) + "\n";

            bf.append(heading);

            allStats.stream().map(pageInfo -> convertPageInfoToCsv(pageInfo)).forEach(l -> bf.append(l));

        }

        return bf.toString().getBytes();

    }

    /**
     * Converts page information object into string in CSV format.
     * @param pageInfo web crawl statistic on one HTML page.
     * @return string representation of {@link PageInfo} object in CSV format.
     */
    private static String convertPageInfoToCsv(PageInfo pageInfo) {
        StringBuilder bf = new StringBuilder();

        String url = pageInfo.getUrl();
        bf.append(url.split(" ").length != 1 ? "\"" + url + "\"" : url).append(",");

        String title = pageInfo.getTitle();
        bf.append(title.split(" ").length != 1 ? "\"" + title + "\"" : title).append(",");

        bf.append(pageInfo.getKeyWordStats().values().stream()
                .map(h -> h.toString()).collect(Collectors.joining(","))).append("\n");

        return bf.toString();
    }

    /**
     * Handles GET request to get web crawl report in JSON format.
     * @return web crawl report in JSON format
     */
    @GetMapping("/webcrawler")
    public List<PageInfo> getAllStats() {

        return pageInfoRepository.findAll();
    }

    /**
     * Handles GET request to get top n web crawl report pages in JSON format.
     * In case count is greater than size of complete report, complete report is returned.
     * @param count number of pages to be included into report.
     * @return n top pages return in JSON format
     */
    @GetMapping("/webcrawler/{count}")
    public List<PageInfo> getAllStats(@PathVariable("count") int count) {
        List<PageInfo> allStats = pageInfoRepository.findAll();
        return allStats.subList(0, Math.min(count, allStats.size()));
    }

}
