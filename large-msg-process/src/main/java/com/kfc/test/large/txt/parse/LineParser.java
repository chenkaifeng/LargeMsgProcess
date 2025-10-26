package com.kfc.test.large.txt.parse;

import com.kfc.test.large.txt.exception.ParseException;

import java.util.List;

/**
 * 行解析器接口：定义不同解析方式的统一行为
 */
@FunctionalInterface
public interface LineParser {
    List<String> parse(String line) throws ParseException;
}
