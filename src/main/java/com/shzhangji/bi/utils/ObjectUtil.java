package com.shzhangji.bi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author huangxiaodi
 * @since 2021-04-01 16:56
 */
public class ObjectUtil {

    public static <T extends Object> T deepClone(Object target) {
        try {
            //字节流对象
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //开始转换该对象
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            //写到当前类，当然也可以写入文件
            oos.writeObject(target);
            //字节输出流
            ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
            //输出该对象
            ObjectInputStream ois = new ObjectInputStream(bais);
            //读出对象，实现新对象的生成
            return (T) ois.readObject();
        } catch (Exception e) {
            System.out.println("克隆出错" + e.getStackTrace());
            return null;
        }
    }
}
