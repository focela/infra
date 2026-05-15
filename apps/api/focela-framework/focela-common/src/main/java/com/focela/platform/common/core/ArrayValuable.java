package com.focela.platform.common.core;

/**
 * Interface for types that can produce a T array.
 */
public interface ArrayValuable<T> {

    /**
     * @return the array
     */
    T[] array();

}
