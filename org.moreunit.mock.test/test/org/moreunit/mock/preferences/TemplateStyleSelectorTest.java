package org.moreunit.mock.preferences;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.model.Category;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;

public class TemplateStyleSelectorTest
{
    @Mock
    private Preferences preferences;
    @Mock
    private MockingTemplateStore templateStore;
    @Mock
    private Logger logger;

    private Display display;
    private Shell shell;
    private boolean headless;

    private Category categoryA;
    private Category categoryB;

    private MockingTemplate templateA1;
    private MockingTemplate templateA2;

    private TemplateStyleSelector selector;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);

        display = Display.getDefault();
        headless = display == null;
        if(headless)
        {
            return;
        }
        display.syncExec(() -> shell = new Shell(display));

        categoryA = new Category("catA", "Cat A");
        categoryB = new Category("catB", "Cat B");

        templateA1 = new MockingTemplate("t1", "catA", "Template A1", null);
        templateA2 = new MockingTemplate("t2", "catA", "Template A2", null);

        // default stubs (overridden when needed)
        when(templateStore.getCategories()).thenReturn(Arrays.asList(categoryA, categoryB));
        when(templateStore.getTemplates(any(Category.class))).thenReturn(emptyList());

        selector = new TemplateStyleSelector(preferences, templateStore, logger);
    }

    @AfterEach
    public void tearDown()
    {
        if(shell != null && ! shell.isDisposed())
        {
            display.syncExec(() -> shell.dispose());
        }
    }

    @Test
    public void should_display_categories_and_default_to_first_template_when_nothing_is_saved()
    {
        if(headless)
        {
            return;
        }

        when(templateStore.getTemplates(categoryA)).thenReturn(Arrays.asList(templateA1, templateA2));

        selector.createContents(shell, null);

        final Combo categoryCombo = findCategoryCombo();
        final Combo templateCombo = findTemplateCombo();

        assertArrayEquals(new String[] { "Cat A", "Cat B" }, categoryCombo.getItems());
        assertEquals(0, categoryCombo.getSelectionIndex());
        assertArrayEquals(new String[] { "Template A1", "Template A2" }, templateCombo.getItems());
        assertEquals(templateA1, selector.getSelectedTemplate());
    }

    @Test
    public void should_select_another_template_of_the_same_category_when_user_selects_it()
    {
        if(headless)
        {
            return;
        }

        when(templateStore.getTemplates(categoryA)).thenReturn(Arrays.asList(templateA1, templateA2));

        selector.createContents(shell, null);

        select(findTemplateCombo(), 1);

        assertEquals(templateA2, selector.getSelectedTemplate());
    }

    @Test
    public void should_select_saved_category_when_created_with_saved_preference()
    {
        if(headless)
        {
            return;
        }

        when(templateStore.getTemplates(categoryA)).thenReturn(Arrays.asList(templateA1, templateA2));
        when(preferences.getMockingTemplate(null)).thenReturn("t2");
        when(templateStore.get("t2")).thenReturn(templateA2);
        when(templateStore.getCategory("catA")).thenReturn(categoryA);

        selector.createContents(shell, null);

        assertEquals(0, findCategoryCombo().getSelectionIndex());
        assertEquals(1, findTemplateCombo().getSelectionIndex());
        assertEquals(templateA2, selector.getSelectedTemplate());
        verify(templateStore, atLeastOnce()).get("t2");
    }

    @Test
    public void should_save_selected_template_when_preferences_are_saved()
    {
        if(headless)
        {
            return;
        }

        when(templateStore.getTemplates(categoryA)).thenReturn(Arrays.asList(templateA1, templateA2));
        when(logger.debugEnabled()).thenReturn(true);

        selector.createContents(shell, null);

        select(findCategoryCombo(), 0);
        select(findTemplateCombo(), 1);

        selector.savePreferences();

        verify(preferences).setMockingTemplate(null, "t2");
        verify(logger).debug(org.mockito.ArgumentMatchers.contains("workspace"));
    }

    @Test
    public void should_not_save_anything_and_warn_when_no_template_is_selected()
    {
        if(headless)
        {
            return;
        }

        selector.createContents(shell, null);

        selector.savePreferences();

        verify(logger).warn("Could not retrieve selected template");
        verify(preferences, never()).setMockingTemplate(any(), any());
    }

    @Test
    public void should_apply_enabled_state_to_combos_when_content_is_created()
    {
        if(headless)
        {
            return;
        }

        selector.setEnabled(false);

        selector.createContents(shell, null);

        assertFalse(findCategoryCombo().getEnabled());
        assertFalse(findTemplateCombo().getEnabled());
    }

    @Test
    public void should_enable_and_disable_combos_when_set_enabled_is_called()
    {
        if(headless)
        {
            return;
        }

        selector.createContents(shell, null);

        selector.setEnabled(false);
        assertFalse(findCategoryCombo().getEnabled());
        assertFalse(findTemplateCombo().getEnabled());

        selector.setEnabled(true);
        assertTrue(findCategoryCombo().getEnabled());
        assertTrue(findTemplateCombo().getEnabled());
    }

    @Test
    public void should_refresh_combos_when_templates_are_reloaded()
    {
        if(headless)
        {
            return;
        }

        final Category categoryC = new Category("catC", "Cat C");
        final MockingTemplate templateC1 = new MockingTemplate("t3", "catC", "Template C1", null);

        selector.createContents(shell, null);

        select(findCategoryCombo(), 0);

        when(templateStore.getCategories()).thenReturn(singletonList(categoryC));
        when(templateStore.getTemplates(categoryC)).thenReturn(singletonList(templateC1));

        selector.reloadTemplates();

        assertArrayEquals(new String[] { "Cat C" }, findCategoryCombo().getItems());
    }

    private void select(Combo combo, int index)
    {
        combo.select(index);
        combo.notifyListeners(SWT.Modify, new Event());
    }

    private static void assertArrayEquals(String[] expected, String[] actual)
    {
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }

    private Combo findCategoryCombo()
    {
        return findCombos().get(0);
    }

    private Combo findTemplateCombo()
    {
        return findCombos().get(1);
    }

    private java.util.List<Combo> findCombos()
    {
        final java.util.List<Combo> combos = new java.util.ArrayList<>();
        collectCombos(shell, combos);
        if(combos.size() < 2)
        {
            throw new AssertionError("Expected two combos, found: " + combos.size());
        }
        return combos;
    }

    private static void collectCombos(Composite composite, java.util.List<Combo> combos)
    {
        for (final Control child : composite.getChildren())
        {
            if(child instanceof final Combo combo)
            {
                combos.add(combo);
            }
            else if(child instanceof final Composite nested)
            {
                collectCombos(nested, combos);
            }
        }
    }
}
