package observatory.internetnlAPI.config.results;

import java.util.Map;

import observatory.internetnlAPI.config.InternetnlRequest;
import observatory.internetnlAPI.config.results.domain.DomainResults;

public class TestResult
{
    private String api_version;

    private InternetnlRequest request;

    /**
     * Map results for each domain.
     */
    private Map<String, DomainResults> domains;

    /**
     * 
     */
    public TestResult() {
    }

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
     * @return the domains
     */
    public Map<String, DomainResults> getDomains() {
        return domains;
    }

    /**
     * @param domains the domains to set
     */
    public void setDomains(Map<String, DomainResults> domains) {
        this.domains = domains;
    }

    
}
