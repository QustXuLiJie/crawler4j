package edu.uci.ics.crawler4j.examples;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.model.Job;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
                && href.startsWith("https://www.lagou.com");
//        return  true;http[s]://www.lagou.com/(\d+).    htmlhttp[s]://www.lagou.com/.*
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

//        logger.debug("Docid: {}", docid);
//        logger.info("URL: {}", url);
//        logger.debug("Domain: '{}'", domain);
//        logger.debug("Sub-domain: '{}'", subDomain);
//        logger.debug("Path: '{}'", path);
//        logger.debug("Parent page: {}", parentUrl);
//        logger.debug("Anchor text: {}", anchor);


//         String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
// && url.matches("https://www.lagou.com/(\\d+).html")
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            String title = htmlParseData.getTitle();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            Document doc = Jsoup.parse(html);
            // 获取工作名称
            Elements jobNameElements = doc.getElementsByClass("job-name");
            String jobName = jobNameElements.attr("title");
            // 获取公司名
            Elements companyElements = doc.getElementsByClass("company");
            String company = companyElements.text();
            //获取工资、地点、经验要求、学历要求和工作类型
            Elements salaryElements = doc.getElementsByClass("job_request");
            String salary = salaryElements.text();
            String[] list = salary.split("/");// 15k-30k /北京 / 经验不限 / 学历不限 / 全职
            Elements businessElements = doc.getElementsByClass("c_feature");
            String business = businessElements.text();
            String[] busList = business.split(" ");
            System.out.println("--------------------------");
            if (list != null && list.length > 4 && company != null && jobName != null && busList != null && busList.length > 4) {
                Job job = new Job();
                job.setSalary(list[0]);
                job.setPlace(list[1]);
                job.setEducational(list[3]);
                job.setExperience(list[2]);
                job.setCompany(company);
                job.setJobname(jobName);
                job.setBusiness(busList[0]);
                job.setStage(busList[2]);
                System.out.println(job.toString());
            }
            System.out.println("--------------------------");
//             System.out.println(text);
            System.out.println("Title is:" + title);
//            System.out.println("Text length: " + text.length());
//            System.out.println("Html length: " + html.length());
//            System.out.println("Number of outgoing links: " + links.size());
        }
    }

    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(8);
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
//        controller.addSeed("https://www.lagou.com");
        controller.addSeed("https://www.lagou.com/zhaopin/");
//         controller.addSeed("http://www.ics.uci.edu/");

         /*
          * Start the crawl. This is a blocking operation, meaning that your code
          * will reach the line after this only when crawling is finished.
          */
        controller.start(MyCrawler.class, numberOfCrawlers);
    }

}