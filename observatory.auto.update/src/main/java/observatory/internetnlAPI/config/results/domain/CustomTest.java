package observatory.internetnlAPI.config.results.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents custom tests.
 */
public enum CustomTest
{

    TLS_1_3_SUPPORT ("tls_1_3_support", "TLS 1.3 Support"),

    MAIL_SENDING_DOMAIN ("mail_non_sending_domain", "E-mail sending domain"),
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
