package org.moreunit.navigation;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A history to navigate back and forward between the locations visited when
 * jumping between classes under tests and test cases (and vice versa), the same
 * way a web browser allows to navigate back and forward in the visited pages.
 */
public class JumpNavigationHistory
{

    private static final int HISTORY_SIZE = 50;

    private static class ThreadSafeInstanceHolder
    {
        private static final JumpNavigationHistory INSTANCE = new JumpNavigationHistory();
    }

    private final Deque<JumpLocation> previousLocations = new ArrayDeque<>();
    private final Deque<JumpLocation> nextLocations = new ArrayDeque<>();

    public static JumpNavigationHistory getInstance()
    {
        return ThreadSafeInstanceHolder.INSTANCE;
    }

    /**
     * Registers a jump from a location to another one: the next
     * {@link #goBack(JumpLocation)} will bring the user back to the location
     * (s)he jumped from.
     */
    public synchronized void registerJump(JumpLocation fromLocation, JumpLocation toLocation)
    {
        if(fromLocation == null || fromLocation.equals(toLocation))
        {
            return;
        }

        if(! fromLocation.equals(previousLocations.peekLast()))
        {
            previousLocations.addLast(fromLocation);
            while(previousLocations.size() > HISTORY_SIZE)
            {
                previousLocations.removeFirst();
            }
        }

        // a new jump invalidates the "forward" history, as with web browsers
        nextLocations.clear();
    }

    /**
     * Returns the location to go back to (and remembers the given current
     * location in order to allow to {@link #goForward(JumpLocation)} afterwards),
     * or null if there is no previous location.
     */
    public synchronized JumpLocation goBack(JumpLocation currentLocation)
    {
        return go(previousLocations, nextLocations, currentLocation);
    }

    /**
     * Returns the location to go forward to (and remembers the given current
     * location in order to allow to {@link #goBack(JumpLocation)} afterwards),
     * or null if there is no next location.
     */
    public synchronized JumpLocation goForward(JumpLocation currentLocation)
    {
        return go(nextLocations, previousLocations, currentLocation);
    }

    private JumpLocation go(Deque<JumpLocation> source, Deque<JumpLocation> opposite, JumpLocation currentLocation)
    {
        if(source.isEmpty())
        {
            return null;
        }

        final JumpLocation location = source.removeLast();
        if(currentLocation != null && ! currentLocation.equals(location))
        {
            opposite.addLast(currentLocation);
        }
        return location;
    }

    /**
     * Tells whether some previously visited location can be returned to.
     */
    public synchronized boolean canGoBack()
    {
        return ! previousLocations.isEmpty();
    }

    /**
     * Tells whether some location visited before going back can be returned to.
     */
    public synchronized boolean canGoForward()
    {
        return ! nextLocations.isEmpty();
    }

    /**
     * Forgets all registered jumps.
     */
    public synchronized void clear()
    {
        previousLocations.clear();
        nextLocations.clear();
    }
}
