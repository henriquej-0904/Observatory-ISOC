package observatory.internetnlAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.TestInfo;
import observatory.internetnlAPI.config.testResult.TestResult;
import observatory.util.InvalidFormatException;
import observatory.util.Util;

/**
 * Represents the available operations to interact with the Internet.nl API.
 * 
 * @author Henrique Campos Ferreira
 */
public interface InternetnlAPI extends AutoCloseable
{
    /**
     * Submits a list of domains for testing.
     * 
     * @param domains - An excel file that contains the list of domains.
     * @param sheetName - The name of the sheet in the domains file.
     * @param type - The type of test to perform.
     * 
     * @return Information about the test.
     * 
     * @throws IOException If an error occured while reading the domains file.
     * @throws InvalidFormatException If the file is not a workbook or it is corrupted.
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    default TestInfo submit(File domains, String sheetName, RequestType type)
        throws IOException, InvalidFormatException, InternetnlAPIException
    {
        String[] domainsList = null;
        try
        (
            InputStream inputStream = new FileInputStream(domains);
            Workbook workbook = Util.openWorkbook(inputStream);
        )
        {
            domainsList = Util.getDomainsList(workbook, sheetName, type);
        }

        return submit(sheetName, domainsList, type);
    }

    /**
     * Submits a list of domains for testing.
     * 
     * @param name - The name of the test.
     * @param domains - A list of domains to test.
     * @param type - The type of test to perform.
     * 
     * @return Information about the test.
     * 
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestInfo submit(String name, String[] domains, RequestType type)
        throws InternetnlAPIException;

    /**
     * Get the status of a submitted test.
     * 
     * @param requestId - The id of the test.
     * 
     * @return Information about the specified test.
     * 
     * @throws TestIdNotFoundException If the specified id does not exist.
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestInfo status(String requestId) throws TestIdNotFoundException, InternetnlAPIException;

    /**
     * Get the results of a completed test.
     * 
     * @param requestId - The id of the test.
     * 
     * @return The results of the specified test.
     * 
     * @throws TestIdNotFoundException If the specified id does not exist.
     * @throws InternetnlAPIException In case of an error while executing the operation.
     */
    TestResult get(String requestId) throws TestIdNotFoundException, InternetnlAPIException;
}
