# Web Crawler REST API

This program is a web crawler to go up to provided depth and up ot maximum pages. Crawling service is exposed as REST endpoints. Web UI is available to complete web crawler request form, start web crawler and get statistics as file in CSV format.

Once application is started web UI is available on: http://localhost:8080/main or http://localhost:8080/

The API caller is able to use these operations on API:
- Start web crawler - POST - /api/webcrawler
- Get all records JSON format - GET - /api/webcrawler
- Get n records JSON format - GET - /api/webcrawler/{count}
- Get all records CSV format - GET - /api/webcrawler/csv
- Get n sorted records CSV format - GET - /api/webcrawler/csv/{count}

Link to [Postman test data samples](https://www.postman.com/avionics-physicist-21440496/workspace/web-crawler-api/collection/18662089-238ceeff-f3a5-47ee-97a5-f4e83ef7728f)

## Installation

Download code as ZIP or ```git pull https://github.com/hmurij/Web-Crawler.git``` 
Import existing Maven project and run com.webcrawler.WebCrawlerApplication.java or start application by executing startCrawler.bat, please note webCrawler.jar should be in the same directory as bat file.



