package observatory.internetnlAPI.config;

/**
 * The type of requests.
 */
public enum RequestType
{
    WEB ("web"),
    MAIL ("mail");

    private String type;

    /**
     * @param type
     */
    private RequestType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }    
}
