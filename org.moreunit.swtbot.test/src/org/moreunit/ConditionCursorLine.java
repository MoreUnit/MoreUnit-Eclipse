package org.moreunit;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class ConditionCursorLine extends DefaultCondition
{
    private final SWTBotEclipseEditor editor;
    private final int lineNumberOfCursor;

    public ConditionCursorLine(SWTBotEclipseEditor editor, int lineNumberOfCursor)
    {
        this.editor = editor;
        this.lineNumberOfCursor = lineNumberOfCursor;
    }

    @Override
    public boolean test() throws Exception
    {
        return editor.cursorPosition().line == lineNumberOfCursor;
    }

    @Override
    public String getFailureMessage()
    {
        return "Failed to move the cursor in time at line " + lineNumberOfCursor;
    }
}