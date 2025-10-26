package com.kfc.test.large.txt.parse;


import com.kfc.test.large.txt.exception.ParseException;
import com.kfc.test.large.txt.exception.ProcessException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class AnnotatedFileProcessor<HEADER, BODY> {
    private final String filePath;
    private final Class<BODY> bodyClass;
    private final AnnotationParser.ParseMetadataPair<HEADER, BODY> metadataPair;
    private final boolean hasHeader;

    public AnnotatedFileProcessor(String filePath, Class<BODY> bodyClass) {
        this.filePath = filePath;
        this.bodyClass = bodyClass;
        this.metadataPair = AnnotationParser.parse(bodyClass);
        this.hasHeader = metadataPair.getHeaderMetadata() != null;
    }

    public void process(HeaderProcessor<HEADER> headerProcessor, BodyProcessor<HEADER, BODY> bodyProcessor)
            throws IOException, ParseException, InstantiationException, IllegalAccessException, ProcessException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), getBodyCharset())
        )) {
            String line;
            long lineNumber = 0;

            HEADER header = null;
            if (hasHeader) {
                line = reader.readLine();
                lineNumber++;
                if (line == null) {
                    throw new ParseException("文件为空，但配置了文件头类");
                }
                header = parseHeader(line, lineNumber);
                if (headerProcessor != null) {
                    headerProcessor.process(header, lineNumber);
                }
            }

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    BODY body = parseBody(line);
                    if (bodyProcessor != null) {
                        bodyProcessor.process(body, lineNumber, header);
                    }
                } catch (Exception e) {
                    throw new ParseException("文件体第" + lineNumber + "行处理失败：" + e.getMessage(), e);
                }
            }
        }
    }

    private HEADER parseHeader(String headerLine, long lineNumber) throws ParseException, InstantiationException, IllegalAccessException {
        AnnotationParser.ParseMetadata<HEADER> headerMetadata = metadataPair.getHeaderMetadata();
        Class<HEADER> headerClass = headerMetadata.getEntityClass();
        HEADER header = headerClass.newInstance();

        try (BufferedReader headerReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(headerLine.getBytes()), headerMetadata.getCharset())
        )) {
            String line = headerReader.readLine();
            List<String> fields = headerMetadata.getLineParser().parse(line);
            headerMetadata.getFieldMapper().map(fields, header);
            return header;
        } catch (IOException e) {
            throw new ParseException("文件头解析IO错误", e);
        }
    }

    private BODY parseBody(String line) throws ParseException, InstantiationException, IllegalAccessException {
        AnnotationParser.ParseMetadata<BODY> bodyMetadata = metadataPair.getBodyMetadata();
        BODY body = bodyClass.newInstance();
        List<String> fields = bodyMetadata.getLineParser().parse(line);
        bodyMetadata.getFieldMapper().map(fields, body);
        return body;
    }

    private Charset getBodyCharset() {
        return metadataPair.getBodyMetadata().getCharset();
    }

    @FunctionalInterface
    public interface HeaderProcessor<HEADER> {
        void process(HEADER header, long lineNumber) throws ProcessException;
    }

    @FunctionalInterface
    public interface BodyProcessor<HEADER, BODY> {
        void process(BODY body, long lineNumber, HEADER header) throws ProcessException;
    }
}