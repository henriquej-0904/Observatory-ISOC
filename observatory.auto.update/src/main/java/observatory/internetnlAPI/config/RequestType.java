package observatory.internetnlAPI.config;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of requests.
 */
public enum RequestType
{
    WEB ("web"),
    MAIL ("mail"),
    ALL ("all");

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
    @JsonValue
    public String getType() {
        return type;
    }    
}
