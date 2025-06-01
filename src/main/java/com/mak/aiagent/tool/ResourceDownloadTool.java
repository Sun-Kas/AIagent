package com.mak.aiagent.tool;

import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool
{
    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Path of the file to save the downloaded resource") String fileName) {
        try {
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(fileName));
            return "Resource downloaded successfully to: " + fileName;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}
