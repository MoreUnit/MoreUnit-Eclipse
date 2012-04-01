package org.moreunit.core.expressions;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class CommandSupportedExtensionsProvider extends AbstractSourceProvider
{
    public static final String JUMP_COMMAND_SUPPORTED_EXTENSIONS = "org.moreunit.core.commands.jumpCommand";

    public static final List<String> SOURCES = unmodifiableList(asList(JUMP_COMMAND_SUPPORTED_EXTENSIONS));

    public static final Map<String, String> VALUES = new HashMap<String, String>();
    static
    {
        VALUES.put(JUMP_COMMAND_SUPPORTED_EXTENSIONS, "js");
    }

    public void dispose()
    {
    }

    public Map<String, String> getCurrentState()
    {
        return new HashMap<String, String>(VALUES);
    }

    public String[] getProvidedSourceNames()
    {
        return SOURCES.toArray(new String[SOURCES.size()]);
    }

    public void updateSupportedExtensions(String commandId, String extensions)
    {
        VALUES.put(commandId, extensions);
        fireSourceChanged(ISources.WORKBENCH, commandId, extensions);
    }
}
