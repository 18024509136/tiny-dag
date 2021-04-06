package com.shzhangji.bi.transforms;

import com.shzhangji.bi.bean.Production;

import java.util.*;
import java.util.function.Function;

/**
 * @author huangxiaodi
 * @since 2021-03-30 11:01
 */
public class AvgFunction implements Function<Production, Production> {
    @Override
    public Production apply(Production production) {
        System.out.println("算子开始：" + new Date());

        if (production == null) {
            return new Production();
        }
        List<String> records = production.getRecords();

        Map<String, Integer> aggMap = new HashMap(10);
        records.stream().forEach(dataLine -> {
            String[] parts = dataLine.split("\\|\\|");
            String key = parts[0];
            String content = parts[1];

            int value = Integer.parseInt(content);
            Integer aggValue = aggMap.get(key);
            if (aggValue == null) {
                aggMap.put(key, value);
            } else {
                aggMap.put(key, value + aggValue);
            }
        });

        List<String> resutls = new ArrayList<>();
        aggMap.forEach((k, v) -> {
            StringBuilder lineDataBuilder = new StringBuilder();
            lineDataBuilder.append(k);
            lineDataBuilder.append("&&");
            lineDataBuilder.append(v);

            resutls.add(lineDataBuilder.toString());
        });

        System.out.println("算子结束：" + new Date());

        return new Production(resutls);
    }
}
