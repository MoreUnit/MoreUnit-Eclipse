package org.moreunit.mock.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.TextEdit;

public class SourceFormatter
{
    public String getFormattedSource(ICompilationUnit compilationUnit) throws BadLocationException, JavaModelException
    {
        final CodeFormatter formatter = ToolFactory.createCodeFormatter(compilationUnit.getJavaProject().getOptions(true));

        final IDocument document = new Document(compilationUnit.getSource());
        final String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
        final TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), 0, document.getLength(), 0, lineDelimiter);

        if(edit != null)
        {
            edit.apply(document);
        }

        return document.get();
    }
}
