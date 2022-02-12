package observatory.internetnlAPI.config.results.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the result status of a category or test.
 */
public enum ResultStatus
{
    STATUS_ERROR ("error"),
    STATUS_FAIL ("failed"),
    STATUS_NOTICE ("warning"),
    STATUS_INFO ("info"),
    STATUS_NOT_TESTED ("not-tested"),
    STATUS_SUCCESS ("passed");
    
    
    private String description;

    private ResultStatus(String description)
    {
        this.description = description;
    }

    @JsonValue
    /**
     * Get the description of this status.
     * @return
     */
    public String getDescription()
    {
        return this.description;
    }
    
}
