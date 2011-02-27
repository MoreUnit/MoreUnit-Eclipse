package org.moreunit.mock.templates;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.templates.Template;
import org.moreunit.mock.model.Part;

/**
 * Wrapper for Eclipse {@link Template}s.
 */
public class EclipseTemplate
{
    private final Part part;
    private final Template eclipseTemplate;

    public EclipseTemplate(Part part, String pattern) throws MockingTemplateException
    {
        this.part = part;
        this.eclipseTemplate = newTemplate(EclipseTemplateContext.CONTEXT_KEY, "", EclipseTemplateContext.CONTEXT_TYPE, pattern, false);
    }

    // extracted as documentation for new Template(...)
    private Template newTemplate(String name, String description, String contextType, String pattern, boolean autoInsertable)
    {
        return new Template(name, description, contextType, pattern, autoInsertable);
    }

    public int getInsertionOffset(MockingContext context) throws JavaModelException, MockingTemplateException
    {
        return part.getInsertionOffset(context);
    }

    public Template template()
    {
        return eclipseTemplate;
    }

    public Part part()
    {
        return part;
    }
}
