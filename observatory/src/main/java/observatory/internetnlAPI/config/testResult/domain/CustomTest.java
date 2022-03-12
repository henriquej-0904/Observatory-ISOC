package observatory.internetnlAPI.config.testResult.domain;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents custom tests.
 * 
 * @author Henrique Campos Ferreira
 */
public enum CustomTest
{

    TLS_1_3_SUPPORT ("tls_1_3_support", "TLS 1.3 Support",
        (obj) -> obj.equals("yes") ? ResultStatus.STATUS_SUCCESS : ResultStatus.STATUS_FAIL),

    /**
     * This field is represented in the batch API by mail_non_sending_domain and it has a
     * boolean value associated with it. To avoid confusion (double negation), this field is represented
     * in the Excel report as E-mail sending domain, so the result of this custom
     * test should be interpreted as the negation of the original value.
     */
    MAIL_SENDING_DOMAIN ("mail_non_sending_domain", "E-mail sending domain",
        (obj) -> !(Boolean)obj ? ResultStatus.STATUS_SUCCESS : ResultStatus.STATUS_FAIL),

    MAIL_SERVER_TESTABLE ("mail_servers_testable_status", "Mail server testable",
        (obj) -> obj.equals("ok") ? ResultStatus.STATUS_SUCCESS : ResultStatus.STATUS_FAIL);


    private String test, description;

    private Function<Object, ResultStatus> convertToResultFunc;

    private CustomTest(String test, String description, Function<Object, ResultStatus> convertToResultFunc)
    {
        this.test = test;
        this.description = description;
        this.convertToResultFunc = convertToResultFunc;
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

    /**
     * Get the conversion to result function.
     * @return The conversion to result function.
     */
    public Function<Object, ResultStatus> getConvertToResultFunc()
    {
        return convertToResultFunc;
    }

    /**
     * Converts the specified result object to a Result.
     * 
     * @param result - The result object to convert.
     * @return The converted Result.
     */
    public ResultStatus convertToResult(Object result)
    {
        return convertToResultFunc.apply(result);
    }
}
