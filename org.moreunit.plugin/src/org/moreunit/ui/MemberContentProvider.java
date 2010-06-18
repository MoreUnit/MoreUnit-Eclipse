package org.moreunit.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.MethodComparator;
import org.moreunit.util.TestNamingPatternAwareTypeComparator;

public class MemberContentProvider implements ITreeContentAndDefaultSelectionProvider
{

    private final Map<IType, List<IMethod>> methodsByType;
    private final Object[] types;
    private final ISelection defaultSelection;

    public MemberContentProvider(Set<IType> types, Set<IMethod> methods)
    {
        methodsByType = groupMethodsByType(methods);

        List<IType> sortedTypes = sortTypes(types);
        Set<IType> allTypes = new LinkedHashSet<IType>(sortedTypes);
        allTypes.addAll(sortTypes(methodsByType.keySet()));
        this.types = allTypes.toArray();

        defaultSelection = getDefaultSelection(sortedTypes);
    }

    private Map<IType, List<IMethod>> groupMethodsByType(Set<IMethod> methods)
    {
        Map<IType, List<IMethod>> methodsByType = new LinkedHashMap<IType, List<IMethod>>();
        for (IMethod method : methods)
        {
            IType type = method.getDeclaringType();
            List<IMethod> methodsForType = methodsByType.get(type);
            if(methodsForType == null)
            {
                methodsForType = new ArrayList<IMethod>();
                methodsByType.put(type, methodsForType);
            }
            methodsForType.add(method);
        }

        for (List<IMethod> typeMethods : methodsByType.values())
        {
            Collections.sort(typeMethods, new MethodComparator());
        }

        return methodsByType;
    }

    private List<IType> sortTypes(Collection<IType> types)
    {
        List<IType> list = new ArrayList<IType>(types);
        Collections.sort(list, new TestNamingPatternAwareTypeComparator(Preferences.getInstance()));
        return list;
    }

    private ISelection getDefaultSelection(List<IType> types)
    {
        IMember defaultSelection = null;
        if(! types.isEmpty())
        {
            defaultSelection = types.get(0);
            List<IMethod> methods = methodsByType.get(defaultSelection);
            if(methods != null && ! methods.isEmpty())
            {
                defaultSelection = methods.get(0);
            }
        }
        return defaultSelection == null ? null : new StructuredSelection(defaultSelection);
    }

    public Object[] getChildren(Object parentElement)
    {
        List<IMethod> children = methodsByType.get(parentElement);
        return children == null ? new IMethod[0] : children.toArray();
    }

    public Object getParent(Object element)
    {
        return element instanceof IType ? null : ((IMethod) element).getDeclaringType();
    }

    public boolean hasChildren(Object element)
    {
        return element instanceof IType && methodsByType.get(element) != null;
    }

    public Object[] getElements(Object inputElement)
    {
        return types;
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public ISelection getDefaultSelection()
    {
        return defaultSelection;
    }

}
