package observatory.internetnlAPI.config.testResult.domain;

/**
 * @author Henrique Campos Ferreira
 */
public class DomainResults
{
    private String status;

    private Report report;

    private Scoring scoring;

    private Results results;

    /**
     * 
     */
    public DomainResults() {
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the report
     */
    public Report getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * @return the scoring
     */
    public Scoring getScoring() {
        return scoring;
    }

    /**
     * @param scoring the scoring to set
     */
    public void setScoring(Scoring scoring) {
        this.scoring = scoring;
    }

    /**
     * @return the results
     */
    public Results getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(Results results) {
        this.results = results;
    }
}
