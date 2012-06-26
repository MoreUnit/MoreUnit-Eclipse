package org.moreunit.mock.model;

import static org.moreunit.core.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

public class SetterDependency extends Dependency
{
    public final String setterMethodName;

    public SetterDependency(String fullyQualifiedClassName, String setterMethodName)
    {
        this(fullyQualifiedClassName, setterMethodName, new ArrayList<TypeParameter>());
    }

    public SetterDependency(String fullyQualifiedClassName, String setterMethodName, List<TypeParameter> typeParameters)
    {
        super(fullyQualifiedClassName, dependencyNameFrom(setterMethodName), typeParameters);
        this.setterMethodName = setterMethodName;
    }

    private static String dependencyNameFrom(String setterMethod)
    {
        checkNotNull(setterMethod);

        if(setterMethod.length() < 4 || ! setterMethod.startsWith("set"))
        {
            throw new IllegalArgumentException(String.format("'%s' is not a setter", setterMethod));
        }

        String withoutSet = setterMethod.substring(3);
        return withoutSet.substring(0, 1).toLowerCase() + withoutSet.substring(1);
    }

}
