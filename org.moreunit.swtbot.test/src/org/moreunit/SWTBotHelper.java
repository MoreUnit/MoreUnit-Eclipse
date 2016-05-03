package org.moreunit;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class SWTBotHelper
{

    
    public static void forceSWTBotShellsRecomputeNameCache(SWTBot bot)
    {
        //WORKAROUND due to an SWTBot bug when shell title is updated afterwards
        bot.shells();
    }
}
