package org.github.jamm;

import java.util.HashMap;
import java.util.Map;

/**
 * The strategy used to determine which measurements should be cached or preloaded in the cache.
 *
 * <p>This strategy will work only for non-arrays as arrays size is based on type and length.</p> 
 */
public interface CachingStrategy {

    /**
     * Returns the measurements that should be preloaded in the cache when the {@code MemoryMeter} instance is created.
     *
     * @param strategy the {@code MemoryMeterStrategy} used to measure the classes
     * @return the measurements that should be preloaded in the cache when the {@code MemoryMeter} instance is created.
     */
    Map<Class<?>, Long> measurementsToPreload(MemoryMeterStrategy strategy);

    /**
     * Checks if the measurement corresponding to the specified class must be cached.
     *
     * @param type the class being measured
     * @return {@code true} if the measurement corresponding to the specified class must be cached, {@code false} otherwise.
     */
    boolean mustCache(Class<?> type);

    /**
     * Helper class to build a set of measurements for cache preloading.
     */
    public static final class PreloadBuilder {

        /**
         * The preloaded measurements
         */
        private final Map<Class<?>, Long> toPreload = new HashMap<>();

        /**
         * The strategy used to measure instance size.
         */
        private final MemoryMeterStrategy strategy;

        /**
         * Creates a new {@code PreloadBuilder} instance.
         * @param strategy the strategy to use for the measurements.
         */
        public PreloadBuilder(MemoryMeterStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Measure the memory occupied by classes from the specified instance and store the measurement for cache preloading.
         *
         * @param instance the object to measure
         * @return this {@code PreloadBuilder}
         */
        public PreloadBuilder addMeasurement(Object instance) {
            Class<?> type = instance.getClass();
            toPreload.put(type, strategy.measureInstance(instance, type));
            return this;
        }

        /**
         * Build the map that can be used to preload the cache.
         * @return the map that can be used to preload the cache.
         */
        public Map<Class<?>, Long> build() {
            return toPreload;
        }
    }
}
