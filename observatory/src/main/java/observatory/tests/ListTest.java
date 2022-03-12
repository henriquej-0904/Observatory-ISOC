package observatory.tests;

import observatory.internetnlAPI.config.testResult.TestResult;

/**
 * Represents a Test of a List of domains with it's results.
 * 
 * @author Henrique Campos Ferreira
 */
public class ListTest
{
    private final String name;

    private final TestResult results;

    public static ListTest from(TestResult results)
    {
        return new ListTest(results.getRequest().getName(), results);
    }

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
