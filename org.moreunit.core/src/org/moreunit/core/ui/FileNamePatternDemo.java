package org.moreunit.core.ui;

import static java.util.Arrays.asList;
import static org.moreunit.core.util.Strings.ucFirst;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.util.StringConstants;

public abstract class FileNamePatternDemo
{
    private static final List<String> WORD_POOL = asList("foo", "bar");

    private Text inputField;

    public void createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(LayoutData.fillRow());

        Link testLink = Composites.link(composite, "Test");

        Label label = new Label(composite, SWT.NONE);
        label.setText("your pattern with the following file:");

        inputField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        inputField.setLayoutData(LayoutData.labelledField());

        final Text fileTypeLbl = outputArea(composite);

        label = new Label(composite, SWT.NONE);
        label.setText("MoreUnit will make it correspond to any file named as follows:");
        label.setLayoutData(LayoutData.fillRow());

        final Text outputArea = outputArea(composite);

        testLink.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                TestFileNamePattern pattern = getPattern();
                String fileName = inputField.getText().trim();

                if(fileName.length() == 0)
                {
                    inputField.setText(generateSourceFileName(pattern));
                }

                FileNameEvaluation evaluation = pattern.evaluate(fileName);

                fileTypeLbl.setText(fileName + " is a " + (evaluation.isTestFile() ? "test" : "source") + " file");

                outputArea.setText(createOutput(evaluation));
                sizeChanged();
            }
        });
    }

    private Text outputArea(Composite parent)
    {
        final Text t = new Text(parent, SWT.BORDER | SWT.MULTI);
        t.setEditable(false);
        t.setLayoutData(LayoutData.fillRow());
        t.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        return t;
    }

    private String createOutput(FileNameEvaluation evaluation)
    {
        StringBuilder sb = new StringBuilder();
        for (String p : evaluation.getAllCorrespondingFileEclipsePatterns())
        {
            if(sb.length() != 0)
            {
                sb.append(StringConstants.NEWLINE);
            }
            sb.append(p);
        }
        return sb.toString();
    }

    protected abstract TestFileNamePattern getPattern();

    protected abstract void sizeChanged();

    public static String generateSourceFileName(TestFileNamePattern pattern)
    {
        Iterator<String> words = WORD_POOL.iterator();

        String separator = pattern.getSeparator();

        if(separator.length() == 0)
        {
            return ucFirst(words.next()) + ucFirst(words.next());
        }
        return words.next() + separator + words.next();
    }

    public void patternChanged()
    {
        inputField.setText(generateSourceFileName(getPattern()));
    }
}
