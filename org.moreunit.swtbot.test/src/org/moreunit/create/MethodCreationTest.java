package org.moreunit.create;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moreunit.ConditionCursorLine;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestProject;
import org.moreunit.test.context.TestType;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MethodCreationTest extends JavaProjectSWTBotTestHelper
{
	@Project(mainSrc = "MethodCreation_class_with_method.txt",
			 testSrc = "MethodCreation_test_without_method.txt",
			 mainSrcFolder = "src",
			 testSrcFolder = "junit",
			 properties = @Properties(
				        testType = TestType.JUNIT4,
				        testClassNameTemplate = "${srcFile}Test",
				        testMethodPrefix = true))
	@Test
	public void should_create_testmethod_when_shortcut_is_pressed_in_method() throws JavaModelException
	{
		openResource("TheWorld.java");
    	SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
    	// move cursor to method
    	int lineNumberOfMethod = 6;
		cutEditor.navigateTo(lineNumberOfMethod, 9);
		bot.waitUntil(new ConditionCursorLine(cutEditor, lineNumberOfMethod));
		
		getShortcutStrategy().pressGenerateShortcut();
				
		bot.waitUntil(new DefaultCondition()
        {
            
            @Override
            public boolean test() throws Exception
            {
                return context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods().length != 0;
            }
            
            @Override
            public String getFailureMessage()
            {
                return "No method added to testing.TheWorldTest";
            }
        }, 20000);
		IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
		assertThat(methods).onProperty("elementName").containsOnly("testGetNumber1");
	}
	
	@Project(mainSrc = "MethodCreation_class_with_method.txt",
	   		 testSrc = "MethodCreation_test_with_testmethod.txt",
	   		 mainSrcFolder = "src",
	   		 testSrcFolder = "junit",
	   		 properties = @Properties(
	    			        testType = TestType.JUNIT4,
	    			        testClassNameTemplate = "${srcFile}Test",
	    			        testMethodPrefix = true))
	@Test
	public void should_create_second_testmethod_when_shortcut_is_pressed_in_testmethod() throws JavaModelException
	{
		openResource("TheWorldTest.java");
		final SWTBotEclipseEditor testcaseEditor = bot.activeEditor().toTextEditor();
		// move cursor to method
    	int lineNumberOfMethod = 9;
    	testcaseEditor.navigateTo(lineNumberOfMethod, 9);
    	bot.waitUntil(new ConditionCursorLine(testcaseEditor, lineNumberOfMethod));
		
		getShortcutStrategy().pressGenerateShortcut();

		// wait until the testcase has changes
		bot.waitUntil(new DefaultCondition() {
			
			@Override
			public boolean test() throws Exception {
				return testcaseEditor.isDirty();
			}
			
			@Override
			public String getFailureMessage() {
				return "Testcase was not manipulated";
			}
		}, 20000);
		testcaseEditor.save();
		
		IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
		assertThat(methods).hasSize(2).onProperty("elementName").contains("testGetNumber1Suffix");
	}
	
	@Project(mainSrc = "MethodCreation_class_with_method.txt",
	        mainSrcFolder = "src",
	        testProject = @TestProject(src = "MethodCreation_test_without_method.txt"),
	        properties = @Properties(
	                                 testType = TestType.JUNIT4,
	                                 testClassNameTemplate = "${srcFile}Test",
	                                 testMethodPrefix = true))
	@Test
	@Ignore
	public void should_create_testmethod_when_shortcut_is_pressed_in_method_with_test_folder_in_another_project() throws JavaModelException
	{
	    openResource("TheWorld.java");
	    SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
	    // move cursor to method
	    int lineNumberOfMethod = 6;
	    cutEditor.navigateTo(lineNumberOfMethod, 9);
	    bot.waitUntil(new ConditionCursorLine(cutEditor, lineNumberOfMethod));

	    getShortcutStrategy().pressGenerateShortcut();
	    
	    // adding the method to the testcase takes a short moment
	    bot.waitUntil(new DefaultCondition()
        {
            
            @Override
            public boolean test() throws Exception
            {
                IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
                return methods.length == 1;
            }
            
            @Override
            public String getFailureMessage()
            {
                try
                {
                    IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
                    StringBuilder errorMessage = new StringBuilder("Element names of methods found: ");
                    for (IMethod method : methods)
                    {
                        errorMessage.append(method.getElementName());
                    }
                    return errorMessage.toString();
                }
                catch (JavaModelException e)
                {
                    return "Cannot retrieve methods " + e.getMessage();
                }
            }
        }, 20000);
	    
        IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
        assertThat(methods).onProperty("elementName").containsOnly("testGetNumber1");
	}

}
