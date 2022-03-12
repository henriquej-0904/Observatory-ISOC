package observatory.internetnlAPI.config;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of requests.
 * 
 * @author Henrique Campos Ferreira
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
    @JsonValue
    public String getType() {
        return type;
    } 
    
    /**
     * Parse a type.
     * @param type
     * @return The type.
     * 
     * @throws IllegalArgumentException if an illegal type is specified.
     */
    public static RequestType parseType(String type)
    {
        switch (type.toUpperCase()) {
            case "WEB":
                return RequestType.WEB;
            case "MAIL":
                return RequestType.MAIL;
        
            default:
                throw new IllegalArgumentException("Illegal type.");
        }
    }
}
