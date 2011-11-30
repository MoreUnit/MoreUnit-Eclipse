package org.moreunit.preferences;

public interface PreferenceConstants
{
    String NL = System.getProperty("line.separator");

    String PREF_PAGE_ID = "org.moreunit.moreunitPreferencePage";
    String PREF_DETAILS_PAGE_ID = "org.moreunit.moreunitPreferencePage_details";

    String USE_PROJECT_SPECIFIC_SETTINGS = "org.moreunit.useprojectsettings";
    String PREF_JUNIT_PATH = "org.moreunit.junit_path";
    String PREF_JUNIT_PATH_DEFAULT = "junit";
    String SHOW_REFACTORING_DIALOG = "org.moreunit.show_refactoring_dialog";

    String PREFIXES = "org.moreunit.prefixes";
    String SUFFIXES = "org.moreunit.suffixes";
    String USE_WIZARDS = "org.moreunit.use_wizards";
    String SWITCH_TO_MATCHING_METHOD = "org.moreunit.switch_to_matching_method";
    String TEST_PACKAGE_PREFIX = "org.moreunit.package_prefix";
    String TEST_PACKAGE_SUFFIX = "org.moreunit.package_suffix";
    String FLEXIBEL_TESTCASE_NAMING = "org.moreunit.flexiblenaming";
    String UNIT_SOURCE_FOLDER = "org.moreunit.unitsourcefolder";
    String EXTENDED_TEST_METHOD_SEARCH = "org.moreunit.extendedTestMethodSearch";

    String TEST_TYPE = "org.moreunit.test_type";
    String TEST_TYPE_VALUE_JUNIT_3 = "junit3";
    String TEST_TYPE_VALUE_JUNIT_4 = "junit4";
    String TEST_TYPE_VALUE_TESTNG = "testng";
    String TEST_SUPERCLASS = "org.moreunit.test_superclass";

    boolean DEFAULT_CREATE_TESTNG = false;
    String DEFAULT_QUALIFIERS = "Test";
    boolean DEFAULT_USE_WIZARDS = true;
    boolean DEFAULT_SWITCH_TO_MATCHING_METHOD = true;
    String DEFAULT_TEST_PACKAGE_PREFIX = "";
    String DEFAULT_TEST_PACKAGE_SUFFIX = "";
    boolean DEFAULT_FLEXIBLE_TESTCASE_NAMING = false;
    String DEFAULT_TEST_TYPE = PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
    String DEFAULT_PRAEFIX = "Test";
    String DEFAULT_SUFFIX = "Test";
    String DEFAULT_TEST_SUPERCLASS = "";
    boolean DEFAULT_EXTENDED_TEST_METHOD_SEARCH = false;

    String TEXT_GENERAL_SETTINGS = "General settings for your unit tests (they can then be refined for each project):";
    String TEXT_FLEXIBLE_NAMING = "Enable flexible naming of test classes";
    String TEXT_TEST_SUPERCLASS = "Test superclass:";
    String TEXT_TEST_METHOD_CONTENT = "Test method content:";
    String TEXT_PACKAGE_SUFFIX = "Test package suffix:";
    String TEXT_PACKAGE_PREFIX = "Test package prefix:";
    String TEXT_TEST_SUFFIXES = "Test suffixes (comma separated):";
    String TEXT_TEST_PREFIXES = "Test prefixes (comma separated):";
    String TEXT_TEST_TYPE = "Test Type";
    String TEXT_TEST_NG = "TestNG";
    String TEXT_JUNIT_4 = "Junit 4";
    String TEXT_JUNIT_3_8 = "JUnit 3.8";
    String TEXT_TEST_METHOD_TYPE = "Use test-prefix for test-methods (e.g. testFoo())";
    String TEXT_TEST_SOURCE_FOLDER = "Test source folder:";
    String TEXT_EXTENDED_TEST_METHOD_SEARCH = "Enable extended search for test methods";

    String TOOLTIP_TEST_SOURCE_FOLDER = "Enter the name of the source folder that usually contains your test sources (examples: junit, test, src/test/java). It may be the same as your production code.";
    String TOOLTIP_TEST_METHOD_CONTENT = "Write here any content that you would like to be inserted in your test methods when created by MoreUnit.";
    String TOOLTIP_PACKAGE_SUFFIX = "If your test classes use the same package as the classes that are tested except for a suffix part, enter this suffix here." + NL + NL + "Example: by specifying \"test\", a test class for a class located in the package \"org.example\" would be searched in the package \"org.example.test\".";
    String TOOLTIP_PACKAGE_PREFIX = "If your test classes use the same package as the classes that are tested except for a prefix part, enter this prefix here." + NL + NL + "Example: by specifying \"test\", a test class for a class located in the package \"org.example\" would be searched in the package \"test.org.example\".";
    String TOOLTIP_TEST_SUFFIXES = "Enter here the suffix(es) that you usually use to name a test class for a given production class, if any." + NL + NL + "Example: by specifying \"Test,Spec\", both \"CustomerTest\" and \"CustomerSpec\" would be considered as valid test classes for the class \"Customer\".";
    String TOOLTIP_TEST_PREFIXES = "Enter here the preffix(es) that you usually use to name a test class for a given production class, if any." + NL + NL + "Example: by specifying \"Test,Spec\", both \"TestProduct\" and \"SpecProduct\" would be considered as valid test classes for the class \"Product\".";
    String TOOLTIP_TEST_SUPERCLASS = "If you want your test classes to have a default superclass, enter its fully qualified name here.";
    String TOOLTIP_FLEXIBLE_NAMING = "Enable this option if you want MoreUnit to search for test classes that introduce some variable part in their name between the test prefix or suffix and the name of the class under test." + NL + NL + "Example: both \"ItemProductionTest\" and \"ItemConsumptionTest\" would be considered as valid test classes for the class \"Item\" (assuming you are using the suffix \"Test\" for your test classes).";
    String TOOLTIP_EXTENDED_TEST_METHOD_SEARCH = "Enable this option if you want MoreUnit to propose jumping from a given method to any test method that calls it (and vice-versa) instead of juste searching for a test method with the same name.";

    String TEST_METHOD_TYPE = "org.moreunit.test_methodType";
    String TEST_METHOD_TYPE_JUNIT3 = "testMethodTypeJunit3";
    String TEST_METHOD_TYPE_NO_PREFIX = "testMethodTypeNoPrefix";
    String TEST_METHOD_DEFAULT_CONTENT = "org.moreunit.test.test_methodDefaultContent";
}
