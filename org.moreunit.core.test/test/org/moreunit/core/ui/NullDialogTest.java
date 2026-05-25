package org.moreunit.core.ui;

import org.junit.jupiter.api.Test;

public class NullDialogTest {

    @Test
    public void testOpen() {
        NullDialog dialog = new NullDialog();
        // Since it does nothing, we just verify it doesn't throw any exceptions
        dialog.open();
    }
}
