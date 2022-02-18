package observatory.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.results.domain.Category;
import observatory.internetnlAPI.config.results.domain.Test;

public enum ExtraField
{
    WEB_DNSSEC (RequestType.WEB, Category.WEB_DNSSEC),
    WEB_TLS_AVAILABLE (RequestType.WEB, Test.WEB_HTTPS_HTTP_AVAILABLE),
    WEB_HTTPS_REDIRECT (RequestType.WEB, Test.WEB_HTTPS_HTTP_REDIRECT),
    WEB_HSTS (RequestType.WEB, Test.WEB_HTTPS_HTTP_HSTS),

    WEB_IPV6 (RequestType.WEB, Category.WEB_IPV6),
    WEB_IPV6_NAME_SERVER (RequestType.WEB, Test.WEB_IPV6_NS_ADDRESS),
    WEB_IPV6_WEB_SERVER (RequestType.WEB, Test.WEB_IPV6_WS_ADDRESS),

    MAIL_DMARC (RequestType.MAIL, Test.MAIL_AUTH_DMARC_EXIST),
    MAIL_DKIM (RequestType.MAIL, Test.MAIL_AUTH_DKIM_EXIST),
    MAIL_SPF (RequestType.MAIL, Test.MAIL_AUTH_SPF_EXIST),
    MAIL_DMARC_POLICY (RequestType.MAIL, Test.MAIL_AUTH_DMARC_POLICY),
    MAIL_SPF_POLICY (RequestType.MAIL, Test.MAIL_AUTH_SPF_POLICY),

    MAIL_STARTTLS (RequestType.MAIL, Test.MAIL_STARTTLS_TLS_AVAILABLE),

    MAIL_DNSSEC_MAILTO_EXIST (RequestType.MAIL, Test.MAIL_DNSSEC_MAILTO_EXIST),
    MAIL_DNSSEC_MX_EXIST (RequestType.MAIL, Test.MAIL_DNSSEC_MX_EXIST),

    MAIL_DANE (RequestType.MAIL, Test.MAIL_STARTTLS_DANE_EXIST),

    MAIL_IPV6 (RequestType.MAIL, Category.MAIL_IPV6),
    MAIL_IPV6_NAME_SERVER (RequestType.MAIL, Test.MAIL_IPV6_NS_ADDRESS),
    MAIL_IPV6_MAIL_SERVER (RequestType.MAIL, Test.MAIL_IPV6_MX_ADDRESS);
    

    private static Map<RequestType, List<ExtraField>> valuesByType;

    private final RequestType type;

    private final Optional<Category> category;
    private final Optional<Test> test;

    private ExtraField(RequestType type, Category category)
    {
        this.type = type;
        this.category = Optional.of(category);
        this.test = Optional.empty();
    }

    private ExtraField(RequestType type, Test test)
    {
        this.type = type;
        this.test = Optional.of(test);
        this.category = Optional.empty();
    }

    /**
     * @return the type
     */
    public RequestType getType() {
        return type;
    }

    /**
     * @return the category
     */
    public Optional<Category> getCategory() {
        return category;
    }

    /**
     * @return the test
     */
    public Optional<Test> getTest() {
        return test;
    }

    public static List<ExtraField> values(RequestType type)
    {
        if (valuesByType == null)
        {
            ExtraField[] values = values();
            
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

    private static List<ExtraField> filterByType(ExtraField[] values, RequestType type)
    {
        return Stream.of(values)
            .filter((value) -> value.getType() == type)
            .collect(Collectors.toUnmodifiableList());
    }
}
