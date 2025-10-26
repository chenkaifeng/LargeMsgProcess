package com.kfc.test.large.txt.domain;


import com.kfc.test.large.txt.exception.ProcessException;
import com.kfc.test.large.txt.parse.AnnotatedFileProcessor;

import java.io.IOException;

public class MixedFileExample {
    public static void main(String[] args) {
        try {
            AnnotatedFileProcessor<FileHeader, Product> processor = new AnnotatedFileProcessor<>(
                    "mixed_file.txt", Product.class
            );

            // 处理文件头
            AnnotatedFileProcessor.HeaderProcessor<FileHeader> headerProcessor = (header, lineNum) -> {
                System.out.printf("文件头（第%d行）：%s%n", lineNum, header);
                if (header.getRecordCount() != 3) {
                    throw new ProcessException("记录数与文件头不符");
                }
            };

            // 处理文件体
            AnnotatedFileProcessor.BodyProcessor<FileHeader, Product> bodyProcessor = (product, lineNum, header) -> {
                System.out.printf("文件体（第%d行）：%s，关联版本：%s%n",
                        lineNum, product, header.getVersion());
            };

            processor.process(headerProcessor, bodyProcessor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}