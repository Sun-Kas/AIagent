package com.mak.aiagent.tool;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.*;
import java.util.regex.*;


@Slf4j
public class WebSearchTool {
    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "http://localhost:8083/search";


    @Tool(description = "Search for information from Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        log.info("Search query: {}", query);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        String html = HttpUtil.createGet(SEARCH_API_URL)
                .form(paramMap)
                .execute()
                .body();;
//        // 1. 找到第一个 <article 开始位置
//        int start = html.indexOf("<article");
//        if (start == -1) {
//            throw new IllegalArgumentException("No <article> tag found");
//        }
//
//        // 2. 找到最后一个 </article> 结束位置
//        int end = html.lastIndexOf("</article>");
//        if (end == -1) {
//            throw new IllegalArgumentException("No </article> tag found");
//        }
//        // +10 是 </article> 长度，确保包含结束标签
//        end += "</article>".length();
//
//        // 3. 截取纯 article 部分
//        String articlesHtml = html.substring(start, end);

        // 1. 仅截取 article 内容块
        StringBuilder articleHtml = new StringBuilder();
        Pattern pattern = Pattern.compile("<article[^>]*class=\"result result-default category-general\"[^>]*>.*?</article>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            articleHtml.append(matcher.group());
        }

        try {
            Document doc = Jsoup.parse(articleHtml.toString());
            Elements articles = doc.select("article.result");

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode resultArray = mapper.createArrayNode();

            for (Element article : articles) {
                Element titleAnchor = article.selectFirst("h3 > a");
                Element contentPara = article.selectFirst("p.content");

                if (titleAnchor != null && contentPara != null) {
                    String title = titleAnchor.text().replaceAll("\\s+", " ").trim();
                    String href = titleAnchor.attr("href").trim();
                    String content = contentPara.text().replaceAll("\\s+", " ").trim();

                    ObjectNode articleJson = mapper.createObjectNode();
                    articleJson.put("title", title);
                    articleJson.put("url", href);
                    articleJson.put("content", content);

                    resultArray.add(articleJson);
                }
            }

            log.info("Search result: {}", resultArray);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to parse HTML.\"}";
        }
    }
}
