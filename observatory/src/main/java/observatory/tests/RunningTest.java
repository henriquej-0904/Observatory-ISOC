package observatory.tests;

import java.util.Objects;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIException;
import observatory.internetnlAPI.config.TestInfo;
import observatory.internetnlAPI.config.testResult.TestResult;

/**
 * A class to get information about a running Test and collect the results.
 * 
 * @author Henrique Campos Ferreira
 */
public class RunningTest
{
    /**
     * The timeout to wait for test result.
     */
    private static final long TIMEOUT_MILLIS = 30 * 1000;


    private final InternetnlAPI api;

    private final String testId;

    private TestResult result;

    /**
     * Create a new RunningTest with the specified testId and API.
     * 
     * @param testId
     * @param api
     */
    public RunningTest(String testId, InternetnlAPI api)
    {
        Objects.requireNonNull(testId);
        Objects.requireNonNull(api);

        this.testId = testId;
        this.api = api;
    }

    /**
     * Get the status of this Test.
     * @return The status.
     * @throws InternetnlAPIException
     */
    public TestInfo getStatus() throws InternetnlAPIException
    {
        return this.api.status(this.testId);
    }
    
    /**
     * Checks if the test has finished.
     * @return True if it has finished.
     * @throws InternetnlAPIException
     */
    public boolean finished() throws InternetnlAPIException
    {
        return getStatus().getRequest().getFinished_date() != null;
    }

    /**
     * Waits for this running test to finish.
     * @return The result of the test.
     * @throws InternetnlAPIException
     */
    public TestResult waitFor() throws InternetnlAPIException
    {
        if (this.result != null)
            return this.result;

        while (!finished())
        {
            sleep(TIMEOUT_MILLIS);
        }

        sleep(5 * 1000);
        this.result = this.api.get(this.testId);
        return this.result;
    }

    /**
     * @return the testId
     */
    public String getTestId() {
        return testId;
    }

    private void sleep(long time)
    {
        try {
            Thread.sleep(time);
        } catch (Exception e) {}
    }
}
