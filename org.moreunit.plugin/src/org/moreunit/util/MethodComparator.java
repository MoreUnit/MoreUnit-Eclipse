package org.moreunit.util;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jdt.core.IMethod;

public class MethodComparator implements Comparator<IMethod>
{

    public int compare(IMethod method1, IMethod method2)
    {
        return Collator.getInstance().compare(method1.getElementName(), method2.getElementName());
    }

}
