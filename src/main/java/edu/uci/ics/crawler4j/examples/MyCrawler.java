package edu.uci.ics.crawler4j.examples;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith("https://www.lagou.com/");
//        return  true;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {

        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();
//        page.getWebURL().getTag()

        System.out.println("********************************");
//
//         System.out.println("Docid: {}" + docid);
//         System.out.println("URL: {}"+ url);
//         System.out.println("Domain: '{}'"+ domain);
//          System.out.println("Sub-domain: '{}'"+ subDomain);
//           System.out.println("Path: '{}'"+ path);
//           System.out.println("Parent page: {}"+ parentUrl);
//         System.out.println("Anchor text: {}"+ anchor);

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);


//         String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            String title = htmlParseData.getTitle();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("--------------------------");
//             System.out.println(text);
            System.out.println("--------------------------");
            System.out.println("Title is:" + title);
            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
    }

    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

         /*
          * Instantiate the controller for this crawl.
          */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

         /*
          * For each crawl, you need to add some seed urls. These are the first
          * URLs that are fetched and then the crawler starts following links
          * which are found in these pages
          */
        controller.addSeed("https://www.lagou.com/zhaopin/Java/");
//         controller.addSeed("http://www.ics.uci.edu/~welling/");
//         controller.addSeed("http://www.ics.uci.edu/");

         /*
          * Start the crawl. This is a blocking operation, meaning that your code
          * will reach the line after this only when crawling is finished.
          */
        controller.start(MyCrawler.class, numberOfCrawlers);
    }

}