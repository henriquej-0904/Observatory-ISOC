package observatory.internetnlAPI.config.results.domain;

/**
 * Represents custom results.
 */
public class CustomResults
{
    private String tls_1_3_support;

    /**
     * 
     */
    public CustomResults() {
    }

    /**
     * @return the tls_1_3_support
     */
    public String getTls_1_3_support() {
        return tls_1_3_support;
    }

    /**
     * @param tls_1_3_support the tls_1_3_support to set
     */
    public void setTls_1_3_support(String tls_1_3_support) {
        this.tls_1_3_support = tls_1_3_support;
    }
}
