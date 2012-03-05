package org.moreunit.create;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;


public class MethodCreationTest extends JavaProjectSWTBotTestHelper
{
	@Project(mainSrc = "MethodCreation_class_with_method.txt",
			 testSrc = "MethodCreation_test_without_method.txt",
			 mainSrcFolder = "src",
			 testSrcFolder = "junit",
			 properties = @Properties(
				        testType = TestType.JUNIT4,
				        testClassSuffixes = "Test",
				        testMethodPrefix = true))
	@Test
	public void should_create_testmethod_when_shortcut_is_pressed_in_method() throws JavaModelException
	{
		openResource("TheWorld.java");
    	SWTBotEclipseEditor cutEditor = bot.activeEditor().toTextEditor();
    	// move cursor to method
    	int lineNumberOfMethod = 6;
		cutEditor.navigateTo(lineNumberOfMethod, 9);
		
		getShortcutStrategy().pressGenerateShortcut();
		
		// adding the method to the testcase takes a short moment
		bot.sleep(1000);
		
		IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
		assertThat(methods).onProperty("elementName").containsOnly("testGetNumber1");
	}
	
	@Project(mainSrc = "MethodCreation_class_with_method.txt",
	   		 testSrc = "MethodCreation_test_with_testmethod.txt",
	   		 mainSrcFolder = "src",
	   		 testSrcFolder = "junit",
	   		 properties = @Properties(
	    			        testType = TestType.JUNIT4,
	    			        testClassSuffixes = "Test",
	    			        testMethodPrefix = true))
	@Test
	public void should_create_second_testmethod_when_shortcut_is_pressed_in_testmethod() throws JavaModelException
	{
		openResource("TheWorldTest.java");
		final SWTBotEclipseEditor testcaseEditor = bot.activeEditor().toTextEditor();
		// move cursor to method
    	int lineNumberOfMethod = 8;
    	testcaseEditor.navigateTo(lineNumberOfMethod, 9);
		
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
		}, 10000);
		testcaseEditor.save();
		
		IMethod[] methods = context.getCompilationUnit("testing.TheWorldTest").findPrimaryType().getMethods();
		assertThat(methods).hasSize(2).onProperty("elementName").contains("testGetNumber1Suffix");
	}
	
}
