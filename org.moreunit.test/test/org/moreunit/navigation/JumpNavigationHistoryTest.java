package org.moreunit.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JumpNavigationHistoryTest
{
    private JumpNavigationHistory history;

    private final ICompilationUnit someClass = mock(ICompilationUnit.class);
    private final ICompilationUnit someClassTest = mock(ICompilationUnit.class);
    private final ICompilationUnit otherClass = mock(ICompilationUnit.class);

    @BeforeEach
    public void setUp()
    {
        history = new JumpNavigationHistory();
    }

    private JumpLocation locationIn(ICompilationUnit compilationUnit)
    {
        return new JumpLocation(compilationUnit, 12);
    }

    @Test
    public void getInstance_should_return_singleton()
    {
        assertSame(JumpNavigationHistory.getInstance(), JumpNavigationHistory.getInstance());
    }

    @Test
    public void goBack_should_return_null_when_no_jump_was_registered()
    {
        assertFalse(history.canGoBack());
        assertNull(history.goBack(locationIn(someClass)));
    }

    @Test
    public void goForward_should_return_null_when_no_jump_was_made()
    {
        assertFalse(history.canGoForward());
        assertNull(history.goForward(locationIn(someClass)));
    }

    @Test
    public void goBack_should_return_the_location_jumped_from()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));

        assertTrue(history.canGoBack());
        assertEquals(locationIn(someClass), history.goBack(locationIn(someClassTest)));
    }

    @Test
    public void goForward_should_return_the_location_jumped_back_from()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));
        history.goBack(locationIn(someClassTest));

        assertTrue(history.canGoForward());
        assertEquals(locationIn(someClassTest), history.goForward(locationIn(someClass)));
    }

    @Test
    public void goBack_should_allow_to_go_back_and_forward_alternatively()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));

        assertEquals(locationIn(someClass), history.goBack(locationIn(someClassTest)));
        assertEquals(locationIn(someClassTest), history.goForward(locationIn(someClass)));
        assertEquals(locationIn(someClass), history.goBack(locationIn(someClassTest)));
    }

    @Test
    public void goBack_should_return_the_last_jumped_from_location_first()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));
        history.registerJump(locationIn(someClassTest), locationIn(otherClass));

        assertEquals(locationIn(someClassTest), history.goBack(locationIn(otherClass)));
        assertEquals(locationIn(someClass), history.goBack(locationIn(someClassTest)));
        assertFalse(history.canGoBack());
    }

    @Test
    public void registerJump_should_reset_the_forward_history()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));
        history.goBack(locationIn(someClassTest));
        assertTrue(history.canGoForward());

        history.registerJump(locationIn(someClass), locationIn(otherClass));

        assertFalse(history.canGoForward());
        assertNull(history.goForward(locationIn(someClass)));
    }

    @Test
    public void registerJump_should_ignore_unknown_or_unchanged_location()
    {
        history.registerJump(null, locationIn(someClassTest));
        history.registerJump(locationIn(someClass), locationIn(someClass));

        assertFalse(history.canGoBack());
    }

    @Test
    public void registerJump_should_not_register_twice_the_same_location_jumped_from()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));
        history.registerJump(locationIn(someClass), locationIn(otherClass));

        assertEquals(locationIn(someClass), history.goBack(locationIn(otherClass)));
        assertFalse(history.canGoBack());
    }

    @Test
    public void goBack_should_not_remember_unknown_current_location()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));

        assertEquals(locationIn(someClass), history.goBack(null));

        assertFalse(history.canGoForward());
    }

    @Test
    public void clear_should_forget_all_locations()
    {
        history.registerJump(locationIn(someClass), locationIn(someClassTest));
        history.goBack(locationIn(someClassTest));

        history.clear();

        assertFalse(history.canGoBack());
        assertFalse(history.canGoForward());
    }
}
