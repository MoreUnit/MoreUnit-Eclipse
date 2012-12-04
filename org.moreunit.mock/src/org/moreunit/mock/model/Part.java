package org.moreunit.mock.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.mock.templates.MockingContext;

@XmlEnum
public enum Part
{
    @XmlEnumValue("test-class-annotation")
    TEST_CLASS_ANNOTATION
    {
        @Override
        public int getInsertionOffset(MockingContext context) throws JavaModelException
        {
            IType type = type(context);
            ISourceRange javadocRange = type.getJavadocRange();
            if(javadocRange != null)
            {
                return javadocRange.getOffset() + javadocRange.getLength() + 1;
            }
            return type.getSourceRange().getOffset();
        }
    },

    @XmlEnumValue("test-class-fields")
    TEST_CLASS_FIELDS
    {
        @Override
        public int getInsertionOffset(MockingContext context) throws JavaModelException
        {
            IType type = type(context);
            Integer offset = afterLastField(type);
            return offset != null ? offset : beforeFirstMember(type);
        }
    },

    @XmlEnumValue("before-instance-method")
    BEFORE_INSTANCE_METHOD
    {
        @Override
        public int getInsertionOffset(MockingContext context) throws JavaModelException
        {
            IMethod beforeInstanceMethod = context.beforeInstanceMethod();
            return beforeInstanceMethod.getSourceRange().getOffset() + beforeInstanceMethod.getSource().lastIndexOf('}') - 1;
        }
    },

    BEFORE_INSTANCE_METHOD_DEFINITION
    {
        @Override
        public int getInsertionOffset(MockingContext context) throws JavaModelException
        {
            IType type = type(context);
            Integer offset = beforeFirstMethod(type);
            return offset != null ? offset : beforeFirstMember(type);
        }
    };

    public abstract int getInsertionOffset(MockingContext context) throws JavaModelException;

    private static IType type(MockingContext context)
    {
        return context.testCaseCompilationUnit.findPrimaryType();
    }

    private static Integer beforeFirstMethod(IType type) throws JavaModelException
    {
        IMethod[] methods = type.getMethods();
        if(methods.length != 0)
        {
            return methods[0].getSourceRange().getOffset();
        }
        return null;
    }

    private static Integer afterLastField(IType type) throws JavaModelException
    {
        IField[] fields = type.getFields();
        if(fields.length != 0)
        {
            ISourceRange fieldRange = fields[fields.length - 1].getSourceRange();
            return fieldRange.getOffset() + fieldRange.getLength();
        }
        return null;
    }

    private static int beforeFirstMember(IType type) throws JavaModelException
    {
        return type.getSourceRange().getOffset() + type.getSource().indexOf('{') + 1;
    }
}
