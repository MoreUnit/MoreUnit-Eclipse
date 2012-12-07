package org.moreunit.mock.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;

public class DependenciesTreeContentProvider implements ITreeContentProvider
{
    private static final Object[] EMPTY_ARRAY = new Object[0];

    private final IType classUnderTest;
    private final DependencyInjectionPointProvider provider;
    private final Logger logger;
    private final List<IType> types = new ArrayList<IType>();
    private final List<IMember> members = new ArrayList<IMember>();

    public DependenciesTreeContentProvider(IType classUnderTest, DependencyInjectionPointProvider provider, Logger logger)
    {
        this.classUnderTest = classUnderTest;
        this.provider = provider;
        this.logger = logger;
        initTypes();
        initMembers();
        removeUnusedTypes();
    }

    private void initTypes()
    {
        try
        {
            types.add(classUnderTest);

            ITypeHierarchy hierarchy = classUnderTest.newSupertypeHierarchy(null);
            Collections.addAll(types, hierarchy.getAllSuperclasses(classUnderTest));
        }
        catch (JavaModelException e)
        {
            logger.error("Error while populating dependencies tree", e);
        }
    }

    private void initMembers()
    {
        try
        {
            addMethods(provider.getConstructors());
            addMethods(provider.getSetters());
            addFields(provider.getFields());
            sortMembers();
        }
        catch (JavaModelException e)
        {
            logger.error("Error while populating dependencies tree", e);
        }
    }

    private void addMethods(Iterable<IMethod> methods) throws JavaModelException
    {
        for_methods: for (IMethod method : methods)
        {
            for (ListIterator<IMember> memberIt = members.listIterator(); memberIt.hasNext();)
            {
                IMember alreadyCollectedMember = memberIt.next();
                if(alreadyCollectedMember instanceof IMethod && alreadyCollectedMember.getElementName().equals(method.getElementName())
                   && ((IMethod) alreadyCollectedMember).getSignature().equals(method.getSignature()))
                {
                    memberIt.set(method);
                    continue for_methods;
                }
            }
            members.add(method);
        }
    }

    private void addFields(Iterable<IField> fields)
    {
        for_fields: for (IField field : fields)
        {
            for (ListIterator<IMember> memberIt = members.listIterator(); memberIt.hasNext();)
            {
                IMember alreadyCollectedMemeber = memberIt.next();
                if(alreadyCollectedMemeber instanceof IMethod && alreadyCollectedMemeber.getElementName().equals(field.getElementName()))
                {
                    memberIt.set(field);
                    continue for_fields;
                }
            }
            members.add(field);
        }
    }

    private void sortMembers()
    {
        // does not care about type ordering, this is already handled by 'types'
        Collections.sort(members, new DependenciesTreeMemberComparator());
    }

    private void removeUnusedTypes()
    {
        Set<IType> usedTypes = new HashSet<IType>(types.size());
        for (IMember member : members)
        {
            usedTypes.add(member.getDeclaringType());
        }

        types.retainAll(usedTypes);
    }

    public Object[] getChildren(Object parentElement)
    {
        if(parentElement instanceof IType)
        {
            IType parentType = (IType) parentElement;
            List<IMember> result = new ArrayList<IMember>(members.size());
            for (IMember member : members)
            {
                if(member.getDeclaringType().equals(parentType))
                {
                    result.add(member);
                }
            }
            return result.toArray();
        }
        return EMPTY_ARRAY;
    }

    public Object getParent(Object element)
    {
        if(element instanceof IMethod)
        {
            return ((IMethod) element).getDeclaringType();
        }
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return getChildren(element).length > 0;
    }

    public Object[] getElements(Object inputElement)
    {
        return getTypes();
    }

    public Object[] getTypes()
    {
        return types.toArray();
    }

    public void dispose()
    {
        // void implementation
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // void implementation
    }
}
