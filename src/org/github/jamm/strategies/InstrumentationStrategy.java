package org.github.jamm.strategies;

import java.lang.instrument.Instrumentation;

import org.github.jamm.MemoryMeterStrategy;

/**
 * {@code MemoryMeterStrategy} relying on {@code Instrumentation} to measure object size.
 *
 */
final class InstrumentationStrategy implements MemoryMeterStrategy {

    public final Instrumentation instrumentation;

    public InstrumentationStrategy(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public long measureInstance(Object object, Class<?> type) {
        return instrumentation.getObjectSize(object);
    }

    @Override
    public long measureArray(Object array, Class<?> type) {
        return instrumentation.getObjectSize(array);
    }
}
