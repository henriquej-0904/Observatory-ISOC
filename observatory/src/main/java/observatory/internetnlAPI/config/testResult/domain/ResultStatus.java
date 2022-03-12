package observatory.internetnlAPI.config.testResult.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the result status of a category or test.
 * 
 * @author Henrique Campos Ferreira
 */
public enum ResultStatus
{
    STATUS_ERROR ("error"),
    STATUS_FAIL ("failed"),
    STATUS_NOTICE ("warning"),
    STATUS_INFO ("info"),
    STATUS_NOT_TESTED ("not_tested"),
    STATUS_SUCCESS ("passed");
    
    
    private String status;

    private ResultStatus(String status)
    {
        this.status = status;
    }

    @JsonValue
    /**
     * Get status.
     * @return
     */
    public String getStatus()
    {
        return this.status;
    }
    
}
