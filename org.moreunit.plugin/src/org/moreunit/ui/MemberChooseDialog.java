package org.moreunit.ui;

import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class MemberChooseDialog extends ChooseDialog<IMember>
{

    public MemberChooseDialog(String title, Set<IType> types, Set<IMethod> methods)
    {
        super(title, new MemberContentProvider(types, methods));
    }

}
