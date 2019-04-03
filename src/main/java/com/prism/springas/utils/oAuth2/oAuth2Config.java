package com.prism.springas.utils.oAuth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties("configBasic.oAuth2")
public class oAuth2Config implements Serializable {

    private static final long serialVersionUID = 1L;

    private String useE2;

    private String E2AuthorizeUrl;

    private String E2AccessTokenUrl;

    private String E2LogoutUrl;

    private String E2OAuthApp1ClientId;

    private String E2OAuthApp1ClientSecret;

    private String E2OAuthApp1CallBackUrl;

    private String E2OAuthApp1Domain;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUseE2() {
        return useE2;
    }

    public void setUseE2(String useE2) {
        this.useE2 = useE2;
    }

    public String getE2AuthorizeUrl() {
        return E2AuthorizeUrl;
    }

    public void setE2AuthorizeUrl(String e2AuthorizeUrl) {
        E2AuthorizeUrl = e2AuthorizeUrl;
    }

    public String getE2AccessTokenUrl() {
        return E2AccessTokenUrl;
    }

    public void setE2AccessTokenUrl(String e2AccessTokenUrl) {
        E2AccessTokenUrl = e2AccessTokenUrl;
    }

    public String getE2LogoutUrl() {
        return E2LogoutUrl;
    }

    public void setE2LogoutUrl(String e2LogoutUrl) {
        E2LogoutUrl = e2LogoutUrl;
    }

    public String getE2OAuthApp1ClientId() {
        return E2OAuthApp1ClientId;
    }

    public void setE2OAuthApp1ClientId(String e2OAuthApp1ClientId) {
        E2OAuthApp1ClientId = e2OAuthApp1ClientId;
    }

    public String getE2OAuthApp1ClientSecret() {
        return E2OAuthApp1ClientSecret;
    }

    public void setE2OAuthApp1ClientSecret(String e2OAuthApp1ClientSecret) {
        E2OAuthApp1ClientSecret = e2OAuthApp1ClientSecret;
    }

    public String getE2OAuthApp1CallBackUrl() {
        return E2OAuthApp1CallBackUrl;
    }

    public void setE2OAuthApp1CallBackUrl(String e2OAuthApp1CallBackUrl) {
        E2OAuthApp1CallBackUrl = e2OAuthApp1CallBackUrl;
    }

    public String getE2OAuthApp1Domain() {
        return E2OAuthApp1Domain;
    }

    public void setE2OAuthApp1Domain(String e2OAuthApp1Domain) {
        E2OAuthApp1Domain = e2OAuthApp1Domain;
    }
}
