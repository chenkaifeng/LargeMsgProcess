package com.kfc.test.large.txt.parse;


import com.kfc.test.large.txt.exception.ParseException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class DelimiterParser implements LineParser {
    private final Pattern delimiterPattern;

    public DelimiterParser(String delimiter) {
        this.delimiterPattern = Pattern.compile(Pattern.quote(delimiter));
    }

    @Override
    public List<String> parse(String line) throws ParseException {
        if (line == null) {
            return null;
        }
        String[] parts = delimiterPattern.split(line, -1);
        return Arrays.asList(parts);
    }
}