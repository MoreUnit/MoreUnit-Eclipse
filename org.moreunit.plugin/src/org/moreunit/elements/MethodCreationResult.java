package org.moreunit.elements;

import org.eclipse.jdt.core.IMethod;

public class MethodCreationResult
{
    private static enum MethodType
    {
        EXISTING, JUST_CREATED, NOT_APPLICABLE;
    }

    private final IMethod method;
    private final MethodType methodType;

    public static MethodCreationResult from(IMethod maybeCreatedMethod)
    {
        if(maybeCreatedMethod == null)
            return noMethodCreated();
        return new MethodCreationResult(maybeCreatedMethod, MethodType.JUST_CREATED);
    }

    public static MethodCreationResult methodAlreadyExists(IMethod existingMethod)
    {
        return new MethodCreationResult(existingMethod, MethodType.EXISTING);
    }

    public static MethodCreationResult noMethodCreated()
    {
        return new MethodCreationResult(null, MethodType.NOT_APPLICABLE);
    }

    private MethodCreationResult(IMethod method, MethodType methodType)
    {
        this.method = method;
        this.methodType = methodType;
    }

    public boolean methodAlreadyExists()
    {
        return methodType == MethodType.EXISTING;
    }

    public boolean methodCreated()
    {
        return methodType == MethodType.JUST_CREATED;
    }

    public IMethod getMethod()
    {
        return method;
    }
}
