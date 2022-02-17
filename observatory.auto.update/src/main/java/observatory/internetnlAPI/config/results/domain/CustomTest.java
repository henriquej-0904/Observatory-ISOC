package observatory.internetnlAPI.config.results.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents custom tests.
 */
public enum CustomTest
{

    TLS_1_3_SUPPORT ("tls_1_3_support", "TLS 1.3 Support"),

    /**
     * This field is represented in the batch API by mail_non_sending_domain and it has a
     * boolean value associated with it. To avoid confusion (double negation), this field is represented
     * in the Excel report as E-mail sending domain, so the result of this custom
     * test should be interpreted as the negation of the original value.
     */
    MAIL_NON_SENDING_DOMAIN ("mail_non_sending_domain", "E-mail sending domain"),
    MAIL_SERVER_TESTABLE ("mail_servers_testable_status", "Mail server testable");


    private String test, description;

    private CustomTest(String test, String description)
    {
        this.test = test;
        this.description = description;
    }

    /**
     * @return the test
     */
    @JsonValue
    public String getTest() {
        return test;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
