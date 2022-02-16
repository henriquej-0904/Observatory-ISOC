package observatory.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.internetnlAPI.config.results.TestResult;

/**
 * Represents a Test of a List with it's results.
 */
public class ListTest
{
    private String name;

    private TestResult results;

    /**
     * @param name
     * @param results
     */
    public ListTest(String name, TestResult results) {
        this.name = name;
        this.results = results;
    }

    public ListTest(File results) throws IOException
    {
        try
        {
            this.name = results.getName();
            this.results = new ObjectMapper().readValue(results, TestResult.class);
        } catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
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
