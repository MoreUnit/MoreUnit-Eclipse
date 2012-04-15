package org.moreunit.core.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils
{
    public static void closeQuietly(Closeable... closeables)
    {
        if(closeables == null)
        {
            return;
        }

        for (Closeable closeable : closeables)
        {
            if(closeable != null)
            {
                try
                {
                    closeable.close();
                }
                catch (IOException e)
                {
                    // ignored
                }
            }
        }
    }
}
