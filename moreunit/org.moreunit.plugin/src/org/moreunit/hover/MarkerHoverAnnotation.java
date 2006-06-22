package org.moreunit.hover;

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


// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.1  2006/02/27 19:55:23  gianasista
// Started hover support
//