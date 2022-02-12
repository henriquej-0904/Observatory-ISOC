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

    WEB_IPV6_NS_ADDRESS ("web_ipv6_ns_address", "IPv6 addresses for name servers", RequestType.WEB),
    WEB_IPV6_NS_REACH ("web_ipv6_ns_reach", "IPv6 reachability of name servers", RequestType.WEB),
    WEB_IPV6_WS_ADDRESS ("web_ipv6_ws_address", "IPv6 addresses for web server", RequestType.WEB),
    WEB_IPV6_WS_REACH ("web_ipv6_ws_reach", "IPv6 reachability of web server", RequestType.WEB),
    WEB_IPV6_WS_SIMILAR ("web_ipv6_ws_similar", "Same website on IPv6 and IPv4", RequestType.WEB),

    WEB_DNSSEC_EXIST ("web_dnssec_exist", "DNSSEC existence", RequestType.WEB),
    WEB_DNSSEC_VALID ("web_dnssec_valid", "DNSSEC validity", RequestType.WEB),

    WEB_HTTPS_HTTP_AVAILABLE ("web_https_http_available", "HTTPS available", RequestType.WEB),
    WEB_HTTPS_HTTP_REDIRECT ("web_https_http_redirect", "HTTPS redirect", RequestType.WEB),
    WEB_HTTPS_HTTP_COMPRESS ("web_https_http_compress", "HTTP compression", RequestType.WEB),
    WEB_HTTPS_HTTP_HSTS ("web_https_http_hsts", "HSTS", RequestType.WEB),
    WEB_HTTPS_TLS_VERSION ("web_https_tls_version", "TLS version", RequestType.WEB),
    WEB_HTTPS_TLS_CIPHERS ("web_https_tls_ciphers", "Ciphers (Algorithm selections)", RequestType.WEB),
    WEB_HTTPS_TLS_CIPHER_ORDER ("web_https_tls_cipherorder", "Cipher order", RequestType.WEB),
    WEB_HTTPS_TLS_KEY_EXCHANGE ("web_https_tls_keyexchange", "Key exchange parameters", RequestType.WEB),
    WEB_HTTPS_TLS_KEY_EXCHANGE_HASH ("web_https_tls_keyexchangehash", "Hash function for key exchange", RequestType.WEB),
    WEB_HTTPS_TLS_COMPRESS ("web_https_tls_compress", "TLS compression", RequestType.WEB),
    WEB_HTTPS_TLS_SECURE_RENEG ("web_https_tls_secreneg", "Secure renegotiation", RequestType.WEB),
    WEB_HTTPS_TLS_CLIENT_RENEG ("web_https_tls_clientreneg", "Client-initiated renegotiation", RequestType.WEB),
    WEB_HTTPS_TLS_0RTT ("web_https_tls_0rtt", "0-RTT", RequestType.WEB),
    WEB_HTTPS_TLS_OCSP ("web_https_tls_ocsp", "OCSP stapling", RequestType.WEB),
    WEB_HTTPS_CERT_CHAIN ("web_https_cert_chain", "Trust chain of certificate", RequestType.WEB),
    WEB_HTTPS_CERT_PUBKEY ("web_https_cert_pubkey", "Public key of certificate", RequestType.WEB),
    WEB_HTTPS_CERT_SIG ("web_https_cert_sig", "Signature of certificate", RequestType.WEB),
    WEB_HTTPS_CERT_DOMAIN ("web_https_cert_domain", "Domain name on certificate", RequestType.WEB),
    WEB_HTTPS_DANE_EXIST ("web_https_dane_exist", "DANE existence", RequestType.WEB),
    WEB_HTTPS_DANE_VALID ("web_https_dane_valid", "DANE validity", RequestType.WEB),

    WEB_APPSECPRIV_X_FRAME_OPTIONS ("web_appsecpriv_x_frame_options", "X-Frame-Options", RequestType.WEB),
    WEB_APPSECPRIV_X_CONTENT_TYPE_OPTIONS ("web_appsecpriv_x_content_type_options", "X-Content-Type-Options", RequestType.WEB),
    WEB_APPSECPRIV_CSP ("web_appsecpriv_csp", "Content-Security-Policy existence", RequestType.WEB),
    WEB_APPSECPRIV_REFERRER_POLICY ("web_appsecpriv_referrer_policy", "Referrer-Policy existence", RequestType.WEB),

    //#endregion

    //#region MAIL

    MAIL_IPV6_NS_ADDRESS ("mail_ipv6_ns_address", "IPv6 addresses for name servers", RequestType.MAIL),
    MAIL_IPV6_NS_REACH ("mail_ipv6_ns_reach", "IPv6 reachability of name servers", RequestType.MAIL),
    MAIL_IPV6_WS_ADDRESS ("mail_ipv6_mx_address", "IPv6 addresses for mail server(s)", RequestType.MAIL),
    MAIL_IPV6_WS_REACH ("mail_ipv6_mx_reach", "IPv6 reachability of mail server(s)", RequestType.MAIL),
    
    MAIL_DNSSEC_MAILTO_EXIST ("mail_dnssec_mailto_exist", "DNSSEC existence", RequestType.MAIL),
    MAIL_DNSSEC_MAILTO_VALID ("mail_dnssec_mailto_valid", "DNSSEC validity", RequestType.MAIL),
    MAIL_DNSSEC_MX_EXIST ("mail_dnssec_mx_exist", "DNSSEC existence", RequestType.MAIL),
    MAIL_DNSSEC_MX_VALID ("mail_dnssec_mx_valid", "DNSSEC validity", RequestType.MAIL),

    MAIL_AUTH_DMARC_EXIST ("mail_auth_dmarc_exist", "DMARC existence", RequestType.MAIL),
    MAIL_AUTH_DMARC_POLICY ("mail_auth_dmarc_policy", "DMARC policy", RequestType.MAIL),
    MAIL_AUTH_DKIM_EXIST ("mail_auth_dkim_exist", "DKIM existence", RequestType.MAIL),
    MAIL_AUTH_SPF_EXIST ("mail_auth_spf_exist", "SPF existence", RequestType.MAIL),
    MAIL_AUTH_SPF_POLICY ("mail_auth_spf_policy", "SPF policy", RequestType.MAIL),

    MAIL_STARTTLS_TLS_AVAILABLE ("mail_starttls_tls_available", "STARTTLS available", RequestType.MAIL),
    MAIL_STARTTLS_TLS_VERSION ("mail_starttls_tls_version", "TLS version", RequestType.MAIL),
    MAIL_STARTTLS_TLS_CIPHERS ("mail_starttls_tls_ciphers", "Ciphers (Algorithm selections)", RequestType.MAIL),
    MAIL_STARTTLS_TLS_CIPHER_ORDER ("mail_starttls_tls_cipherorder", "Cipher order", RequestType.MAIL),
    MAIL_STARTTLS_TLS_KEY_EXCHANGE ("mail_starttls_tls_keyexchange", "Key exchange parameters", RequestType.MAIL),
    MAIL_STARTTLS_TLS_KEY_EXCHANGE_HASH ("mail_starttls_tls_keyexchangehash", "Hash function for key exchange", RequestType.MAIL),
    MAIL_STARTTLS_TLS_COMPRESS ("mail_starttls_tls_compress", "TLS compression", RequestType.MAIL),
    MAIL_STARTTLS_TLS_SECURE_RENEG ("mail_starttls_tls_secreneg", "Secure renegotiation", RequestType.MAIL),
    MAIL_STARTTLS_TLS_CLIENT_RENEG ("mail_starttls_tls_clientreneg", "Client-initiated renegotiation", RequestType.MAIL),
    MAIL_STARTTLS_TLS_0RTT ("mail_starttls_tls_0rtt", "0-RTT", RequestType.MAIL),
    MAIL_STARTTLS_CERT_CHAIN ("mail_starttls_cert_chain", "Trust chain of certificate", RequestType.MAIL),
    MAIL_STARTTLS_CERT_PUBKEY ("mail_starttls_cert_pubkey", "Public key of certificate", RequestType.MAIL),
    MAIL_STARTTLS_CERT_SIG ("mail_starttls_cert_sig", "Signature of certificate", RequestType.MAIL),
    MAIL_STARTTLS_CERT_DOMAIN ("mail_starttls_cert_domain", "Domain name on certificate", RequestType.MAIL),
    MAIL_STARTTLS_DANE_EXIST ("mail_starttls_dane_exist", "DANE existence", RequestType.MAIL),
    MAIL_STARTTLS_DANE_VALID ("mail_starttls_dane_valid", "DANE validity", RequestType.MAIL),
    MAIL_STARTTLS_DANE_ROLLOVER ("mail_starttls_dane_rollover", "DANE rollover scheme", RequestType.MAIL);


    //#endregion


    private static Map<RequestType, List<Test>> valuesByType;

    private String test, description;

    private RequestType type;

    private Test(String test, String description, RequestType type)
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
}
