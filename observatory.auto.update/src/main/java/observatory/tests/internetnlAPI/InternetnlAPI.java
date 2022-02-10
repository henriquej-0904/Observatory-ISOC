package observatory.tests.internetnlAPI;

import java.io.File;

import observatory.tests.internetnlAPI.config.RequestType;
import observatory.tests.internetnlAPI.config.TestInfo;
import observatory.tests.internetnlAPI.config.results.TestResult;

/**
 * Represents the available operations to interact with the Internet.nl API.
 */
public interface InternetnlAPI
{
    /**
     * Submits a list of domains for testing.
     * 
     * @param domains - An excel file that contains the list of domains.
     * @param sheetName - The name of the sheet in the domains file.
     * @param requestType - The type of test to perform.
     * 
     * @return Information about the test.
     * 
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestInfo submit(File domains, String sheetName, RequestType requestType)
        throws InternetnlAPIException;

    /**
     * Get the status of a submitted test.
     * 
     * @param requestId - The id of the test.
     * 
     * @return Information about the specified test.
     * 
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestInfo status(String requestId) throws InternetnlAPIException;

    /**
     * Get the results of a completed test.
     * 
     * @param requestId - The id of the test.
     * 
     * @return The results of the specified test.
     * 
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestResult get(String requestId) throws InternetnlAPIException;
}
