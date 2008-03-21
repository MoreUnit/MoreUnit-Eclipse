package org.moreunit.preferences;

import org.eclipse.core.runtime.QualifiedName;

public interface PreferenceConstants {

	String PREF_PAGE_ID							= "org.moreunit.moreunitPreferencePage";
	String PREF_DETAILS_PAGE_ID					= "org.moreunit.moreunitPreferencePage_details";

	QualifiedName USE_PROJECT_SPECIFIC_SETTINGS		= new QualifiedName("org.moreunit", "useprojectsettings");
	String PREF_JUNIT_PATH 						= "org.moreunit.junit_path";
	String PREF_JUNIT_PATH_DEFAULT 				= "junit";
	String SHOW_REFACTORING_DIALOG 				= "org.moreunit.show_refactoring_dialog";

	String	PREFIXES							= "org.moreunit.prefixes";
	String	SUFFIXES							= "org.moreunit.suffixes";
	String	USE_WIZARDS							= "org.moreunit.use_wizards";
	String	SWITCH_TO_MATCHING_METHOD			= "org.moreunit.switch_to_matching_method";
	String	TEST_PACKAGE_PREFIX					= "org.moreunit.package_prefix";
	String	TEST_PACKAGE_SUFFIX					= "org.moreunit.package_suffix";
	String	FLEXIBEL_TESTCASE_NAMING			= "org.moreunit.flexiblenaming";
	String UNIT_SOURCE_FOLDER					= "org.moreunit.unitsourcefolder"; 

	String TEST_TYPE							= "org.moreunit.test_type";
	String TEST_TYPE_VALUE_JUNIT_3				= "junit3";
	String TEST_TYPE_VALUE_JUNIT_4				= "junit4";
	String TEST_TYPE_VALUE_TESTNG				= "testng";
	String TEST_SUPERCLASS						= "org.moreunit.test_superclass";

	boolean DEFAULT_CREATE_TESTNG				= false;
	String	DEFAULT_QUALIFIERS					= "Test";
	boolean	DEFAULT_USE_WIZARDS					= true;
	boolean	DEFAULT_SWITCH_TO_MATCHING_METHOD	= true;
	String	DEFAULT_TEST_PACKAGE_PREFIX			= "";
	String	DEFAULT_TEST_PACKAGE_SUFFIX			= "";
	boolean DEFAULT_FLEXIBLE_TESTCASE_NAMING	= false;
	String	DEFAULT_TEST_TYPE					= PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
	String 	DEFAULT_PRAEFIX						= "test";
	String 	DEFAULT_SUFFIX						= "Test";
	String	DEFAULT_TEST_SUPERCLASS				= "";
	
	String TEXT_FLEXIBLE_NAMING = "Enable flexible naming of tests";
	String TEXT_TEST_SUPERCLASS = "Test superclass";
	String TEXT_PACKAGE_SUFFIX = "Test package suffix";
	String TEXT_PACKAGE_PREFIX = "Test package prefix";
	String TEXT_TEST_SUFFIXES = "Test &Suffixes (comma separated):";
	String TEXT_TEST_PREFIXES = "Test &Prefixes (comma separated):";
	String TEXT_TEST_TYPE = "Test Type";
	String TEXT_TEST_NG = "TestNG";
	String TEXT_JUNIT_4 = "Junit 4";
	String TEXT_JUNIT_3_8 = "JUnit 3.8";
}
