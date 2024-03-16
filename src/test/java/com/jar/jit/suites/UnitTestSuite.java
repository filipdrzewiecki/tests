package com.jar.jit.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Basic integration test suite")
@SelectPackages("com.jar.jit")
public class UnitTestSuite {
}
