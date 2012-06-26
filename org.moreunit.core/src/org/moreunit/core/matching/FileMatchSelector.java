package org.moreunit.core.matching;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

public interface FileMatchSelector
{
    IFile select(Collection<IFile> files, IFile preferredFile);
}
