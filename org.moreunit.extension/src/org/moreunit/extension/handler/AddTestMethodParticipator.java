/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.extension.handler;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.extensionpoints.IAddTestMethodParticipator;
import org.moreunit.log.LogHandler;
import org.moreunit.util.StringConstants;

/**
 * The class <code>AddTestMethodParticipator</code> implements a participator, that
 * modifies the test method created by moreunit.
 * <p>
 * <b>&copy; AG, D-49326 Melle 2010</b>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 09.07.2010
 * @since 1.5
 */
public class AddTestMethodParticipator implements IAddTestMethodParticipator {

	/**
	 * Constructor for AddTestMethodParticipator.
	 */
	public AddTestMethodParticipator() {

		// Default-Contructor
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTestMethod(final IAddTestMethodContext context) {

		try {
			doAddTestMethod(context);
		} catch (Exception e) {
			LogHandler.getInstance().handleExceptionLog("Exeption running extension", e);
		}
	}

	/**
	 * Run extension code.
	 * @param context Extension context.
	 * @throws JavaModelException Error.
	 */
	private void doAddTestMethod(final IAddTestMethodContext context)
		throws JavaModelException {

		// Inits
		IMethod testMethod = context.getTestMethod();

		// If testMethod is null, the method existed already
		if (testMethod == null) {
			LogHandler.getInstance().handleInfoLog("TestMethod already exists");
			return;
		}

		// Get method under test
		IMethod methodUnderTest = context.getMethodUnderTest();

		// Info
		LogHandler.getInstance().handleInfoLog(
			"TestMethod: " + testMethod.getElementName());

		// Get Source
		StringBuilder builder = new StringBuilder(testMethod.getSource());

		// Add throws Exception
		int start = builder.indexOf("public void ");
		start = builder.indexOf("()", start);
		start = builder.indexOf("{", start);
		builder.replace(start, start + 1, "throws Exception {");

		// Extend Annotation
		String target = "@Test";
		start = builder.indexOf(target);
		builder.replace(start, start + target.length(), "@Test(groups = \"Standard\")");

		// Add comment
		builder.insert(0, createTestMethodComment(methodUnderTest));

		// Delete old method
		ICompilationUnit testUnit = context.getTestClass();
		IJavaElement sibling = getSiblingElement(testUnit, testMethod);
		testMethod.delete(true, null);

		// Create new test method and add it to context
		IMethod newMethod =
			testUnit.findPrimaryType().createMethod(builder.toString(), sibling, true,
				null);
		context.setTestMethod(newMethod);
	}

	/**
	 * Get the sibling of the method.
	 * @param unit Compilation unit.
	 * @param method Method.
	 * @return Sibling element.
	 * @throws JavaModelException Error.
	 */
	private IJavaElement getSiblingElement(final ICompilationUnit unit,
		final IMethod method) throws JavaModelException {

		// Inits
		IJavaElement sibling = null;

		// Get the class (eclipse says type), in that our method is defined
		IType type = method.getDeclaringType();
		for (IMethod iMethod : type.getMethods()) {
			if (iMethod.equals(method)) {
				return sibling;
			}
			sibling = iMethod;
		}

		// Not found
		return null;
	}

	/**
	 * Create the Testmethod JavaDocComment.
	 * @param methodUnderTest Method to Test.
	 * @return JavaDocComment.
	 */
	private StringBuilder createTestMethodComment(final IMethod methodUnderTest) {

		// Build ParameterTypeList
		StringBuilder parameterList = new StringBuilder();
		for (String parameter : methodUnderTest.getParameterTypes()) {
			if (parameterList.length() > 0) {
				parameterList.append(", ");
			}

			// Add name (without generics)
			String name = Signature.toString(parameter);
			name = name.split("<")[0];
			parameterList.append(name);
		}

		// Build Comment
		StringBuilder commentContent = new StringBuilder();
		commentContent.append("/**") // +
			.append(StringConstants.NEWLINE) // +
			.append(" * Test method for {@link ") // +
			.append(methodUnderTest.getDeclaringType().getFullyQualifiedName()) // +
			.append("#") // +
			.append(methodUnderTest.getElementName()) // +
			.append("(") // +
			.append(parameterList) // +
			.append(")") // +
			.append("}.").append(StringConstants.NEWLINE) // +
			.append(" * @throws Exception Error.").append(StringConstants.NEWLINE) // +
			.append(" */").append(StringConstants.NEWLINE);

		// Deliver
		return commentContent;
	}
}
