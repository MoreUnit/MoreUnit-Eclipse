package org.moreunit.mock.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.moreunit.core.util.Strings;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;

import com.google.inject.Inject;

public class TemplateStyleSelector implements SelectionListener
{
    private static final int COMBO_WIDTH_CORRECTION_MARGIN = 4;

    private final Preferences preferences;
    private final MockingTemplateStore templateStore;
    private final Logger logger;
    private final List<Category> categories = new ArrayList<Category>();
    private final List<MockingTemplate> categoryTemplates = new ArrayList<MockingTemplate>();

    private IJavaProject project;
    private PixelConverter pixelConverter;
    private Combo categoryCombo;
    private Combo templateCombo;
    private boolean contentCreated;
    private boolean enabled;

    @Inject
    public TemplateStyleSelector(Preferences preferences, MockingTemplateStore templateStore, Logger logger)
    {
        this.preferences = preferences;
        this.templateStore = templateStore;
        this.logger = logger;

        categories.addAll(templateStore.getCategories());
        Collections.sort(categories);

        contentCreated = false;
        enabled = true;
    }

    public void createContents(Composite parent, IJavaProject project)
    {
        this.project = project;
        this.pixelConverter = new PixelConverter(parent);

        Composite labelAndFieldComposite = new Composite(parent, SWT.NONE);
        labelAndFieldComposite.setLayout(new GridLayout(3, false));

        GridData rowLayoutData = new GridData();
        rowLayoutData.heightHint = 30;
        labelAndFieldComposite.setLayoutData(rowLayoutData);

        Label label = new Label(labelAndFieldComposite, SWT.NONE);
        label.setText("Mock style:");

        categoryCombo = createCombo(labelAndFieldComposite);
        categoryCombo.setItems(categoryNames());
        adaptSize(categoryCombo);

        templateCombo = createCombo(labelAndFieldComposite);
        templateCombo.setItems(new String[] { "Please select a category first..." });
        adaptSize(templateCombo);

        categoryCombo.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent event)
            {
                Category category = categories.get(categoryCombo.getSelectionIndex());

                categoryTemplates.clear();
                categoryTemplates.addAll(templateStore.getTemplates(category));
                Collections.sort(categoryTemplates);

                templateCombo.setItems(templateNames());
                templateCombo.select(0);
            }
        });

        initValues();

        contentCreated = true;

        initStyle();
    }

    private Combo createCombo(Composite parent)
    {
        Combo combo = new Combo(parent, SWT.SINGLE | SWT.BORDER);
        combo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
        return combo;
    }

    private String[] categoryNames()
    {
        String[] categoryNames = new String[categories.size()];
        int i = 0;
        for (Category category : categories)
        {
            categoryNames[i++] = category.name();
        }
        return categoryNames;
    }

    private void adaptSize(Combo combo)
    {
        int biggestWidth = 0;
        for (String item : combo.getItems())
        {
            if(item.length() > biggestWidth)
            {
                biggestWidth = item.length();
            }
        }
        ((GridData) combo.getLayoutData()).widthHint = pixelConverter.convertWidthInCharsToPixels(biggestWidth + COMBO_WIDTH_CORRECTION_MARGIN);
    }

    private String[] templateNames()
    {
        String[] templateNames = new String[categoryTemplates.size()];
        int i = 0;
        for (MockingTemplate template : categoryTemplates)
        {
            templateNames[i++] = template.name();
        }
        return templateNames;
    }

    private void initValues()
    {
        String mockingTemplateId = preferences.getMockingTemplate(project);
        if(Strings.isBlank(mockingTemplateId))
        {
            categoryCombo.select(0);
            templateCombo.select(0);
        }
        else
        {
            MockingTemplate mockingTemplate = templateStore.get(mockingTemplateId);
            Category category = templateStore.getCategory(mockingTemplate.categoryId());
            categoryCombo.select(categories.indexOf(category));
            templateCombo.select(categoryTemplates.indexOf(mockingTemplate));
        }
    }

    private void initStyle()
    {
        categoryCombo.setEnabled(enabled);
        templateCombo.setEnabled(enabled);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        if(contentCreated)
        {
            initStyle();
        }
    }

    public void savePreferences()
    {
        int selectionIndex = templateCombo.getSelectionIndex();
        // it may happens that the first entry was not automatically selected...
        if(selectionIndex == - 1)
        {
            selectionIndex = 0;
        }

        MockingTemplate selectedTemplate = categoryTemplates.get(selectionIndex);
        preferences.setMockingTemplate(project, selectedTemplate.id());

        if(logger.debugEnabled())
        {
            logger.debug(String.format("Defined template %s for %s", selectedTemplate.id(), project == null ? "workspace" : "project " + project.getElementName()));
        }
    }

    public void widgetDefaultSelected(SelectionEvent event)
    {
        // nothing to do
    }

    public void widgetSelected(SelectionEvent event)
    {
        // nothing to do
    }
}
