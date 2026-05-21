package com.zsm.aiagentlasted.tools;

import cn.hutool.core.io.FileUtil;
import com.zsm.aiagentlasted.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 文件操作工具
 *
 * @author zsm
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file. " +
            "Use this tool when the user asks to read, view, or retrieve content from an existing file.")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_DIR + File.separator + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file" + e.getMessage();
        }
    }

    @Tool(description = "Write or save content to a file. " +
            "Use this tool when the user explicitly asks to save, write, or store content to a file, " +
            "create a document, or persist any information locally. " +
            "Examples: saving user profiles, storing reports, creating text files, etc.")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + File.separator + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file" + e.getMessage();
        }
    }

}
