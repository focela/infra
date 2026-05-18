package com.focela.platform.common.utils.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.common.model.PageResult;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.convert.Convert.toCollection;
import static java.util.Arrays.asList;

/**
 * Collection utilities.
 */
public class CollectionUtils {

    public static boolean containsAny(Object source, Object... targets) {
        return asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtil::isEmpty);
    }

    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return from.stream().anyMatch(predicate);
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return convertList(Arrays.asList(from), func);
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> PageResult<U> convertPage(PageResult<T> from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return new PageResult<>(from.getTotal());
        }
        return new PageResult<>(convertList(from.getList(), func), from.getTotal());
    }

    public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
                                                      Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from,
                                                         Function<? super T, ? extends U> mapper,
                                                         Function<U, ? extends Stream<? extends R>> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        return map.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static <T> Set<T> convertSet(Collection<T> from) {
        return convertSet(from, v -> v);
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().filter(filter).collect(Collectors.toMap(keyFunc, v -> v));
    }

    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from,
                                                    Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from,
                                                       Function<? super T, ? extends U> mapper,
                                                       Function<U, ? extends Stream<? extends R>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream()
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    // No better name yet; suffixed with 2 for now.
    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }

    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<K, T> builder = ImmutableMap.builder();
        from.forEach(item -> builder.put(keyFunc.apply(item), item));
        return builder.build();
    }

    /**
     * Compare old and new lists to determine which items were created, updated, or deleted.
     *
     * @param oldList  old list
     * @param newList  new list
     * @param sameFunc comparator function; returns true if the two items represent the same logical entity.
     *                 Note: "same" is determined by the item's identifier, not by full equality.
     * @return [create list, update list, delete list]
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList,
                                             BiFunction<T, T, Boolean> sameFunc) {
        List<T> createList = new LinkedList<>(newList); // Initially assume everything is new; remove as we find matches.
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        // Iterate over oldList to find updateList and deleteList.
        for (T oldObj : oldList) {
            // 1. Look for a match.
            T foundObj = null;
            for (Iterator<T> iterator = createList.iterator(); iterator.hasNext(); ) {
                T newObj = iterator.next();
                // 1.1 No match; skip.
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                // 1.2 Match; remove and stop searching.
                iterator.remove();
                foundObj = newObj;
                break;
            }
            // 2. Matched -> updateList; otherwise -> deleteList.
            if (foundObj != null) {
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return asList(createList, updateList, deleteList);
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    public static <T> T getFirst(List<T> from) {
        return !CollectionUtil.isEmpty(from) ? from.get(0) : null;
    }

    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        return findFirst(from, predicate, Function.identity());
    }

    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().map(func).orElse(null);
    }

    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert !from.isEmpty(); // Assertion to silence warning.
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // Assertion to silence warning.
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> T getMinObject(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // Assertion to silence warning.
        return from.stream().min(Comparator.comparing(valueFunc)).get();
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc,
                                                                     BinaryOperator<V> accumulator) {
        return getSumValue(from, valueFunc, accumulator, null);
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc,
                                                                     BinaryOperator<V> accumulator, V defaultValue) {
        if (CollUtil.isEmpty(from)) {
            return defaultValue;
        }
        assert !from.isEmpty(); // Assertion to silence warning.
        return from.stream().map(valueFunc).filter(Objects::nonNull).reduce(accumulator).orElse(defaultValue);
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    public static <T> Collection<T> singleton(T obj) {
        return obj == null ? Collections.emptyList() : Collections.singleton(obj);
    }

    public static <T> List<T> newArrayList(List<List<T>> list) {
        return list.stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Convert to LinkedHashSet.
     *
     * @param <T>         element type
     * @param elementType element class
     * @param value       value to convert
     * @return {@link LinkedHashSet}
     */
    @SuppressWarnings("unchecked")
    public static <T> LinkedHashSet<T> toLinkedHashSet(Class<T> elementType, Object value) {
        return (LinkedHashSet<T>) toCollection(LinkedHashSet.class, elementType, value);
    }

    public static boolean dfs(Long node, Map<Long, Set<Long>> graph) {
        return dfs(node, graph, new HashSet<>(), new HashSet<>());
    }

    private static boolean dfs(Long node, Map<Long, Set<Long>> graph, Set<Long> visited, Set<Long> inStack) {
        if (inStack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }
        visited.add(node);
        inStack.add(node);
        Set<Long> neighbors = graph.getOrDefault(node, Collections.emptySet());
        for (Long neighbor : neighbors) {
            if (dfs(neighbor, graph, visited, inStack)) {
                return true;
            }
        }
        inStack.remove(node);
        return false;
    }

}
