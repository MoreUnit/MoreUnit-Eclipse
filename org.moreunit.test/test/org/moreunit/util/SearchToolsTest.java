package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;

import org.moreunit.test.workspace.TypeHandler;

public class SearchToolsTest extends ContextTestCase
{
    @Project(mainCls = "AbstractTest")
    @Test
    public void findConcreteSubclasses_should_find_all_concrete_subclasses() throws Exception
    {
        TypeHandler abstractTypeHandler = context.getPrimaryTypeHandler("AbstractTest");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        abstractTypeHandler.createSubclass("ConcreteTest");
        abstractTypeHandler.createSubclass("AnotherConcreteTest");
        TypeHandler anotherAbstractTypeHandler = abstractTypeHandler.createSubclass("AnotherAbstractTest");
        anotherAbstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AnotherAbstractTest extends AbstractTest {}");
        anotherAbstractTypeHandler.get().getCompilationUnit().save(null, true);

        Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(abstractTypeHandler.get());

        assertThat(concreteSubclasses).hasSize(2).extracting("elementName").containsOnly("ConcreteTest", "AnotherConcreteTest");
    }

    @Project(mainCls = "AbstractTest2")
    @Test
    public void findConcreteSubclasses_should_find_only_concrete_ones() throws Exception
    {
        TypeHandler abstractTypeHandler = context.getPrimaryTypeHandler("AbstractTest2");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest2 {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        abstractTypeHandler.createSubclass("ConcreteTest2");

        Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(abstractTypeHandler.get());

        assertThat(concreteSubclasses).hasSize(1).extracting("elementName").containsOnly("ConcreteTest2");
    }

    @Project(mainCls = "ITest")
    @Test
    public void findConcreteSubclasses_should_handle_interfaces() throws Exception
    {
        TypeHandler interfaceHandler = context.getPrimaryTypeHandler("ITest");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface ITest {}");
        interfaceHandler.get().getCompilationUnit().save(null, true);

        interfaceHandler.createSubclass("ConcreteTest3");
        TypeHandler abstractTypeHandler = interfaceHandler.createSubclass("AbstractTest3");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest3 implements ITest {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(interfaceHandler.get());

        assertThat(concreteSubclasses).hasSize(1).extracting("elementName").containsOnly("ConcreteTest3");
    }
}
