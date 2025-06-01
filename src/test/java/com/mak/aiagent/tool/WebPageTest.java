package com.mak.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebPageTest {
    @Test
    public void testWebPage() {
        WebPageTool webPageTool = new WebPageTool();
        String url = "https://www.codefather.cn/course/1915010091721236482/section/1920794055716278274?contentType=text&tabKey=list#heading-16";
        String result = webPageTool.scrapeWebPage(url);
        System.out.println(result);
    }
}
