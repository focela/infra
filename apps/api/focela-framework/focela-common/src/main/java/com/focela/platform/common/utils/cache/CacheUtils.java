package com.focela.platform.common.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Cache utilities.
 */
public class CacheUtils {

    /**
     * Maximum cache size for the asynchronous-reloading LoadingCache.
     *
     * @see <a href="">Notes on the local CacheUtils utility</a>
     */
    private static final Integer CACHE_MAX_SIZE = 10000;

    /**
     * Build a LoadingCache that asynchronously reloads.
     *
     * Note: if your cache interacts with ThreadLocal, either propagate the ThreadLocal yourself
     * or use {@link #buildCache(Duration, CacheLoader)} instead.
     *
     * Rule of thumb:
     * 1. Per-user caches: use {@link #buildCache(Duration, CacheLoader)}.
     * 2. Global/system caches: use this method.
     *
     * @param duration expiration duration
     * @param loader  CacheLoader instance
     * @return LoadingCache instance
     */
    public static <K, V> LoadingCache<K, V> buildAsyncReloadingCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                // Only blocks the current loading thread; other threads return the stale value.
                .refreshAfterWrite(duration)
                // Use asyncReloading for fully asynchronous loading, including the thread blocked by refreshAfterWrite.
                .build(CacheLoader.asyncReloading(loader, Executors.newCachedThreadPool())); // TODO: consider making this configurable
    }

    /**
     * Build a LoadingCache that reloads synchronously.
     *
     * @param duration expiration duration
     * @param loader  CacheLoader instance
     * @return LoadingCache instance
     */
    public static <K, V> LoadingCache<K, V> buildCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                // Only blocks the current loading thread; other threads return the stale value.
                .refreshAfterWrite(duration)
                .build(loader);
    }

}
