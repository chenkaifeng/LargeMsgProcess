package com.kfc.test.large.utils;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author: Chenkf
 * @create: 2024/09/02
 **/
public class LargeTextFileBatchProcessor {

    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 256;

    /**
     * 逐块处理大文本文件
     *
     * @param filePath        文件路径
     * @param recordProcessor 记录处理器接口
     * @param delimiter       分隔符
     * @throws IOException 如果读取文件时发生错误
     */
    public static void processFile(String filePath, Charset charset, RecordProcessor recordProcessor, String delimiter) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, charset))) {
            // 可以根据需要调整缓冲区大小
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            StringBuilder currentChunk = new StringBuilder();

            while ((charsRead = reader.read(buffer)) != -1) {
                currentChunk.append(buffer, 0, charsRead);
                // 处理当前块中的所有明细
                String currentContent = currentChunk.toString();
                // 使用正则表达式分割字符串
                if (currentContent.contains(delimiter)) {
                    String[] records = currentContent.split(Pattern.quote(delimiter));
                    for (int i = 0; i < records.length - 1; i++) {
                        // 处理每个分割后的记录
                        recordProcessor.processRecord(records[i]);

                        // 更新当前块，移除已经处理的部分
                        currentChunk.delete(0, records[i].length());
                        currentChunk.delete(0, delimiter.length());
                    }
                }
            }
            // 处理最后一个块
            String currentContent = currentChunk.toString();
            if (!currentContent.isEmpty()) {
                String[] records = currentContent.split(delimiter);
                for (int i = 0; i < records.length; i++) {
                    recordProcessor.processRecord(records[i]);
                }
            }
        }
    }

    /**
     * 记录处理器接口
     */
    @FunctionalInterface
    public interface RecordProcessor {
        void processRecord(String record);
    }

    public static void main(String[] args) {
        String filePath = "F:\\my\\github-project\\LargeMsgProcess\\test\\details.txt"; // 大文本文件的路径
        String delimiter = ":LST:"; // 分隔符

        String resultFilePath = filePath + ".result";
        String charset = "GBK";
        Charset charset1 = Charset.forName(charset);

        File oriFile = new File(filePath);
        File resultFile = new File(resultFilePath);

        try {

            FileUtils.deleteQuietly(resultFile);
            AtomicInteger i = new AtomicInteger();
            processFile(filePath, charset1, record -> {
                // 在这里处理每条记录

                try {
                    if(i.get() > 0){
                        FileUtils.writeStringToFile(resultFile, delimiter, charset1, true);
                    }
                    FileUtils.writeStringToFile(resultFile, record, charset1, true);
                    i.getAndIncrement();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 例如，你可以在这里将数据写入数据库或其他处理
            }, delimiter);

            boolean areEqual = FileUtils.contentEquals(oriFile, resultFile);
            if (areEqual) {
                System.out.println("The files are identical.");
            } else {
                System.out.println("The files are not identical.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
