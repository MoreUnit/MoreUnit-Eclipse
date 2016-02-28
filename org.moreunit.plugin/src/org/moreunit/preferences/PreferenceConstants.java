package org.moreunit.preferences;

import org.moreunit.core.matching.TestFileNamePattern;

public interface PreferenceConstants
{
    String PREFERENCES_VERSION = "org.moreunit.preferences.version";
    int CURRENT_PREFERENCES_VERSION = 2;

    String NL = System.getProperty("line.separator");

    String PREF_PAGE_ID = "org.moreunit.moreunitPreferencePage";
    String PREF_DETAILS_PAGE_ID = "org.moreunit.moreunitPreferencePage_details";

    String USE_PROJECT_SPECIFIC_SETTINGS = "org.moreunit.useprojectsettings";
    String PREF_JUNIT_PATH = "org.moreunit.junit_path";
    String PREF_JUNIT_PATH_DEFAULT = "test";
    String SHOW_REFACTORING_DIALOG = "org.moreunit.show_refactoring_dialog";

    String SWITCH_TO_MATCHING_METHOD = "org.moreunit.switch_to_matching_method";
    String TEST_PACKAGE_PREFIX = "org.moreunit.package_prefix";
    String TEST_PACKAGE_SUFFIX = "org.moreunit.package_suffix";
    String UNIT_SOURCE_FOLDER = "org.moreunit.unitsourcefolder";
    String EXTENDED_TEST_METHOD_SEARCH = "org.moreunit.extendedTestMethodSearch";
    String ENABLE_TEST_METHOD_SEARCH_BY_NAME = "org.moreunit.enableTestMethodSearchByName";
    String TEST_CLASS_NAME_TEMPLATE = "org.moreunit.testClassNameTemplate";
    String GENERATE_COMMENTS_FOR_TEST_METHOD = "org.moreunit.generateCommentsForTestMethod";

    String TEST_TYPE = "org.moreunit.test_type";
    String TEST_TYPE_VALUE_JUNIT_3 = "junit3";
    String TEST_TYPE_VALUE_JUNIT_4 = "junit4";
    String TEST_TYPE_VALUE_TESTNG = "testng";
    String TEST_SUPERCLASS = "org.moreunit.test_superclass";

    boolean DEFAULT_GENERATE_COMMENTS_FOR_TEST_METHOD = false;
    boolean DEFAULT_SWITCH_TO_MATCHING_METHOD = true;
    String DEFAULT_TEST_PACKAGE_PREFIX = "";
    String DEFAULT_TEST_PACKAGE_SUFFIX = "";
    String DEFAULT_TEST_TYPE = PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;
    String DEFAULT_TEST_SUPERCLASS = "";
    boolean DEFAULT_EXTENDED_TEST_METHOD_SEARCH = true;
    boolean DEFAULT_ENABLE_TEST_METHOD_SEARCH_BY_NAME = true;
    String DEFAULT_TEST_CLASS_NAME_TEMPLATE = TestFileNamePattern.SRC_FILE_VARIABLE + "Test";
    String DEFAULT_TEST_METHOD_DEFAULT_CONTENT = "throw new RuntimeException(\"not yet implemented\");";
    String DEFAULT_TEST_ANNOTATION_MODE = "OFF";

    String TEXT_GENERAL_SETTINGS = "General settings for your unit tests (they can then be refined for each project):";
    String TEXT_TEST_SUPERCLASS = "Test superclass:";
    String TEXT_TEST_METHOD_CONTENT = "Test method content:";
    String TEXT_PACKAGE_SUFFIX = "Test package suffix:";
    String TEXT_PACKAGE_PREFIX = "Test package prefix:";
    String TEXT_TEST_TYPE = "Test Type";
    String TEXT_TEST_NG = "TestNG";
    String TEXT_JUNIT_4 = "Junit 4";
    String TEXT_JUNIT_3_8 = "JUnit 3.8";
    String TEXT_TEST_METHOD_TYPE = "Use test-prefix for test-methods (e.g. testFoo())";
    String TEXT_TEST_SOURCE_FOLDER = "Test source folder:";
    String TEXT_EXTENDED_TEST_METHOD_SEARCH = "Enable test method search \"by call\"";
    String TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME = "Enable test method search \"by name\"";
    String TEXT_GENERATE_COMMENTS_FOR_TEST_METHOD = "Generate comments for test methods";
    String TEXT_ANNOTATION_MODE = "Annotate tested methods";
    String TEST_ANNOTATION_MODE_DISABLED = "Disabled";
    String TEST_ANNOTATION_MODE_BY_NAME = "Search by method name";
    String TEST_ANNOTATION_MODE_EXTENDED_SEARCH = "Also search by call (not recommended)";

    String TOOLTIP_TEST_SOURCE_FOLDER = "Enter the name of the source folder that usually contains your test sources (examples: junit, test, src/test/java). It may be the same as your production code.";
    String TOOLTIP_TEST_METHOD_CONTENT = "Write here any content that you would like to be inserted in your test methods when created by MoreUnit.";
    String TOOLTIP_PACKAGE_SUFFIX = "If your test classes use the same package as the classes that are tested except for a suffix part, enter this suffix here." + NL + NL + "Example: by specifying \"test\", a test class for a class located in the package \"org.example\" would be searched in the package \"org.example.test\".";
    String TOOLTIP_PACKAGE_PREFIX = "If your test classes use the same package as the classes that are tested except for a prefix part, enter this prefix here." + NL + NL + "Example: by specifying \"test\", a test class for a class located in the package \"org.example\" would be searched in the package \"test.org.example\".";
    String TOOLTIP_TEST_SUPERCLASS = "If you want your test classes to have a default superclass, enter its fully qualified name here.";
    String TOOLTIP_EXTENDED_TEST_METHOD_SEARCH = "Enable this option if you want MoreUnit to propose jumping from a given method to any test method that calls it (and vice-versa).";
    String TOOLTIP_ENABLE_TEST_METHOD_SEARCH_BY_NAME = "Check this option if you want MoreUnit to blindly propose jumping from a given method to a test method with the same name, even though the test method does not call the method under test.";
    String TOOLTIP_TEST_ANNOTATION_EXTENDED_SEARCH = "This option will cause conflicts if other parts of Eclipse are searching for method method calls at the same time, which occurs often. Only activate if you know what you are doing.";

    String TEST_METHOD_TYPE = "org.moreunit.test_methodType";
    String TEST_METHOD_TYPE_JUNIT3 = "testMethodTypeJunit3";
    String TEST_METHOD_TYPE_NO_PREFIX = "testMethodTypeNoPrefix";
    String TEST_METHOD_DEFAULT_CONTENT = "org.moreunit.test.test_methodDefaultContent";

    String TEST_ANNOTATION_MODE = "org.moreunit.test_annotation";

    interface Deprecated
    {
        String PREFIXES = "org.moreunit.prefixes";
        String SUFFIXES = "org.moreunit.suffixes";
        String FLEXIBEL_TESTCASE_NAMING = "org.moreunit.flexiblenaming";
    }
}
