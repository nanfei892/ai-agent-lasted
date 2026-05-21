package com.zsm.aiagentlasted.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取工具
 */
public class WebScrapingTool {

    @Tool(description = "Scrape and extract content from a specific web page URL. " +
            "Use this tool when the user mentions a specific website URL and wants to " +
            "analyze its content, extract information, or read articles from that site. " +
            "Examples: analyzing content from a specific blog, reading articles from a news site, " +
            "extracting information from a forum or community website.")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
