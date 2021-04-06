package com.shzhangji.bi.tasks;

import com.shzhangji.bi.Source;
import com.shzhangji.bi.bean.Production;
import com.shzhangji.bi.bean.Task;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @author huangxiaodi
 * @since 2021-03-29 17:11
 */
public class JdbcSourceTask extends Task implements Source, Callable<Object> {

    private Integer dataSourceId;

    private String sql;

    public JdbcSourceTask(Integer dataSourceId, String sql) {
        this.dataSourceId = dataSourceId;
        this.sql = sql;
    }

    @Override
    public Production read() {
        // 查询数据
        Production production = new Production();

        // TODO 通过dataSourceId和Sql 连接数据库查询数据
        Random random = new Random();
        // 构造测试数据
        for (int i = 0; i < 10000000; i++) {
            StringBuilder lineData = new StringBuilder();
            // 年级
            lineData.append(i % 10 + 1);
            lineData.append("&&");
            // 性别
            lineData.append(random.nextInt(2) + 1);
            lineData.append("&&");
            // 成绩
            lineData.append(random.nextInt(10) + 18);

            //System.out.println(this.getTaskKey() + "产生数据：" + lineData);

            production.getRecords().add(lineData.toString());
        }

        System.out.println("生成数据结束：" + new Date());
        /*production.addElement("1&&2&&22");
        production.addElement("2&&2&&23");
        production.addElement("3&&2&&18");
        production.addElement("4&&1&&23");
        production.addElement("1&&1&&19");
        production.addElement("2&&1&&23");
        production.addElement("3&&1&&27");
        production.addElement("4&&1&&24");
        production.addElement("1&&1&&24");
        production.addElement("2&&2&&24");
        production.addElement("3&&2&&22");
        production.addElement("4&&1&&22");
        production.addElement("1&&1&&27");
        production.addElement("2&&1&&24");
        production.addElement("3&&1&&27");
        production.addElement("4&&1&&20");
        production.addElement("1&&2&&25");
        production.addElement("2&&1&&20");
        production.addElement("3&&2&&24");
        production.addElement("4&&2&&24");
        production.addElement("1&&1&&18");
        production.addElement("2&&2&&22");
        production.addElement("3&&2&&24");
        production.addElement("4&&1&&23");
        production.addElement("1&&1&&21");*/


        return production;
    }

    @Override
    public Production call() throws Exception {
        return read();
    }
}
