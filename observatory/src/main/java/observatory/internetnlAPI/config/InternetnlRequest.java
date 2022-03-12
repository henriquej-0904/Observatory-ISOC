package observatory.internetnlAPI.config;

/**
 * Represents a Test request in the Internetnl API.
 * 
 * @author Henrique Campos Ferreira
 */
public class InternetnlRequest
{
    private String request_id, name, status,
        submit_date, finished_date;

    private RequestType request_type;

    /**
     * 
     */
    public InternetnlRequest() {
    }

    /**
     * @return the request_id
     */
    public String getRequest_id() {
        return request_id;
    }

    /**
     * @param request_id the request_id to set
     */
    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the request_type
     */
    public RequestType getRequest_type() {
        return request_type;
    }

    /**
     * @param request_type the request_type to set
     */
    public void setRequest_type(RequestType request_type) {
        this.request_type = request_type;
    }

    /**
     * @return the submit_date
     */
    public String getSubmit_date() {
        return submit_date;
    }

    /**
     * @param submit_date the submit_date to set
     */
    public void setSubmit_date(String submit_date) {
        this.submit_date = submit_date;
    }

    /**
     * @return the finished_date
     */
    public String getFinished_date() {
        return finished_date;
    }

    /**
     * @param finished_date the finished_date to set
     */
    public void setFinished_date(String finished_date) {
        this.finished_date = finished_date;
    }
}
