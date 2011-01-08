package org.moreunit.util;

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
public class Preconditions
{

    public static <T> T checkNotNull(T reference)
    {
        return checkNotNull(reference, null);
    }

    public static <T> T checkNotNull(T reference, String errorMessage)
    {
        if(reference == null)
        {
            if(errorMessage == null)
                throw new NullPointerException();
            else
                throw new NullPointerException(errorMessage);
        }
        return reference;
    }
}
