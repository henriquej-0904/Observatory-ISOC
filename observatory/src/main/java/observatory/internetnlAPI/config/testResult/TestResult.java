package observatory.internetnlAPI.config.testResult;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.internetnlAPI.config.InternetnlRequest;
import observatory.internetnlAPI.config.testResult.domain.DomainResults;
import observatory.util.InvalidFormatException;

/**
 * Represents the results of a test.
 * 
 * @author Henrique Campos Ferreira
 */
public class TestResult
{
    private String api_version;

    private InternetnlRequest request;

    /**
     * Map results for each domain.
     * This map should be ordered by key (the domain) and the order must be
     * equal to the list of submitted domains.
     */
    private LinkedHashMap<String, DomainResults> domains;

    /**
     * 
     */
    public TestResult() {
    }

    //#region Load & Save

    /**
     * Save the results to a file.
     * 
     * @param output - The output file.
     * 
     * @throws IOException
     */
    public void save(File output) throws IOException
    {
        new ObjectMapper().writeValue(output, this);
    }

    /**
     * Load a test result from a file.
     * @param input - The file that contains the results.
     * @return The Test Result.
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static TestResult fromFile(File input) throws IOException, InvalidFormatException
    {
        try {
            return new ObjectMapper().readValue(input, TestResult.class);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFormatException(e);
        }
    }

    //#endregion

    //#region Getters & Setters

    /**
     * @return the api_version
     */
    public String getApi_version() {
        return api_version;
    }

    /**
     * @param api_version the api_version to set
     */
    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    /**
     * @return the request
     */
    public InternetnlRequest getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(InternetnlRequest request) {
        this.request = request;
    }

    /**
     * Get the domains results.
     * This map is ordered by key (the domain) and the order is
     * equal to the list of submitted domains.
     * @return The domains results.
     */
    public LinkedHashMap<String, DomainResults> getDomains() {
        return domains;
    }

    /**
     * @param domains the domains to set
     */
    public void setDomains(LinkedHashMap<String, DomainResults> domains) {
        this.domains = domains;
    }

    //#endregion

}
