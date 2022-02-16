package observatory.internetnlAPI.config.results.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import observatory.internetnlAPI.config.RequestType;

/**
 * Represents the tests performed by the internet.nl API.
 */
public enum Test
{

    //#region WEB

    WEB_IPV6_NS_ADDRESS ("web_ipv6_ns_address", "IPv6 addresses for name servers", RequestType.WEB, Category.WEB_IPV6),
    WEB_IPV6_NS_REACH ("web_ipv6_ns_reach", "IPv6 reachability of name servers", RequestType.WEB, Category.WEB_IPV6),
    WEB_IPV6_WS_ADDRESS ("web_ipv6_ws_address", "IPv6 addresses for web server", RequestType.WEB, Category.WEB_IPV6),
    WEB_IPV6_WS_REACH ("web_ipv6_ws_reach", "IPv6 reachability of web server", RequestType.WEB, Category.WEB_IPV6),
    WEB_IPV6_WS_SIMILAR ("web_ipv6_ws_similar", "Same website on IPv6 and IPv4", RequestType.WEB, Category.WEB_IPV6),

    WEB_DNSSEC_EXIST ("web_dnssec_exist", "DNSSEC existence", RequestType.WEB, Category.WEB_DNSSEC),
    WEB_DNSSEC_VALID ("web_dnssec_valid", "DNSSEC validity", RequestType.WEB, Category.WEB_DNSSEC),

    WEB_HTTPS_HTTP_AVAILABLE ("web_https_http_available", "HTTPS available", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_HTTP_REDIRECT ("web_https_http_redirect", "HTTPS redirect", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_HTTP_COMPRESS ("web_https_http_compress", "HTTP compression", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_HTTP_HSTS ("web_https_http_hsts", "HSTS", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_VERSION ("web_https_tls_version", "TLS version", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_CIPHERS ("web_https_tls_ciphers", "Ciphers (Algorithm selections)", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_CIPHER_ORDER ("web_https_tls_cipherorder", "Cipher order", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_KEY_EXCHANGE ("web_https_tls_keyexchange", "Key exchange parameters", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_KEY_EXCHANGE_HASH ("web_https_tls_keyexchangehash", "Hash function for key exchange", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_COMPRESS ("web_https_tls_compress", "TLS compression", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_SECURE_RENEG ("web_https_tls_secreneg", "Secure renegotiation", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_CLIENT_RENEG ("web_https_tls_clientreneg", "Client-initiated renegotiation", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_0RTT ("web_https_tls_0rtt", "0-RTT", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_TLS_OCSP ("web_https_tls_ocsp", "OCSP stapling", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_CERT_CHAIN ("web_https_cert_chain", "Trust chain of certificate", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_CERT_PUBKEY ("web_https_cert_pubkey", "Public key of certificate", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_CERT_SIG ("web_https_cert_sig", "Signature of certificate", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_CERT_DOMAIN ("web_https_cert_domain", "Domain name on certificate", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_DANE_EXIST ("web_https_dane_exist", "DANE existence", RequestType.WEB, Category.WEB_HTTPS),
    WEB_HTTPS_DANE_VALID ("web_https_dane_valid", "DANE validity", RequestType.WEB, Category.WEB_HTTPS),

    WEB_APPSECPRIV_X_FRAME_OPTIONS ("web_appsecpriv_x_frame_options", "X-Frame-Options", RequestType.WEB, Category.WEB_APPSECPRIV),
    WEB_APPSECPRIV_X_CONTENT_TYPE_OPTIONS ("web_appsecpriv_x_content_type_options", "X-Content-Type-Options", RequestType.WEB, Category.WEB_APPSECPRIV),
    WEB_APPSECPRIV_CSP ("web_appsecpriv_csp", "Content-Security-Policy existence", RequestType.WEB, Category.WEB_APPSECPRIV),
    WEB_APPSECPRIV_REFERRER_POLICY ("web_appsecpriv_referrer_policy", "Referrer-Policy existence", RequestType.WEB, Category.WEB_APPSECPRIV),

    //#endregion

    //#region MAIL

    MAIL_IPV6_NS_ADDRESS ("mail_ipv6_ns_address", "IPv6 addresses for name servers", RequestType.MAIL, Category.MAIL_IPV6),
    MAIL_IPV6_NS_REACH ("mail_ipv6_ns_reach", "IPv6 reachability of name servers", RequestType.MAIL, Category.MAIL_IPV6),
    MAIL_IPV6_WS_ADDRESS ("mail_ipv6_mx_address", "IPv6 addresses for mail server(s)", RequestType.MAIL, Category.MAIL_IPV6),
    MAIL_IPV6_WS_REACH ("mail_ipv6_mx_reach", "IPv6 reachability of mail server(s)", RequestType.MAIL, Category.MAIL_IPV6),
    
    MAIL_DNSSEC_MAILTO_EXIST ("mail_dnssec_mailto_exist", "DNSSEC existence", RequestType.MAIL, Category.MAIL_DNSSEC),
    MAIL_DNSSEC_MAILTO_VALID ("mail_dnssec_mailto_valid", "DNSSEC validity", RequestType.MAIL, Category.MAIL_DNSSEC),
    MAIL_DNSSEC_MX_EXIST ("mail_dnssec_mx_exist", "DNSSEC existence", RequestType.MAIL, Category.MAIL_DNSSEC),
    MAIL_DNSSEC_MX_VALID ("mail_dnssec_mx_valid", "DNSSEC validity", RequestType.MAIL, Category.MAIL_DNSSEC),

    MAIL_AUTH_DMARC_EXIST ("mail_auth_dmarc_exist", "DMARC existence", RequestType.MAIL, Category.MAIL_AUTH),
    MAIL_AUTH_DMARC_POLICY ("mail_auth_dmarc_policy", "DMARC policy", RequestType.MAIL, Category.MAIL_AUTH),
    MAIL_AUTH_DKIM_EXIST ("mail_auth_dkim_exist", "DKIM existence", RequestType.MAIL, Category.MAIL_AUTH),
    MAIL_AUTH_SPF_EXIST ("mail_auth_spf_exist", "SPF existence", RequestType.MAIL, Category.MAIL_AUTH),
    MAIL_AUTH_SPF_POLICY ("mail_auth_spf_policy", "SPF policy", RequestType.MAIL, Category.MAIL_AUTH),

    MAIL_STARTTLS_TLS_AVAILABLE ("mail_starttls_tls_available", "STARTTLS available", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_VERSION ("mail_starttls_tls_version", "TLS version", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_CIPHERS ("mail_starttls_tls_ciphers", "Ciphers (Algorithm selections)", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_CIPHER_ORDER ("mail_starttls_tls_cipherorder", "Cipher order", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_KEY_EXCHANGE ("mail_starttls_tls_keyexchange", "Key exchange parameters", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_KEY_EXCHANGE_HASH ("mail_starttls_tls_keyexchangehash", "Hash function for key exchange", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_COMPRESS ("mail_starttls_tls_compress", "TLS compression", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_SECURE_RENEG ("mail_starttls_tls_secreneg", "Secure renegotiation", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_CLIENT_RENEG ("mail_starttls_tls_clientreneg", "Client-initiated renegotiation", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_TLS_0RTT ("mail_starttls_tls_0rtt", "0-RTT", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_CERT_CHAIN ("mail_starttls_cert_chain", "Trust chain of certificate", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_CERT_PUBKEY ("mail_starttls_cert_pubkey", "Public key of certificate", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_CERT_SIG ("mail_starttls_cert_sig", "Signature of certificate", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_CERT_DOMAIN ("mail_starttls_cert_domain", "Domain name on certificate", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_DANE_EXIST ("mail_starttls_dane_exist", "DANE existence", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_DANE_VALID ("mail_starttls_dane_valid", "DANE validity", RequestType.MAIL, Category.MAIL_STARTTLS),
    MAIL_STARTTLS_DANE_ROLLOVER ("mail_starttls_dane_rollover", "DANE rollover scheme", RequestType.MAIL, Category.MAIL_STARTTLS);


    //#endregion


    private static Map<RequestType, List<Test>> valuesByType;

    private static Map<Category, List<Test>> valuesByCategory;

    private String test, description;

    private RequestType type;

    private Category category;

    private Test(String test, String description, RequestType type, Category category)
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

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    public static List<Test> values(RequestType type)
    {
        if (valuesByType == null)
        {
            Test[] values = values();
            
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

    private static List<Test> filterByType(Test[] values, RequestType type)
    {
        return Stream.of(values)
            .filter((value) -> value.getType() == type)
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<Test> values(Category category)
    {
        if (valuesByCategory == null)
        {
            Test[] values = values();
            
            valuesByCategory = Stream.of(Category.values())
                .map((t) -> Map.entry(t, filterByCategory(values, t)))
                .collect(Collectors.toUnmodifiableMap
                    (
                        (entry) -> entry.getKey(),
                        (entry) -> entry.getValue()
                    )
                );
        }

        return valuesByCategory.get(category);
    }

    private static List<Test> filterByCategory(Test[] values, Category category)
    {
        return Stream.of(values)
            .filter((value) -> value.getCategory() == category)
            .collect(Collectors.toUnmodifiableList());
    }
}
