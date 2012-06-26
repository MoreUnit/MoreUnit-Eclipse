package org.moreunit.core.languages;

import static org.moreunit.core.util.Preconditions.checkArgument;

import org.moreunit.core.util.Strings;

public class Language implements Comparable<Language>
{
    private final String extension;
    private final String label;

    public Language(String extension, String label)
    {
        checkArgument(! Strings.isBlank(extension), "Invalid extension: " + extension);
        this.extension = extension.trim();
        this.label = label != null ? label : extension;
    }

    public Language(String extension)
    {
        this(extension, extension);
    }

    public String getExtension()
    {
        return extension;
    }

    public String getLabel()
    {
        return label;
    }

    public int compareTo(Language o)
    {
        return label.compareTo(o.label);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extension == null) ? 0 : extension.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Language other = (Language) obj;
        if(extension == null)
        {
            if(other.extension != null)
            {
                return false;
            }
        }
        else if(! extension.equals(other.extension))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("%s[.%s]", label, extension);
    }
}
