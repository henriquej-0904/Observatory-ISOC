package observatory.internetnlAPI.config.testResult.domain;

/**
 * Represents a result of a test or category.
 */
public class Result
{
    private ResultStatus status;

    private String verdict;

    /**
     * 
     */
    public Result() {
    }

    /**
     * @return the status
     */
    public ResultStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    /**
     * @return the verdict
     */
    public String getVerdict() {
        return verdict;
    }

    /**
     * @param verdict the verdict to set
     */
    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }
}
