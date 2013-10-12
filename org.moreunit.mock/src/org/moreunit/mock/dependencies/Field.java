package org.moreunit.mock.dependencies;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

public class Field
{
    private final IField field;
    private boolean visibleToTestCase;

    public Field(IField field, boolean visibleToTestCase)
    {
        this.field = field;
        this.visibleToTestCase = visibleToTestCase;
    }

    public IField get()
    {
        return field;
    }

    public boolean isAssignable() throws JavaModelException
    {
        return ! Flags.isFinal(field.getFlags());
    }

    public boolean isInjectable() throws JavaModelException
    {
        return hasAnnotation("Inject") || hasAnnotation("Resource") || hasAnnotation("Autowired");
    }

    private boolean hasAnnotation(String annotationName) throws JavaModelException
    {
        // we can't just use fied.getAnnotation(annotationName).exists() as it
        // may return a cached value
        for (IAnnotation annotation : field.getAnnotations())
        {
            if(annotation.getElementName().matches("^([^\\.]+\\.)*" + annotationName + "$"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isVisibleToTestCase()
    {
        return visibleToTestCase;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        return field.equals(((Field) obj).field);
    }

    @Override
    public String toString()
    {
        return field.toString();
    }
}
