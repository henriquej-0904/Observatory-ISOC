package observatory.internetnlAPI.config.results.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import observatory.internetnlAPI.config.RequestType;

/**
 * Represents custom tests.
 */
public enum CustomTest
{

    TLS_1_3_SUPPORT ("tls_1_3_support", "TLS 1.3 Support", RequestType.ALL),

    MAIL_SENDING_DOMAIN ("mail_non_sending_domain", "E-mail sending domain", RequestType.MAIL),
    MAIL_SERVER_TESTABLE ("mail_servers_testable_status", "Mail server testable", RequestType.MAIL);


    private static Map<RequestType, List<CustomTest>> valuesByType;

    private String test, description;

    private RequestType type;

    private CustomTest(String test, String description, RequestType type)
    {
        this.test = test;
        this.description = description;
        this.type = type;
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
     * @return the type
     */
    public RequestType getType() {
        return type;
    }

    public static List<CustomTest> values(RequestType type)
    {
        if (valuesByType == null)
        {
            CustomTest[] values = values();
            
            valuesByType = Stream.of(RequestType.values())
                .map((t) -> Map.entry(t, filterByType(values, t)))
                .collect(Collectors.toUnmodifiableMap
                    (
                        (entry) -> entry.getKey(),
                        (entry) -> entry.getValue()
                    )
                );
        }

        return valuesByType.get(type);
    }

    private static List<CustomTest> filterByType(CustomTest[] values, RequestType type)
    {
        return Stream.of(values)
            .filter((value) -> type == RequestType.ALL || value.getType() == type)
            .collect(Collectors.toUnmodifiableList());
    }
}
