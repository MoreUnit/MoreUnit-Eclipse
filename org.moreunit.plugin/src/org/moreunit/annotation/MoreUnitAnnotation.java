package org.moreunit.annotation;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * @author vera 01.02.2009 16:43:52
 */
public class MoreUnitAnnotation extends Annotation
{

    private final Position position;

    public static final String ANNOTATION_ID = "org.moreunit.testCaseAnnotation";
    public static final String ANNOTATION_ID_IGNORED = "org.moreunit.testCaseAnnotationIgnoredMethod";
    
    public static MoreUnitAnnotation createAnnotationForTestedMethod(ISourceRange range)
    {
        return new MoreUnitAnnotation(ANNOTATION_ID, range);
    }
    
    public static MoreUnitAnnotation createAnnotationForIgnoredTesMethod(ISourceRange range)
    {
        return new MoreUnitAnnotation(ANNOTATION_ID_IGNORED, range);
    }
    
    private MoreUnitAnnotation(String id, ISourceRange range)
    {
        super(id, false, null);
        position = new Position(range.getOffset(), range.getLength());
    }

    public Position getPosition()
    {
        return position;
    }
}
