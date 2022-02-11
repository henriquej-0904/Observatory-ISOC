package observatory.internetnlAPI.config;

/**
 * Represents information about a Test in the internet.nl API.
 */
public class TestInfo
{
    private String api_version;

    private InternetnlRequest request;

    /**
     * 
     */
    public TestInfo() {
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
}
