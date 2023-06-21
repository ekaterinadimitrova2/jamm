package org.github.jamm;
import java.util.ArrayList;
import java.util.LinkedList;

import org.github.jamm.MemoryMeter.Guess;
import org.github.jamm.strategies.MeasurementCache;
import org.github.jamm.strategies.MemoryMeterStrategies;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 */
public class MeasurementCacheTest {

    @Test
    public void testCacheEverything() {
        MemoryMeter reference = MemoryMeter.builder().withGuessing(Guess.ALWAYS_INSTRUMENTATION).build();
        Tracker tracker = new Tracker();
        MemoryMeter meter = memoryMeter(tracker, CachingStrategies.EVERYTHING);

        // First call no caching
        assertEquals(reference.measure("test"), meter.measure("test"));
        assertEquals(1, tracker.measureInstanceCount);

        // Second call String classes size is cached caching
        assertEquals(reference.measure("test"), meter.measure("test"));
        assertEquals(1, tracker.measureInstanceCount);

        // Try with a different String
        assertEquals(reference.measure("boom"), meter.measure("boom"));
        assertEquals(1, tracker.measureInstanceCount);

        // With measure deep we should also hit the cache for the String object then measure the array directly
        assertEquals(0, tracker.measureArrayCount);
        assertEquals(reference.measureDeep("boom"), meter.measureDeep("boom"));
        assertEquals(1, tracker.measureInstanceCount);
        assertEquals(1, tracker.measureArrayCount);

        // Array are never cached
        assertEquals(reference.measureDeep("boom"), meter.measureDeep("boom"));
        assertEquals(1, tracker.measureInstanceCount);
        assertEquals(2, tracker.measureArrayCount);

        // First call for a different object through measureDeep
        assertEquals(reference.measureDeep(new ArrayList<>()), meter.measureDeep(new ArrayList<>()));
        assertEquals(2, tracker.measureInstanceCount);
        assertEquals(3, tracker.measureArrayCount);

        // The measurement for ArrayList should be in the cache
        assertEquals(reference.measure(new ArrayList<>()), meter.measure(new ArrayList<>()));
        assertEquals(2, tracker.measureInstanceCount);
    }

    @Test
    public void testCacheMostCommonJavaClasses() {
        MemoryMeter reference = MemoryMeter.builder().withGuessing(Guess.ALWAYS_INSTRUMENTATION).build();
        Tracker tracker = new Tracker();
        MemoryMeter meter = memoryMeter(tracker, CachingStrategies.MOST_COMMON_JAVA_CLASSES);

        int preloadingCalls = tracker.measureInstanceCount;

        // First call String already cached
        assertEquals(reference.measure("test"), meter.measure("test"));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);

        // Try with a different String
        assertEquals(reference.measure("boom"), meter.measure("boom"));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);

        // With measure deep we should also hit the cache for the String object then measure the array directly
        assertEquals(0, tracker.measureArrayCount);
        assertEquals(reference.measureDeep("boom"), meter.measureDeep("boom"));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);
        assertEquals(1, tracker.measureArrayCount);

        // Array are never cached
        assertEquals(reference.measureDeep("boom"), meter.measureDeep("boom"));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);
        assertEquals(2, tracker.measureArrayCount);

        // The measurement for ArrayList should be in the cache
        assertEquals(reference.measureDeep(new ArrayList<>()), meter.measureDeep(new ArrayList<>()));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);
        assertEquals(3, tracker.measureArrayCount);

        // Other objects should not be in the cache
        assertEquals(reference.measure(new LinkedList<>()), meter.measure(new LinkedList<>()));
        assertEquals(preloadingCalls + 1, tracker.measureInstanceCount);

        // and should not be cached
        assertEquals(reference.measure(new LinkedList<>()), meter.measure(new LinkedList<>()));
        assertEquals(preloadingCalls + 2, tracker.measureInstanceCount);
    }

    @Test
    public void testCacheMostCommonJavaClassesAndMeasurable() {
        MemoryMeter reference = MemoryMeter.builder().withGuessing(Guess.ALWAYS_INSTRUMENTATION).build();
        Tracker tracker = new Tracker();
        MemoryMeter meter = memoryMeter(tracker, CachingStrategies.MOST_COMMON_JAVA_CLASSES_AND_MEASURABLE);

        int preloadingCalls = tracker.measureInstanceCount;

        assertEquals(reference.measure("test"), meter.measure("test"));
        assertEquals(preloadingCalls, tracker.measureInstanceCount);

        // Other objects should not be in the cache
        assertEquals(reference.measure(new LinkedList<>()), meter.measure(new LinkedList<>()));
        assertEquals(preloadingCalls + 1, tracker.measureInstanceCount);

        // and should not be cached
        assertEquals(reference.measure(new LinkedList<>()), meter.measure(new LinkedList<>()));
        assertEquals(preloadingCalls + 2, tracker.measureInstanceCount);

        ChildMeasurable bart = new ChildMeasurable("Bart");
        ChildMeasurable lisa = new ChildMeasurable("Lisa");
        ChildMeasurable maggie = new ChildMeasurable("Maggie");
        ParentMeasurable homer = new ParentMeasurable("Homer", bart, lisa, maggie);

        assertEquals(0, tracker.measureArrayCount);
        assertEquals(reference.measureDeep(homer), meter.measureDeep(homer));
        assertEquals(preloadingCalls + 4, tracker.measureInstanceCount); // 2 additional calls for parent and child classes
        assertEquals(5, tracker.measureArrayCount); // 1 for each String array + 1 for children array

        // Second time no additional calls except the array one.
        assertEquals(reference.measureDeep(homer), meter.measureDeep(homer));
        assertEquals(preloadingCalls + 4, tracker.measureInstanceCount);
        assertEquals(5 + 5, tracker.measureArrayCount);
    }

    private static class ParentMeasurable implements Measurable
    {
        private final String name;

        private final ChildMeasurable[] children;

        public ParentMeasurable(String name, ChildMeasurable... children) {
            this.name = name;
            this.children = children;
        }

        @Override
        public void addChildrenTo(MeasurementStack stack) {
            stack.pushObject(this, "name", name);
            stack.pushObject(this, "children", children);
        }
    }

    private static class ChildMeasurable implements Measurable
    {
        private String name;

        public ChildMeasurable(String name) {
            this.name = name;
        }
 
        @Override
        public void addChildrenTo(MeasurementStack stack) {
            stack.pushObject(this, "name", name);
        }
    }

    public MemoryMeter memoryMeter(Tracker tracker, CachingStrategy cachingStrategy) {

        MemoryMeterStrategy original = MemoryMeterStrategies.getInstance().getStrategy(Guess.ALWAYS_INSTRUMENTATION);
        MemoryMeterStrategy listener = new CallTracker(original, tracker);
        MemoryMeterStrategy measurementCache = new MeasurementCache(listener, cachingStrategy);

        return new MemoryMeter(measurementCache,
                               Filters.getClassFilters(true),
                               Filters.getFieldFilters(true, false, true),
                               false,
                               NoopMemoryMeterListener.FACTORY);
    }

    public static class Tracker {

        public int measureInstanceCount;

        public int measureArrayCount;
    }

    /**
     * {@code MemoryMeterStrategy} used to count the actual count that go through the caching layer.
     */
    public static class CallTracker implements MemoryMeterStrategy
    {
        private final MemoryMeterStrategy strategy;

        private final Tracker tracker;

        public CallTracker(MemoryMeterStrategy strategy, Tracker tracker) {
            this.strategy = strategy;
            this.tracker = tracker;
        }

        @Override
        public long measureInstance(Object object, Class<?> type) {
            tracker.measureInstanceCount++;
            return strategy.measureInstance(object, type);
        }

        @Override
        public long measureArray(Object array, Class<?> type) {
            tracker.measureArrayCount++;
            return strategy.measureArray(array, type);
        }
    }
}
