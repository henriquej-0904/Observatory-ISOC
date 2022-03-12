package observatory.internetnlAPI.config.testResult.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import observatory.internetnlAPI.config.RequestType;

/**
 * Represents the categories of the tests performed by the internet.nl API.
 */
public enum Category
{
    WEB_IPV6 ("web_ipv6", "Modern address (IPv6)", RequestType.WEB),
    WEB_DNSSEC ("web_dnssec", "Signed domain name (DNSSEC)", RequestType.WEB),
    WEB_HTTPS ("web_https", "Secure connection (HTTPS)", RequestType.WEB),
    WEB_APPSECPRIV ("web_appsecpriv", "HTTP security headers", RequestType.WEB),

    MAIL_IPV6 ("mail_ipv6", "Modern address (IPv6)", RequestType.MAIL),
    MAIL_DNSSEC ("mail_dnssec", "Signed domain name (DNSSEC)", RequestType.MAIL),
    MAIL_AUTH ("mail_auth", "Authenticity marks against phishing (DMARC, DKIM en SPF)", RequestType.MAIL),
    MAIL_STARTTLS ("mail_starttls", "Secure mail server connection (STARTTLS and DANE)", RequestType.MAIL);

    private static Map<RequestType, List<Category>> valuesByType;

    private String category, description;

    private RequestType type;

    private Category(String category, String description, RequestType type)
    {
        this.category = category;
        this.description = description;
        this.type = type;
    }

    /**
     * @return the category
     */
    @JsonValue
    public String getCategory() {
        return category;
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
    
    public static List<Category> values(RequestType type)
    {
        if (valuesByType == null)
        {
            Category[] values = values();
            
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

    private static List<Category> filterByType(Category[] values, RequestType type)
    {
        return Stream.of(values)
            .filter((value) -> value.getType() == type)
            .collect(Collectors.toUnmodifiableList());
    }
}
