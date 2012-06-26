package org.moreunit.core.ui;

import static java.util.Arrays.asList;
import static org.moreunit.core.util.Strings.ucFirst;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.preferences.LayoutData;
import org.moreunit.core.util.StringConstants;

public abstract class FileNamePatternDemo
{
    private static final List<String> WORD_POOL = asList("foo", "bar");

    private Text inputField;

    public void createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Button creationButton = new Button(composite, SWT.NONE);
        creationButton.setText("Test ");

        Label label = new Label(composite, SWT.NONE);
        label.setText(" your pattern with the following file:");

        inputField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        inputField.setLayoutData(LayoutData.LABEL_AND_FIELD);

        final Label fileTypeLbl = new Label(composite, SWT.NONE);
        GridData data = new GridData(GridData.FILL, GridData.FILL, false, true);
        data.horizontalSpan = 3;
        fileTypeLbl.setLayoutData(data);

        label = new Label(composite, SWT.NONE);
        label.setText("MoreUnit will make it correspond to any file named as follows:");
        label.setLayoutData(data);

        final Text outputArea = new Text(composite, SWT.NONE);
        outputArea.setEditable(false);
        outputArea.setLayoutData(data);

        creationButton.addSelectionListener(new SelectionAdapter()
        {
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

    protected String createOutput(FileNameEvaluation evaluation)
    {
        StringBuilder sb = new StringBuilder();
        for (String p : evaluation.getPreferredCorrespondingFilePatterns())
        {
            if(sb.length() != 0)
            {
                sb.append(StringConstants.NEWLINE);
            }
            sb.append(p);
        }
        for (String p : evaluation.getOtherCorrespondingFilePatterns())
        {
            sb.append(StringConstants.NEWLINE).append(p);
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
