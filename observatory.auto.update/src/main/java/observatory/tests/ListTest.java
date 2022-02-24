package observatory.tests;

import observatory.internetnlAPI.config.results.TestResult;

/**
 * Represents a Test of a List of domains with it's results.
 */
public class ListTest
{
    private String name;

    private TestResult results;

    private String[] testedDomains;
    
    /**
     * 
     */
    public ListTest() {
    }

    /**
     * @param name
     * @param results
     */
    public ListTest(String name, TestResult results, String[] testedDomains) {
        this.name = name;
        this.results = results;
        this.testedDomains = testedDomains;
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

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param results the results to set
     */
    public void setResults(TestResult results) {
        this.results = results;
    }

    /**
     * @return the testedDomains
     */
    public String[] getTestedDomains() {
        return testedDomains;
    }

    /**
     * @param testedDomains the testedDomains to set
     */
    public void setTestedDomains(String[] testedDomains) {
        this.testedDomains = testedDomains;
    }
}
