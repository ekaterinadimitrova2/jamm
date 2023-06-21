package org.github.jamm;

/**
 * Represents a strategy to measure the shallow memory used by a Java object. 
 */
public interface MemoryMeterStrategy
{
    /**
     * Measures the shallow memory used by the specified object.
     *
     * @param object the object to measure
     * @param type the object type
     * @return the shallow memory usage of the specified object
     */
    default long measure(Object object) {

        Class<?> type = object.getClass();
        if (type.isArray())
            return measureArray(object, type);

        return measureInstance(object, type);
    }

    /**
     * Measures the shallow memory used by the specified object.
     *
     * @param object the object to measure
     * @param type the object type
     * @return the shallow memory usage of the specified object
     */
    long measureInstance(Object object, Class<?> type);

    /**
     * Measures the shallow memory used by the specified array.
     *
     * @param object the array to measure
     * @param type the array type
     * @return the shallow memory usage of the specified array
     */
    long measureArray(Object array, Class<?> type);

    /**
     * Measures the shallow memory used by the specified array.
     *
     * @param array the array to measure
     * @return the shallow memory used by the specified array.
     */
    default long measureArray(Object[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified byte array.
     *
     * @param array the array to measure
     * @return the shallow memory used by the specified byte array.
     */
    default long measureArray(byte[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified boolean array.
     *
     * @param array the boolean array to measure
     * @return the shallow memory used by the specified boolean array.
     */
    default long measureArray(boolean[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified short array.
     *
     * @param array the short array to measure
     * @return the shallow memory used by the specified short array.
     */
    default long measureArray(short[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified char array.
     *
     * @param array the char array to measure
     * @return the shallow memory used by the specified char array.
     */
    default long measureArray(char[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified int array.
     *
     * @param array the int array to measure
     * @return the shallow memory used by the specified int array.
     */
    default long measureArray(int[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified float array.
     *
     * @param array the float array to measure
     * @return the shallow memory used by the specified float array.
     */
    default long measureArray(float[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified long array.
     *
     * @param array the long array to measure
     * @return the shallow memory used by the specified long array.
     */
    default long measureArray(long[] array)
    {
        return measureArray(array, array.getClass());
    }

    /**
     * Measures the shallow memory used by the specified double array.
     *
     * @param array the long array to measure
     * @return the shallow memory used by the specified double array.
     */
    default long measureArray(double[] array)
    {
        return measureArray(array, array.getClass());
    }
}
