package observatory.tests.update;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.tests.internetnlAPI.InternetnlAPI;
import observatory.tests.internetnlAPI.InternetnlAPIException;
import observatory.tests.internetnlAPI.config.TestInfo;
import observatory.tests.internetnlAPI.config.results.TestResult;

/**
 * A class to get information about a running Test and collect the results.
 */
public class RunningTest
{
    /**
     * The timeout to wait for test result.
     */
    private static final long TIMEOUT_MILLIS = 15 * 1000;


    private InternetnlAPI api;

    private String testId;

    private File outputResults;

    private TestResult result;

    /**
     * Create a new RunningTest with the specified testId, output file and API.
     * 
     * @param testId
     * @param outputResults
     * @param api
     */
    public RunningTest(String testId, File outputResults, InternetnlAPI api)
    {
        Objects.requireNonNull(testId);
        Objects.requireNonNull(outputResults);
        Objects.requireNonNull(api);

        this.testId = testId;
        this.api = api;
        this.outputResults = outputResults;
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
            try {
                Thread.sleep(TIMEOUT_MILLIS);
            } catch (Exception e) {}
        }

        this.result = this.api.get(this.testId);
        return this.result;
    }

    /**
     * Dump the result of the test.
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void dump() throws InternetnlAPIException, IOException
    {
        new ObjectMapper().writeValue(this.outputResults, waitFor());
    }
}
