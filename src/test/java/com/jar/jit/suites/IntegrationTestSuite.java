package com.jar.jit.suites;

import com.jar.jit.controller.AccountControllerIT;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

//@Suite
@SuiteDisplayName("Full integration test suite")
@SelectClasses( AccountControllerIT.class )
@IncludeTags({"basic", "full"})
public class IntegrationTestSuite {
}
