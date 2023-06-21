package org.github.jamm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.github.jamm.CachingStrategy.PreloadBuilder;

/**
 * Pre-defined caching strategies.
 */
public final class CachingStrategies {

    /**
     * Caching strategy that cache every instance measurements for re-use.
     */
    public static final CachingStrategy EVERYTHING = new CachingStrategy() {

        @Override
        public boolean mustCache(Class<?> type) {
            return true;
        }

        @Override
        public Map<Class<?>, Long> measurementsToPreload(MemoryMeterStrategy strategy) {
            return Collections.emptyMap();
        }
    };

    /**
     * Caching strategy that preload the measurements for the most commonly used java classes.
     */
    public static final CachingStrategy MOST_COMMON_JAVA_CLASSES = new CachingStrategy() {

        @Override
        public boolean mustCache(Class<?> type) {
            return false;
        }

        @Override
        public Map<Class<?>, Long> measurementsToPreload(MemoryMeterStrategy strategy) {

            return mostCommonlyJavaClasses(strategy);
        }
    };

    /**
     * Caching strategy that preload the measurements for most commonly used java classes and cache the measurements
     * of measured classes implementing the {@code Measured} interface.
     */
    public static final CachingStrategy MOST_COMMON_JAVA_CLASSES_AND_MEASURABLE = new CachingStrategy() {

        @Override
        public boolean mustCache(Class<?> type) {
            return Measurable.class.isAssignableFrom(type);
        }

        @Override
        public Map<Class<?>, Long> measurementsToPreload(MemoryMeterStrategy strategy) {

            return mostCommonlyJavaClasses(strategy);
        }
    };
    
    /**
     * Returns the measurements for the most commonly used Java classes.
     *
     * @param strategy the {@code MemoryMeterStrategy} measurements.
     * @return the measurements for the most commonly used Java classes.
     */
    private static Map<Class<?>, Long> mostCommonlyJavaClasses(MemoryMeterStrategy strategy) {

        ByteBuffer heapBuffer = ByteBuffer.allocate(0);
        ByteBuffer readOnlyHeapBuffer = ByteBuffer.allocate(0).asReadOnlyBuffer();
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(0);
        ByteBuffer readOnlyDirectBuffer = ByteBuffer.allocateDirect(0).asReadOnlyBuffer();

        return new PreloadBuilder(strategy).addMeasurement(Byte.valueOf((byte) 0))
                                           .addMeasurement(Short.valueOf((short) 0))
                                           .addMeasurement(Character.valueOf('a'))
                                           .addMeasurement(Integer.valueOf(0))
                                           .addMeasurement(Long.valueOf(0))
                                           .addMeasurement(Float.valueOf(0))
                                           .addMeasurement(Double.valueOf(0))
                                           .addMeasurement("")
                                           .addMeasurement(new ArrayList<>())
                                           .addMeasurement(new HashSet<>())
                                           .addMeasurement(new HashMap<>())
                                           .addMeasurement(heapBuffer)
                                           .addMeasurement(readOnlyHeapBuffer)
                                           .addMeasurement(directBuffer)
                                           .addMeasurement(readOnlyDirectBuffer)
                                           .build();
    }

    private CachingStrategies() {
    }
}
