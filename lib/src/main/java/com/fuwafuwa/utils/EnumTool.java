package com.fuwafuwa.utils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumTool {

    /**
     * 根据条件获取枚举对象
     *
     * @param className 枚举类
     * @param predicate 筛选条件
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getEnum(Class<T> className, Predicate<T> predicate) {
        Map<Class, Object> map = new ConcurrentHashMap<>();
        if (!className.isEnum()) {
//            logger.info("Class 不是枚举类");
            return null;
        }
        Object obj = map.get(className);
        T[] ts = null;
        if (obj == null) {
            ts = className.getEnumConstants();
            map.put(className, ts);
        } else {
            ts = (T[]) obj;
        }
        return Stream.of(ts).filter(predicate).findFirst();
    }
}
