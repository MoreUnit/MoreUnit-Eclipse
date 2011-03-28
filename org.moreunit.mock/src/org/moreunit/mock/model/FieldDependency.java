package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.List;

public class FieldDependency extends Dependency
{
    public final String fieldName;

    public FieldDependency(String fullyQualifiedClassName, String fieldName, String name)
    {
        this(fullyQualifiedClassName, fieldName, name, new ArrayList<TypeParameter>());
    }

    public FieldDependency(String fullyQualifiedClassName, String fieldName, String name, List<TypeParameter> typeParameters)
    {
        super(fullyQualifiedClassName, name, typeParameters);
        this.fieldName = fieldName;
    }
}
