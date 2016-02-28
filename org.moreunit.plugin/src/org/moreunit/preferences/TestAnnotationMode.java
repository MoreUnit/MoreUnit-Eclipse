package org.moreunit.preferences;

import org.moreunit.preferences.Preferences.MethodSearchMode;

public enum TestAnnotationMode
{
    OFF(null), BY_NAME(MethodSearchMode.BY_NAME), BY_CALL_AND_BY_NAME(MethodSearchMode.BY_CALL_AND_BY_NAME);

    private final MethodSearchMode methodSearchMode;

    TestAnnotationMode(MethodSearchMode methodSearchMode)
    {
        this.methodSearchMode = methodSearchMode;
    }

    public MethodSearchMode getMethodSearchMode()
    {
        if(this == OFF)
        {
            throw new IllegalStateException("no method search mode available when annotations are OFF");
        }
        return methodSearchMode;
    }
}
