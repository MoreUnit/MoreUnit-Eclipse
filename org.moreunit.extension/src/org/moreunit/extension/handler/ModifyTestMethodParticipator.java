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

import java.util.List;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.log.LogHandler;

/**
 * The class <code>ModifyTestMethodParticipator</code> modifies test methods created by
 * MoreUnit. Second try, more sophisticated, with using JDT-AST.
 * <p>
 * <b>&copy; AG, D-49326 Melle 2010</b>
 * <p>
 * <dl>
 * <dt><b>Changes:</b></dt>
 * <dd>09.08.2010 Gro Handle the case, that a new test class is created, as well. Throw
 * Exceptions, Jump to test method after modification</dd>
 * </dl>
 * <p>
 * @author Andreas Groll
 * @version 09.08.2010
 * @since 1.5
 */
public class ModifyTestMethodParticipator {

	/**
	 * Constructor for AddTestMethodParticipator.
	 */
	public ModifyTestMethodParticipator() {

		// Default-Contructor
	}

	/**
	 * Run extension code.
	 * @param context Extension context.
	 * @throws Exception Error.
	 */
	void modifyTestMethod(final IAddTestMethodContext context) throws Exception {

		// Inits
		IMethod testMethod = context.getTestMethod();

		// Testmethode in editor öffnen, sonst funzt die AST-Modifikation nicht
		IEditorPart editorPart;
		if (testMethod != null) {
			editorPart = openMethodInEditor(context.getTestMethod());
		} else {
			editorPart = openCompilationUnitInEditor(context.getTestClass());
		}

		// Kompilationseinheit mit Quelle erstellen
		final ICompilationUnit compilationUnit = context.getTestClass();
		final String compilationSource = compilationUnit.getSource();
		final IDocument sourceDocument = new Document(compilationSource);

		// Erzeuge AST-Wurzel aus ICompilationUnit
		final ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(compilationUnit);
		final CompilationUnit astRoot = (CompilationUnit)astParser.createAST(null);

		// Änderungen ab jetzt aufzeichen
		astRoot.recordModifications();

		// Import zufügen
		addImport(astRoot, "org.testng.Assert.fail", true);

		// Methode, oder Methoden modifizieren
		if (context.isNewTestClassCreated()) {
			IMethod[] methods = compilationUnit.findPrimaryType().getMethods();
			for (IMethod iMethod : methods) {
				if (iMethod.getElementName().startsWith("test")) {
					modifyMethod(astRoot, iMethod);
					jumpToMethod(editorPart, iMethod);
				}
			}
		} else {
			modifyMethod(astRoot, testMethod);
			jumpToMethod(editorPart, testMethod);
		}

		// Änderungen committen
		TextEdit edits = astRoot.rewrite(sourceDocument, compilationUnit.getJavaProject().getOptions(true));
		edits.apply(sourceDocument);
		String newSource = sourceDocument.get();
		compilationUnit.getBuffer().setContents(newSource);
	}

	/**
	 * Modify the test method.
	 * @param astRoot Wurzelknoten.
	 * @param testMethod Test method.
	 */
	private void modifyMethod(final CompilationUnit astRoot, final IMethod testMethod) {

		// Info
		LogHandler.getInstance().handleInfoLog("Modify: " + testMethod.getElementName());

		// Methodendeklaration beschaffen
		MethodDeclaration testMethodDeclaration = findMethodDeclaration(astRoot, testMethod);

		// Astknoten erstellen, der modifiziert werden soll
		AST astToModify = testMethodDeclaration.getAST();

		// Werfen aller Fehler erlauben
		rawListAdd(testMethodDeclaration.thrownExceptions(), astToModify.newSimpleName("Exception"));

		// JavaDoc erzeugen
		Javadoc javaDoc = astToModify.newJavadoc();

		// Beschreibungsfeld
		TagElement tagElement = astToModify.newTagElement();
		rawListAdd(tagElement.fragments(),
			newTextElement(astToModify, getTestMethodCommentDescription(testMethod)));
		rawListAdd(javaDoc.tags(), tagElement);

		// Doku für Werfen aller Fehler
		tagElement = astToModify.newTagElement();
		tagElement.setTagName(TagElement.TAG_THROWS);
		rawListAdd(tagElement.fragments(), astToModify.newSimpleName("Exception"));
		rawListAdd(tagElement.fragments(), newTextElement(astToModify, "Error."));
		rawListAdd(javaDoc.tags(), tagElement);

		// JavaDoc zuweisen
		testMethodDeclaration.setJavadoc(javaDoc);

		// Alle Annotationen entfernen (nicht über Iterator, da Liste geändert wird!)
		removeAnnotations(testMethodDeclaration);

		// Neue Annotation erzeugen
		rawListInsertFirst(testMethodDeclaration.modifiers(), newTestAnnotation(astToModify));
	}

	/**
	 * Fügt eine Importanweisung hinzu.
	 * @param astRoot Wurzel.
	 * @param classToImport Zu importierende Klasse.
	 * @param isStatic Statischer Import?
	 */
	private void addImport(final CompilationUnit astRoot, final String classToImport, final boolean isStatic) {

		// Import bereits vorhanden
		for (Object o : astRoot.imports()) {
			ImportDeclaration i = (ImportDeclaration)o;
			if (i.getName().toString().equals(classToImport)) {
				return;
			}
		}

		// Astknoten erstellen, der modifiziert werden soll
		AST astToModify = astRoot.getAST();

		// Importdeklaration für fail zufügen
		ImportDeclaration importDeclaration = astToModify.newImportDeclaration();
		importDeclaration.setStatic(isStatic);
		importDeclaration.setName(astToModify.newName(classToImport));
		rawListAdd(astRoot.imports(), importDeclaration);
	}

	/**
	 * Removes all annotation declarations.
	 * @param methodDeclaration Method declaration.
	 */
	private void removeAnnotations(final MethodDeclaration methodDeclaration) {

		List<?> modifiers = methodDeclaration.modifiers();
		for (int i = modifiers.size() - 1; i >= 0; i--) {
			Object obj = modifiers.get(i);
			if (obj instanceof Annotation) {
				modifiers.remove(obj);
			}
		}
	}

	/**
	 * Create a new Testannotation.
	 * @param ast Ast-node.
	 * @return Annotation.
	 */
	private Annotation newTestAnnotation(final AST ast) {

		// Stringwert erzeugen
		StringLiteral strg = ast.newStringLiteral();
		strg.setLiteralValue("Standard");

		// Zuweisung erzeugen
		Assignment assignment = ast.newAssignment();
		assignment.setLeftHandSide(ast.newSimpleName("groups"));
		assignment.setOperator(Assignment.Operator.ASSIGN);
		assignment.setRightHandSide(strg);

		// Annotation anlegen
		SingleMemberAnnotation annotation = ast.newSingleMemberAnnotation();
		annotation.setTypeName(ast.newSimpleName("Test"));
		annotation.setValue(assignment);

		// Liefern
		return annotation;
	}

	/**
	 * Get a text element from ast with text.
	 * @param ast AST-Node.
	 * @param text Text.
	 * @return TextElement.
	 */
	private TextElement newTextElement(final AST ast, final String text) {

		TextElement textElement = ast.newTextElement();
		textElement.setText(text);
		return textElement;
	}

	/**
	 * Create the Testmethod JavaDocComment.
	 * @param methodUnderTest Method to Test.
	 * @return JavaDocComment.
	 */
	private String getTestMethodCommentDescription(final IMethod methodUnderTest) {

		// Parameterliste erstellen
		StringBuilder parameterList = new StringBuilder();
		for (String parameter : methodUnderTest.getParameterTypes()) {
			if (parameterList.length() > 0) {
				parameterList.append(", ");
			}

			// Name zufügen, ohne Generics
			String name = Signature.toString(parameter);
			name = name.split("<")[0];
			parameterList.append(name);
		}

		// Kommentar bauen und liefern
		return "Test method for " //+
			+ "{@link " // +
			+ methodUnderTest.getDeclaringType().getFullyQualifiedName() // +
			+ "#" // +
			+ methodUnderTest.getElementName() // +
			+ "(" // +
			+ parameterList // +
			+ ")" //+
			+ "}.";
	}

	/**
	 * Find a method declaration in a compilation unit.
	 * @param astRoot Root of AST.
	 * @param testMethod Testmethod.
	 * @return Method declaration, or <code>null</code> if not found.
	 */
	private MethodDeclaration findMethodDeclaration(final CompilationUnit astRoot, final IMethod testMethod) {

		// Übergabevariable erstellen (Feld, da Variable final sein muss!)
		final MethodDeclaration[] foundMethodDeclaration = new MethodDeclaration[1];
		foundMethodDeclaration[0] = null;

		// Visitor erstellen
		ASTVisitor astVisitor = new ASTVisitor() {

			@Override
			public boolean visit(final MethodDeclaration methodDeclaration) {

				if (methodDeclaration.getName().toString().equals(testMethod.getElementName())) {
					foundMethodDeclaration[0] = methodDeclaration;
				}

				// Wenn wir die Methode gefunden haben, keine weiteren Kinder untersuchen
				return foundMethodDeclaration[0] != null;
			}
		};

		// Visitor anwenden
		astRoot.accept(astVisitor);

		// Liefern
		return foundMethodDeclaration[0];
	}

	/**
	 * Open a java element in editor.
	 * @param method Methode.
	 * @return EditorPart.
	 * @throws JavaModelException Fehler.
	 * @throws PartInitException Fehler.
	 */
	private IEditorPart openMethodInEditor(final IMethod method) throws PartInitException, JavaModelException {

		return JavaUI.openInEditor(method.getDeclaringType().getParent());
	}

	/**
	 * Open a java element in editor.
	 * @param compilationUnit Kompilationseinheit.
	 * @return EditorPart.
	 * @throws JavaModelException Fehler.
	 * @throws PartInitException Fehler.
	 */
	private IEditorPart openCompilationUnitInEditor(final ICompilationUnit compilationUnit)
		throws PartInitException, JavaModelException {

		return JavaUI.openInEditor(compilationUnit.getPrimaryElement());
	}

	/**
	 * Jump to method.
	 * @param editorPart EditorPart.
	 * @param method Method.
	 */
	private void jumpToMethod(final IEditorPart editorPart, final IMethod method) {

		JavaUI.revealInEditor(editorPart, (IJavaElement)method);
	}

	/**
	 * Adds an element to a raw list.
	 * @param list Raw list.
	 * @param obj Element.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void rawListAdd(final List list, final Object obj) {

		list.add(obj);
	}

	/**
	 * Inserts an element to a raw list at the first position.
	 * @param list Raw list.
	 * @param firstObj Element.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void rawListInsertFirst(final List list, final Object firstObj) {

		// Original Listenlänge merken
		int origSize = list.size();

		// Sonderfall: Liste ist leer
		if (origSize == 0) {
			list.add(firstObj);
			return;
		}

		// Die Liste hat nun mindestens ein Element
		int idx = 0;
		Object old = null;
		while (true) {

			// Liste ist durch
			if (idx == origSize) {
				list.add(old);
				break;
			}

			// Umkopieren
			Object idxObj = list.get(idx);
			if (idx == 0) {
				list.set(idx, firstObj);
			} else {
				list.set(idx, old);
			}

			// Index incrementieren
			idx++;
			old = idxObj;
		}
	}
}
