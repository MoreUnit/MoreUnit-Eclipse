package org.moreunit.mock.templates;

public class TemplateAlreadyDefinedException extends Exception
{
    private static final long serialVersionUID = - 6594805348873016229L;

    private final String templateId;

    public TemplateAlreadyDefinedException(String templateId)
    {
        super("A template already exists with ID " + templateId);
        this.templateId = templateId;
    }

    public String getTemplateId()
    {
        return templateId;
    }
}
