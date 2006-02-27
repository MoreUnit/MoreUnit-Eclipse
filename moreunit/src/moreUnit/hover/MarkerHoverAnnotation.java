package moreUnit.hover;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * @author vera
 * 26.02.2006 21:00:00
 */
public class MarkerHoverAnnotation implements IAnnotationHover{

	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		return "Test";
	}

}


// $Log$