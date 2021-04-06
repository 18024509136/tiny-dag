package com.shzhangji.bi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangxiaodi
 * @since 2021-03-30 16:15
 */
public class KeyValueBuilder {

    private static final Pattern oneKeyPattern = Pattern.compile("^(.*?)&&(.*?)$");

    private static final Pattern towKeyPattern = Pattern.compile("^(.*?&&.*?)&&(.*?)$");

    private static final Pattern threeKeyPattern = Pattern.compile("^(.*?&&.*?&&.*?)&&(.*?)$");

    private static final Pattern fourKeyPattern = Pattern.compile("^(.*?&&.*?&&.*?&&.*?)&&(.*?)$");

    private static final Pattern fiveKeyPattern = Pattern.compile("^(.*?&&.*?&&.*?&&.*?&&.*?)&&(.*?)$");

    private static final Pattern sixKeyPattern = Pattern.compile("^(.*?&&.*?&&.*?&&.*?&&.*?&&.*?)&&(.*?)$");

    private static Map<String, Pattern> patternMap;

    private static final ThreadLocal<Pattern> patterLocal = new ThreadLocal<>();

    static {
        patternMap = new HashMap<>(6);
        patternMap.put("1", oneKeyPattern);
        patternMap.put("2", towKeyPattern);
        patternMap.put("3", threeKeyPattern);
        patternMap.put("4", fourKeyPattern);
        patternMap.put("5", fiveKeyPattern);
        patternMap.put("6", sixKeyPattern);
    }

    private KeyValueBuilder() {

    }

    public static KeyValue build(String lineData, int keyNum) {
        Pattern pattern = patterLocal.get();
        if (pattern == null) {
            String patternKey = String.valueOf(keyNum);
            pattern = patternMap.get(patternKey);
            patterLocal.set(pattern);
        }

        Matcher matcher = pattern.matcher(lineData);

        String key = null;
        String value = null;
        while (matcher.find()) {
            key = matcher.group(1);
            value = matcher.group(2);
        }

        return new KeyValue(key, value);
    }

    public static class KeyValue {

        private String key;

        private String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
