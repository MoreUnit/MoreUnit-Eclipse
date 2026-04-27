package org.moreunit.mock.templates;

import java.io.Serial;


import org.eclipse.jdt.core.IType;

public class NoDependenciesToMockException extends MockingTemplateException
{
    @Serial
    private static final long serialVersionUID = 3403205174495871647L;

    public NoDependenciesToMockException(IType classUnderTest)
    {
        super("Could not find any dependencies to mock in " + classUnderTest.getElementName(), true);
    }
}
