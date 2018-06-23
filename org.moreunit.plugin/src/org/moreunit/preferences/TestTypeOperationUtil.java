package org.moreunit.preferences;

import java.util.HashMap;
import java.util.Map;

public class TestTypeOperationUtil
{
    public static final Map BEFORE_CLASS_METHOD_ANNOTATION = new HashMap();
    public static final Map BEFORE_METHOD_ANNOTATION = new HashMap();

    public static final Map TEARDOWN_METHOD_ANNOTATION = new HashMap();
    public static final Map AFTER_CLASS_METHOD_ANNOTATION = new HashMap();

    public static final Map TEST_ANNOTATION = new HashMap();

    static
    {

        BEFORE_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "org.junit.BeforeClass"); //$NON-NLS-1$
        BEFORE_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, "org.junit.jupiter.api.BeforeAll"); //$NON-NLS-1$
        BEFORE_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "org.testng.annotations.BeforeClass"); //$NON-NLS-1$

        BEFORE_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "org.junit.Before"); //$NON-NLS-1$
        BEFORE_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, "org.junit.jupiter.api.BeforeEach"); //$NON-NLS-1$
        BEFORE_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "org.testng.annotations.BeforeMethod"); //$NON-NLS-1$

        TEARDOWN_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "org.junit.After"); //$NON-NLS-1$
        TEARDOWN_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, "org.junit.jupiter.api.AfterEach"); //$NON-NLS-1$
        TEARDOWN_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "org.testng.annotations.AfterMethod"); //$NON-NLS-1$

        AFTER_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "org.junit.AfterClass"); //$NON-NLS-1$
        AFTER_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, "org.junit.jupiter.api.AfterAll"); //$NON-NLS-1$
        AFTER_CLASS_METHOD_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "org.testng.annotations.AfterClass"); //$NON-NLS-1$

        TEST_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "org.junit.Test"); //$NON-NLS-1$
        TEST_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, "org.junit.jupiter.api.Test"); //$NON-NLS-1$
        TEST_ANNOTATION.put(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "org.testng.annotations.Test"); //$NON-NLS-1$
    }

    public static boolean supportTestAnnotation(String testType)
    {
        return TEST_ANNOTATION.containsKey(testType);
    }
}
