package org.moreunit.codemining;

import static java.util.Collections.emptyList;
import static org.moreunit.core.util.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.core.util.LRUCache;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences.MethodSearchMode;

/**
 * Computes the labels of the "Jump to test/tested ..." {@link JumpCodeMining}s
 * of a compilation unit.
 * <p>
 * All the labels of a compilation unit are computed at once (in particular the
 * corresponding test cases - respectively classes under test - are searched
 * only once for the whole file) and are cached. Since a label does not depend
 * on the code being typed but only on the names of the elements and on the
 * content of the corresponding files, nothing is computed again while the
 * user types: a new computation is only triggered when the structure of the
 * compilation unit changes, when one of the corresponding files changes or
 * when the cached labels expire. Without this, one JDT search (and, for
 * methods without test, one call hierarchy search) was run per method and per
 * code mining refresh.
 * </p>
 */
public class JumpLabelComputer
{
    /**
     * Safety net: cached labels are computed again at the latest after this
     * delay (eg. to notice a test case created in the meantime).
     */
    public static final long DEFAULT_MAX_CACHE_AGE = 30_000;

    private static final int CACHE_SIZE = 8;

    private static final Map<String, CacheEntry> CACHE = new LRUCache<>(CACHE_SIZE);

    private final ICompilationUnit compilationUnit;
    private final Collection<IJavaElement> elements;
    private final long maxCacheAge;

    // created at most once per computer: its search for corresponding types is
    // cached by the facade, whatever the number of elements
    private TypeFacade typeFacade;

    // computed at most once per computer, all elements at once
    private Map<String, String> labels;

    public JumpLabelComputer(ICompilationUnit compilationUnit, Collection<IJavaElement> elements)
    {
        this(compilationUnit, elements, DEFAULT_MAX_CACHE_AGE);
    }

    // visible for testing purposes
    protected JumpLabelComputer(ICompilationUnit compilationUnit, Collection<IJavaElement> elements, long maxCacheAge)
    {
        this.compilationUnit = compilationUnit;
        this.elements = checkNotNull(elements, "Missing elements");
        this.maxCacheAge = maxCacheAge;
    }

    /**
     * Returns the label of the given element (an empty string if it has no
     * corresponding test / class under test), computing the labels of all the
     * elements of the compilation unit at once if they are not cached yet.
     */
    public String labelFor(IJavaElement element)
    {
        return allLabels().getOrDefault(element.getHandleIdentifier(), "");
    }

    private synchronized Map<String, String> allLabels()
    {
        if(labels == null)
        {
            labels = cachedLabelsOrCompute();
        }
        return labels;
    }

    private Map<String, String> cachedLabelsOrCompute()
    {
        final String fingerprint = fingerprint();

        final CacheEntry cached;
        synchronized (CACHE)
        {
            cached = CACHE.get(cacheKey());
        }
        if(cached != null && cached.isStillValid(fingerprint, maxCacheAge))
        {
            return cached.labels;
        }

        final Map<String, String> computedLabels = computeLabels();
        synchronized (CACHE)
        {
            CACHE.put(cacheKey(), new CacheEntry(fingerprint, System.currentTimeMillis(), relatedUnitFingerprints(), computedLabels));
        }
        return computedLabels;
    }

    /**
     * Computes the labels of all the elements of the compilation unit at once.
     */
    protected Map<String, String> computeLabels()
    {
        final Map<String, String> computedLabels = new LinkedHashMap<>();
        if(compilationUnit == null)
        {
            return computedLabels;
        }

        final TypeFacade facade = typeFacade();
        final String testOrTested = facade instanceof TestCaseTypeFacade ? "tested" : "test";

        // one single search for the whole compilation unit (cached by the facade)
        final Collection<IType> correspondingTypes = correspondingTypes();
        final IType classUnderTest = correspondingTypes.isEmpty() ? null : correspondingTypes.iterator().next();

        for (final IJavaElement element : elements)
        {
            if(element instanceof IType)
            {
                computedLabels.put(element.getHandleIdentifier(), correspondingTypes.isEmpty() ? "" : label(testOrTested, "class"));
            }
            else if(element instanceof final IMethod method)
            {
                final boolean jumpable = facade instanceof final ClassTypeFacade classTypeFacade ? hasTestMethod(classTypeFacade, method) //
                    : facade instanceof final TestCaseTypeFacade testCaseTypeFacade && classUnderTest != null && ! testCaseTypeFacade.getCorrespondingTestedMethods(method, classUnderTest).isEmpty();
                computedLabels.put(element.getHandleIdentifier(), jumpable ? label(testOrTested, "method") : "");
            }
        }
        return computedLabels;
    }

    private boolean hasTestMethod(ClassTypeFacade classTypeFacade, IMethod method)
    {
        // the fast path (search by name) avoids running a call hierarchy
        // search for every method already covered by a test method
        return ! classTypeFacade.getCorrespondingTestMethodsByName(method).isEmpty() //
            || ! classTypeFacade.getCorrespondingTestMethods(method, MethodSearchMode.BY_CALL).isEmpty();
    }

    private String label(String testOrTested, String classOrMethod)
    {
        return " Jump to " + testOrTested + " " + classOrMethod;
    }

    private synchronized TypeFacade typeFacade()
    {
        if(typeFacade == null)
        {
            typeFacade = TypeFacade.createFacade(compilationUnit);
        }
        return typeFacade;
    }

    private Collection<IType> correspondingTypes()
    {
        return compilationUnit == null ? emptyList() : typeFacade().getCorrespondingClasses(false);
    }

    private String cacheKey()
    {
        return compilationUnit == null ? "" : compilationUnit.getHandleIdentifier();
    }

    /**
     * A fingerprint of everything the labels depend on, except the content of
     * the corresponding files (see {@link #relatedUnitFingerprints()}) and the
     * preferences (see {@link #DEFAULT_MAX_CACHE_AGE}).
     */
    private String fingerprint()
    {
        final StringBuilder fingerprint = new StringBuilder();
        for (final IJavaElement element : elements)
        {
            fingerprint.append(element.getHandleIdentifier()).append('\n');
        }
        return fingerprint.toString();
    }

    /**
     * Fingerprints of the source of the corresponding test cases / classes
     * under test: their content is what the labels of the methods also depend
     * on.
     */
    private Map<ICompilationUnit, String> relatedUnitFingerprints()
    {
        final Map<ICompilationUnit, String> fingerprints = new LinkedHashMap<>();
        for (final IType correspondingType : correspondingTypes())
        {
            final ICompilationUnit correspondingUnit = correspondingType.getCompilationUnit();
            if(correspondingUnit != null)
            {
                fingerprints.putIfAbsent(correspondingUnit, sourceHashOf(correspondingUnit));
            }
        }
        return fingerprints;
    }

    private static String sourceHashOf(ICompilationUnit unit)
    {
        try
        {
            return unit.exists() ? String.valueOf(unit.getSource().hashCode()) : "<gone>";
        }
        catch (final JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
            return "<unknown>";
        }
    }

    private static class CacheEntry
    {
        private final String fingerprint;
        private final long creationTime;
        private final Map<ICompilationUnit, String> relatedUnitFingerprints;
        private final Map<String, String> labels;

        CacheEntry(String fingerprint, long creationTime, Map<ICompilationUnit, String> relatedUnitFingerprints, Map<String, String> labels)
        {
            this.fingerprint = fingerprint;
            this.creationTime = creationTime;
            this.relatedUnitFingerprints = relatedUnitFingerprints;
            this.labels = labels;
        }

        boolean isStillValid(String currentFingerprint, long maxCacheAge)
        {
            return fingerprint.equals(currentFingerprint) //
                && System.currentTimeMillis() - creationTime < maxCacheAge //
                && relatedUnitFingerprintsUnchanged();
        }

        private boolean relatedUnitFingerprintsUnchanged()
        {
            for (final Map.Entry<ICompilationUnit, String> entry : relatedUnitFingerprints.entrySet())
            {
                if(! entry.getValue().equals(sourceHashOf(entry.getKey())))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
