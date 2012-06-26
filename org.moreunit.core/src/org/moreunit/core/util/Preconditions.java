package org.moreunit.core.util;

import java.util.Collection;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state.
 * <p>
 * Note: largely inspired by: <a href="http://guava-libraries.googlecode
 * .com/svn/trunk/javadoc/com/google/common/base/Preconditions.html">Google
 * Guava Preconditions</a>, it might be replaced by Google library if other
 * needs arise.
 * </p>
 */
public final class Preconditions
{
    /**
     * Checks that the given object reference is not null.
     * 
     * @param reference the reference to test for nullity
     * @return {@code reference} if not null
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference)
    {
        if(reference == null)
        {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Checks that the given object reference is not null.
     * 
     * @param reference the reference to test for nullity
     * @param errorMessage the exception message to use if {@code reference} is
     *            null (will be converted to a string if not already one)
     * @return {@code reference} if not null
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage)
    {
        if(reference == null)
        {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Checks that the given expression is true.
     * 
     * @param expression the boolean expression to evaluate
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression)
    {
        if(! expression)
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks that the given expression is true.
     * 
     * @param expression the boolean expression to evaluate
     * @param errorMessage the exception message to use if {@code expression} is
     *            false (will be converted to a string if not already one)
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, Object errorMessage)
    {
        if(! expression)
        {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Checks that the given collection reference is not null and - if not null
     * - checks that the collection is not empty.
     * 
     * @param collection the collection reference to test for nullity or
     *            emptiness
     * @return {@code collection} if not null or empty
     * @throws NullPointerException if {@code collection} is null
     * @throws IllegalArgumentException if {@code collection} is empty
     */
    public static <T, C extends Collection<T>> C checkNotNullOrEmpty(C collection)
    {
        checkNotNull(collection);
        checkArgument(! collection.isEmpty());
        return collection;
    }

    /**
     * Checks that the given collection reference is not null and - if not null
     * - checks that the collection is not empty.
     * 
     * @param collection the collection reference to test for nullity or
     *            emptiness
     * @param errorMessage the exception message to use if
     *            {@code collectionReference} is null or empty (will be
     *            converted to a string if not already one)
     * @return {@code collection} if not null or empty
     * @throws NullPointerException if {@code collection} is null
     * @throws IllegalArgumentException if {@code collection} is empty
     */
    public static <T, C extends Collection<T>> C checkNotNullOrEmpty(C collection, Object errorMessage)
    {
        checkNotNull(collection, errorMessage);
        checkArgument(! collection.isEmpty(), errorMessage);
        return collection;
    }
}
