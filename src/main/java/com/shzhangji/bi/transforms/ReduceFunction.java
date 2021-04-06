package com.shzhangji.bi.transforms;

import com.shzhangji.bi.bean.Production;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author huangxiaodi
 * @since 2021-03-31 16:20
 */
public class ReduceFunction implements Function<Production, Production> {
    @Override
    public Production apply(Production production) {
        if (production == null) {
            return new Production();
        }

        List<String> records = production.getRecords();

        List<String> results = new ArrayList<>();
        records.stream().forEach(dataLine -> {
            String[] parts = dataLine.split("\\|\\|");
            String key = parts[0];
            String content = parts[1];

            String result = new StringBuilder(key).append("&&").append(content).toString();
            results.add(result);
        });

        return new Production(results);
    }
}
