package net.kolotyluk.loom;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * <h1>Cucumber Test Runner</h1>
 * <p>
 *     <a href="https://kolotyluk.github.io/Cucumber.html">Cucumber</a>
 *     is one of the most popular tools for managing Behaviour Driven Development (BDD), including Behaviour
 *     Testing as part of Test Driven Development (TDD).
 * </p>
 * @see <a href="https://kolotyluk.github.io/Cucumber.html">Cucumber</a>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("net/kolotyluk/loom/Lag.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
// @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "net.kolotyluk.loom.steps.lag")
public class CucumberTestRunner {
}
