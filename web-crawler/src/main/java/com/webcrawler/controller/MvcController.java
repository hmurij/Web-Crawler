package com.webcrawler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcrawler.dao.PageInfoRepository;
import com.webcrawler.model.CrawlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MvcController {

    @Autowired
    PageInfoRepository pageInfoRepository;

    private static Logger log = LoggerFactory.getLogger(RestController.class);

    /**
     * Handles initial GET request, adds empty crawl request object as attribute to model.
     * @param model holder for model attributes.
     * @return Thymeleaf template name
     */
    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("crawlRequest", new CrawlRequest());
        return "main";
    }

    /**
     * Handles POST request to initiate web crawler service. Once web crawl request completed
     * all statistics are collected from repository and total number of hits per each keyword
     * added as attribute to model as map in keyword total hits pairs.
     * @param crawlRequest object holding web crawler request parameters,
     *                     such as: url, coma delimited keywords, depth and page limit.
     *
     * @param model holder for model attributes
     * @return Thymeleaf template name
     * @throws JsonProcessingException
     */
    @PostMapping("/start")
    public String startCrawl(@ModelAttribute("crawlRequest") CrawlRequest crawlRequest, Model model) throws JsonProcessingException {
        log.info("Start crawl on url: {}", crawlRequest.toString());

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/webcrawler";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(new ObjectMapper().writeValueAsString(crawlRequest), headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(url, request, String.class);


        var allStats = pageInfoRepository.findAll();

        var stats = new HashMap<>( allStats.isEmpty() ? Map.of(): allStats.get(0).getKeyWordStats());
        allStats.forEach(as -> {
                    for (var entry : as.getKeyWordStats().entrySet()) {
                        stats.replace(entry.getKey(), stats.get(entry.getKey()) + entry.getValue());
                    }
                }
        );


        model.addAttribute("seed", crawlRequest);
        model.addAttribute("stats", stats);

        return "info";
    }

    /**
     * Handles GET request to get web crawl service report as file in CSV format.
     * @return web crawl service report as file in CSV format.
     */
    @GetMapping("/csvFile")
    public HttpEntity<byte[]> getCsvFile() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/webcrawler/csv";

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url,
                byte[].class);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("text/csv"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=report_" + currentDateTime + ".csv");

        return new HttpEntity<byte[]>(response.getBody(), header);
    }

    /**
     * Handles GET request to get top 10 pages of web crawl service report as file in CSV format,
     * sorted in descending order.
     * @return web crawl service report of top 10 pages as file in CSV format.
     */
    @GetMapping("/csvFileTop10")
    public HttpEntity<byte[]> getCsvFileTop10() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/webcrawler/csv/10";

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url,
                byte[].class);

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("text/csv"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=reportTop10_" + currentDateTime + ".csv");

        return new HttpEntity<byte[]>(response.getBody(), header);
    }



}
