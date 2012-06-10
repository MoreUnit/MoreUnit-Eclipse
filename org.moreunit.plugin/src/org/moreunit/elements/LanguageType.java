package org.moreunit.elements;

import org.eclipse.core.runtime.IPath;

public enum LanguageType
{
    JAVA("java"), GROOVY("groovy"), UNKNOWN(null);

    private final String extension;

    private LanguageType(String extension)
    {
        this.extension = extension;
    }

    public static LanguageType forPath(IPath path)
    {
        return forExtension(path.getFileExtension());
    }

    public static LanguageType forExtension(String ext)
    {
        for (LanguageType l : values())
        {
            if(l.extension != null && l.extension.equals(ext))
            {
                return l;
            }
        }
        return UNKNOWN;
    }

    public String getExtension()
    {
        return extension;
    }
}
