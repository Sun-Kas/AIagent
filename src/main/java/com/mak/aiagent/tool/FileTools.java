package com.mak.aiagent.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileTools {
    private final String FILEPATH = System.getProperty("user.dir")+"/tmp";

    @Tool(description = "Read from a file")
    public String readFile(@ToolParam(description = "The file name") String fileName) {
        String path= FILEPATH + "/" + fileName;
        try {
            return FileUtil.readUtf8String(path);
        } catch (IORuntimeException e) {
            return  "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "Write into a file")
    public String writeFile(@ToolParam(description = "The file name") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String path= FILEPATH + "/" + fileName;
        try {
            FileUtil.writeUtf8String(content, path);
            return "File written successfully.";
        } catch (IORuntimeException e) {
            return  "Error writing file: " + e.getMessage();
        }
    }

    @Tool(description = "add content into a file")
    public String appendFile(@ToolParam(description = "The file name") String fileName,
                            @ToolParam(description = "Content to add to the file") String content) {
        String path= FILEPATH + "/" + fileName;
        try {
            FileUtil.appendUtf8String(content, path);
            return "File written successfully.";
        } catch (IORuntimeException e) {
            return  "Error writing file: " + e.getMessage();
        }
    }
}
