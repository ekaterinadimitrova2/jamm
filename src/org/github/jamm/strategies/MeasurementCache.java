package org.github.jamm.strategies;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.github.jamm.CachingStrategy;
import org.github.jamm.MemoryMeterStrategy;

/**
 * {@code MemoryMeterStrategy} decorator that cache measurements based on a provided {@code CachingStrategy}.
 */
public class MeasurementCache implements MemoryMeterStrategy {

    /**
     * The decorated {@code MemoryMeterStrategy} used to perform the measurements.
     */
    private MemoryMeterStrategy meterStrategy;

    /**
     * The caching strategy used to determine which measurements must be cached.
     */
    private CachingStrategy cachingStrategy;

    /**
     * The map used to cache the measurements
     */
    private final Map<Class<?>, Long> cache = new ConcurrentHashMap<>();

    /**
     * Wraps the specified meter strategy.
     *
     * @param meterStrategy the decorated strategy
     * @param cachingStrategy the strategy used to populate the cache.
     */
    public MeasurementCache(MemoryMeterStrategy meterStrategy, CachingStrategy cachingStrategy) {
        this.meterStrategy = meterStrategy;
        this.cachingStrategy = cachingStrategy;
        cache.putAll(cachingStrategy.measurementsToPreload(meterStrategy));
    }

    @Override
    public long measureInstance(Object object, Class<?> type) {
        Long size = cache.get(type);
        if (size != null)
            return size.longValue();

        size = meterStrategy.measureInstance(object, type);
        if (cachingStrategy.mustCache(type))
            cache.put(type, size);

        return size;
    }

    @Override
    public long measureArray(Object array, Class<?> type) {
        return meterStrategy.measureArray(array, type);
    }

    @Override
    public long measureArray(Object[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(byte[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(boolean[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(short[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(char[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(int[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(float[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(long[] array) {
        return meterStrategy.measureArray(array);
    }

    @Override
    public long measureArray(double[] array) {
        return meterStrategy.measureArray(array);
    }
}
