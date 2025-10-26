package com.kfc.test.large.txt.parse;

import com.kfc.test.large.txt.exception.ParseException;

import java.util.ArrayList;
import java.util.List;


public class FixedLengthParser implements LineParser {
    private final List<Integer> fieldLengths;
    private final int totalLength;

    public FixedLengthParser(List<Integer> fieldLengths) {
        this.fieldLengths = new ArrayList<>(fieldLengths);
        this.totalLength = fieldLengths.stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public List<String> parse(String line) throws ParseException {
        if (line == null) {
            throw new ParseException("行内容为null");
        }
        if (line.length() != totalLength) {
            throw new ParseException(String.format(
                    "行长度不匹配（预期：%d，实际：%d），内容：%s",
                    totalLength, line.length(), line
            ));
        }

        List<String> fields = new ArrayList<>(fieldLengths.size());
        int start = 0;
        for (int length : fieldLengths) {
            int end = Math.min(start + length, line.length());
            fields.add(line.substring(start, end).trim());
            start = end;
        }
        return fields;
    }
}
