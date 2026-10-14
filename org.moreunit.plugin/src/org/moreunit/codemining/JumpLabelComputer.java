package org.moreunit.codemining;

import static java.util.Collections.emptyList;
import static org.moreunit.core.util.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * The searches for the corresponding tests and classes under test: not
     * in the common pool of the JVM, which a slow search must not exhaust for
     * the rest of Eclipse, and with a few threads, which stop when idle.
     */
    private static final ExecutorService EXECUTOR = newExecutor();

    /** The computations running, see {@link #computation(String)} */
    private static final Map<String, CompletableFuture<Map<String, String>>> COMPUTATIONS = new HashMap<>();

    // computed at most once per computer, all elements at once
    private CompletableFuture<Map<String, String>> labels;

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
        try
        {
            return label(element).join();
        }
        catch (final CompletionException e)
        {
            // what computing the labels threw, as when it was computed in this thread
            if(e.getCause() instanceof final RuntimeException cause)
            {
                throw cause;
            }
            if(e.getCause() instanceof final Error cause)
            {
                throw cause;
            }
            throw e;
        }
    }

    /**
     * Returns the label of the given element, as {@link #labelFor} does, once
     * the labels of all the elements are computed: no thread waits for them
     * meanwhile, whatever the number of elements.
     */
    public CompletableFuture<String> label(IJavaElement element)
    {
        final String handle = element.getHandleIdentifier();
        return allLabels().thenApply(computed -> computed.getOrDefault(handle, ""));
    }

    private synchronized CompletableFuture<Map<String, String>> allLabels()
    {
        // computed again after a failure, at the next refresh
        if(labels == null || labels.isCompletedExceptionally())
        {
            final String fingerprint = fingerprint();
            // the labels in the cache are answered at once, without waiting
            // behind the searches of other compilation units
            final Map<String, String> cached = cachedLabels(fingerprint);
            labels = cached != null ? CompletableFuture.completedFuture(cached) : computation(fingerprint);
        }
        return labels;
    }

    /**
     * Returns the computation of the labels, shared by the computers of the
     * same compilation unit and elements: each refresh of the minings creates
     * a computer, while one of a previous refresh may still be searching.
     */
    private CompletableFuture<Map<String, String>> computation(String fingerprint)
    {
        final String key = cacheKey() + '\n' + fingerprint;
        synchronized (COMPUTATIONS)
        {
            final CompletableFuture<Map<String, String>> running = COMPUTATIONS.get(key);
            if(running != null)
            {
                return running;
            }
            final CompletableFuture<Map<String, String>> computation = CompletableFuture.supplyAsync(() -> computeAndCache(fingerprint), EXECUTOR);
            COMPUTATIONS.put(key, computation);
            computation.whenComplete((computed, failure) -> {
                synchronized (COMPUTATIONS)
                {
                    COMPUTATIONS.remove(key, computation);
                }
                if(failure != null)
                {
                    LogHandler.getInstance().handleExceptionLog("Could not compute the jump code minings", failure);
                }
            });
            return computation;
        }
    }

    /**
     * Stops the threads that compute the labels, as MoreUnit stops.
     */
    public static void shutdown()
    {
        EXECUTOR.shutdownNow();
    }

    private static ExecutorService newExecutor()
    {
        final AtomicInteger count = new AtomicInteger();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), runnable -> {
            final Thread thread = new Thread(runnable, "MoreUnit code minings " + count.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * @return the labels in the cache if they are still valid, else
     *         <code>null</code>
     */
    private Map<String, String> cachedLabels(String fingerprint)
    {
        final CacheEntry cached;
        synchronized (CACHE)
        {
            cached = CACHE.get(cacheKey());
        }
        return cached != null && cached.isStillValid(fingerprint, maxCacheAge) ? cached.labels : null;
    }

    private Map<String, String> computeAndCache(String fingerprint)
    {
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
