package observatory.tests;

import observatory.internetnlAPI.config.results.TestResult;

/**
 * Represents a Test of a List of domains with it's results.
 */
public class ListTest
{
    private final String name;

    private final TestResult results;

    /**
     * @param name
     * @param results
     */
    public ListTest(String name, TestResult results) {
        this.name = name;
        this.results = results;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the results
     */
    public TestResult getResults() {
        return results;
    }
}
