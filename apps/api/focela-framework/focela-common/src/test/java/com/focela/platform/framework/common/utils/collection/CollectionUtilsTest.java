package com.focela.platform.framework.common.utils.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link CollectionUtils}
 */
public class CollectionUtilsTest {

    @Data
    @AllArgsConstructor
    private static class Dog {

        private Integer id;
        private String name;
        private String code;

    }

    @Test
    public void testDiffList() {
        // prepare parameters
        Collection<Dog> oldList = Arrays.asList(
                new Dog(1, "Huahua", "hh"),
                new Dog(2, "Wangcai", "wc")
        );
        Collection<Dog> newList = Arrays.asList(
                new Dog(null, "Huahua2", "hh"),
                new Dog(null, "Xiaobai", "xb")
        );
        BiFunction<Dog, Dog, Boolean> sameFunc = (oldObj, newObj) -> {
            boolean same = oldObj.getCode().equals(newObj.getCode());
            // if equal, set the id for later update
            if (same) {
                newObj.setId(oldObj.getId());
            }
            return same;
        };

        // invoke
        List<List<Dog>> result = CollectionUtils.diffList(oldList, newList, sameFunc);
        // assert
        assertEquals(result.size(), 3);
        // assert create
        assertEquals(result.get(0).size(), 1);
        assertEquals(result.get(0).get(0), new Dog(null, "Xiaobai", "xb"));
        // assert update
        assertEquals(result.get(1).size(), 1);
        assertEquals(result.get(1).get(0), new Dog(1, "Huahua2", "hh"));
        // assert delete
        assertEquals(result.get(2).size(), 1);
        assertEquals(result.get(2).get(0), new Dog(2, "Wangcai", "wc"));
    }

}
