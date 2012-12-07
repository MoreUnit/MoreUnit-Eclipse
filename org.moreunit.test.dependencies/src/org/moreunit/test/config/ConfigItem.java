package org.moreunit.test.config;

public class ConfigItem<T>
{
    private boolean overridden;
    private T item;

    public static <T> ConfigItem<T> useDefault()
    {
        return new ConfigItem<T>();
    }

    private ConfigItem()
    {
    }

    public void overrideWith(T item)
    {
        overridden = true;
        this.item = item;
    }

    public boolean isOverridden()
    {
        return overridden;
    }

    public T get()
    {
        return item;
    }
}
