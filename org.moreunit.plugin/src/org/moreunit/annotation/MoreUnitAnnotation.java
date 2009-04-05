package org.moreunit.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * @author vera 01.02.2009 16:43:52
 */
public class MoreUnitAnnotation extends Annotation
{

    private final Position position;

    private static final String ANNOTATION_ID = "org.moreunit.testCaseAnnotation";

    public MoreUnitAnnotation(int offset, int length)
    {
        super(ANNOTATION_ID, false, null);
        position = new Position(offset, length);
    }

    public Position getPosition()
    {
        return position;
    }
}
