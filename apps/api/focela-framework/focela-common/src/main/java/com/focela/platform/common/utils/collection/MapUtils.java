package com.focela.platform.common.utils.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.focela.platform.common.core.KeyValue;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Map utilities.
 */
public class MapUtils {

    /**
     * Get all values from a multimap for the given keys.
     *
     * @param multimap multimap
     * @param keys keys
     * @return value array
     */
    public static <K, V> List<V> getList(Multimap<K, V> multimap, Collection<K> keys) {
        List<V> result = new ArrayList<>();
        keys.forEach(k -> {
            Collection<V> values = multimap.get(k);
            if (CollectionUtil.isEmpty(values)) {
                return;
            }
            result.addAll(values);
        });
        return result;
    }

    /**
     * Look up a value by key in the map and pass it to the consumer for further processing.
     * No-op when the key is null. When the resolved value is null, no processing is performed.
     *
     * @param map the map
     * @param key key
     * @param consumer further processing logic
     */
    public static <K, V> void findAndThen(Map<K, V> map, K key, Consumer<V> consumer) {
        if (ObjUtil.isNull(key) || CollUtil.isEmpty(map)) {
            return;
        }
        V value = map.get(key);
        if (value == null) {
            return;
        }
        consumer.accept(value);
    }

    public static <K, V> Map<K, V> convertMap(List<KeyValue<K, V>> keyValues) {
        Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(keyValues.size());
        keyValues.forEach(keyValue -> map.put(keyValue.getKey(), keyValue.getValue()));
        return map;
    }

    /**
     * Get a BigDecimal value from a Map.
     *
     * @param map Map data source
     * @param key key name
     * @return BigDecimal value; returns null when parsing fails or the value is null
     */
    public static BigDecimal getBigDecimal(Map<String, ?> map, String key) {
        return getBigDecimal(map, key, null);
    }

    /**
     * Get a BigDecimal value from a Map.
     *
     * @param map          Map data source
     * @param key          key name
     * @param defaultValue default value
     * @return BigDecimal value; returns the default value when parsing fails or the value is null
     */
    public static BigDecimal getBigDecimal(Map<String, ?> map, String key, BigDecimal defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
