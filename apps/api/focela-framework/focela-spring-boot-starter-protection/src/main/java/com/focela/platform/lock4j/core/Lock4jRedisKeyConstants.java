package com.focela.platform.lock4j.core;

/**
 * Lock4j Redis key constants.
 */
public interface Lock4jRedisKeyConstants {

    /**
     * Distributed lock.
     *
     * KEY format: lock4j:%s // parameter is produced by DefaultLockKeyBuilder
     * VALUE format: HASH // RLock.class: Redisson lock backed by a Hash structure
     * Expiration: variable
     */
    String LOCK4J = "lock4j:%s";

}
