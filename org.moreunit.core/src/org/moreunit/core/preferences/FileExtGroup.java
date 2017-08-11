package org.moreunit.core.preferences;

import static org.moreunit.core.util.Strings.countOccurrences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.ui.Composites;
import org.moreunit.core.ui.ExpandableCompositeContainer;
import org.moreunit.core.ui.ExpandableCompositeContainer.ExpandableContent;
import org.moreunit.core.ui.FileNamePatternDemo;
import org.moreunit.core.ui.Labels;
import org.moreunit.core.ui.LayoutData;

public class FileExtGroup implements GenericPreferencesGroup
{
    private static final int EXPLANATION_WIDTH_HINT = 350;

    private final ExpandableCompositeContainer container;
    private final LanguagePreferencesWriter prefWriter;
    private final Composite fileExtGroup;
    private ExtensionField testFileExt;
    private ExtensionField srcFileExt;
    private Button checkBox;

    public FileExtGroup(Composite parent, ExpandableCompositeContainer container, LanguagePreferencesWriter prefsWriter)
    {
        this.container = container;
        this.prefWriter = prefsWriter;

        this.fileExtGroup = Composites.gridGroup(parent, "Rule for file extensions:", 2, 10);
    }

    public void createContents()
    {
        createExtensionFields(fileExtGroup);
    }
    
    private void createExtensionFields(Composite parent)
    {
        Boolean extEnable = prefWriter.getExtEnable();   
        
        checkBox = new Button(parent,SWT.CHECK);
                
        Label label = new Label(parent, SWT.NONE);
        label.setText("Source file extension:");
        srcFileExt = new ExtensionField(parent, SWT.SINGLE | SWT.BORDER);
        srcFileExt.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getSrcFileExt().length() != 0)
        {
            srcFileExt.setText(prefWriter.getSrcFileExt());
        }
        else
        {
            srcFileExt.setText("");
            
        }

        label = new Label(parent, SWT.NONE);
        label.setText("Test file extension:");

        testFileExt = new ExtensionField(parent, SWT.SINGLE | SWT.BORDER);
        testFileExt.setLayoutData(LayoutData.labelledField());

        if(prefWriter.getTestFileExt().length() != 0)
        {
            testFileExt.setText(prefWriter.getTestFileExt());
        }
        else
        {
            testFileExt.setText("");
            
        }

        testFileExt.setEnabled(extEnable);
        srcFileExt.setEnabled(extEnable);
        checkBox.setSelection(extEnable);
        
        checkBox.setText("Heterogen file extensions (if unchecked the same file extension for source and test files assumed");
        checkBox.setLayoutData(LayoutData.fillRow());
        
        checkBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                testFileExt.setEnabled(btn.getSelection());
                srcFileExt.setEnabled(btn.getSelection());
            }
        });        
    }


    public void forceFocus()
    {
        checkBox.setFocus();
    }

    public String getError()
    {
        String errorMsg = null;

        if(checkBox.getSelection())
        {        
            if(0 == srcFileExt.getField().getText().length()
               && 0 == testFileExt.getField().getText().length())
            {
                errorMsg = "Please enter file extensions.";
            }
            
            if(0 == srcFileExt.getField().getText().length()
                || 0 == testFileExt.getField().getText().length())
             {
                 errorMsg = "Please enter a file extension for test files and source files.";
             }        
            
            if(!(srcFileExt.isValid() && testFileExt.isValid()))
            {
                errorMsg = "Please enter the file extension in the format \"[.]extension\"";
            }
        }

        return errorMsg;
    }

    public String getWarning()
    {
        String warrningMsg = null;        
        if(checkBox.getSelection())
        {  
            if(srcFileExt.getField().getText().equals(testFileExt.getField().getText()))
            {
                warrningMsg = "If you want to use the same file extension you can uncheck this group.";
            }           
        }
            
        return warrningMsg;
    }

    public void saveProperties()
    {
        prefWriter.setFileExts(srcFileExt.getExtension(), testFileExt.getExtension());
        prefWriter.setExtEnable(checkBox.getSelection());
    }

    public void setEnabled(boolean enabled)
    {
        testFileExt.setEnabled(enabled);
        srcFileExt.setEnabled(enabled);
        container.setExpandable(enabled);
    }

    public void restoreDefaults()
    {
        testFileExt.setText("");
        srcFileExt.setText("");
    }
    
    public void addModifyListener(ModifyListener listener)
    {
        srcFileExt.addModifyListener(listener);
        testFileExt.addModifyListener(listener);        
      
        checkBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                // we trigger the textfield modifylistener because we cant use ModifyListener on the checkbox 
                srcFileExt.setText(srcFileExt.getExtension() + "");
            }
        });

    }
}
