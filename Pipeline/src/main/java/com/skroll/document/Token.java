package com.skroll.document;

import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Token {
    String token;
    List<TokenAttributes> tokenFeaturesList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<TokenAttributes> getTokenFeaturesList() {
        return tokenFeaturesList;
    }

    public void setTokenFeaturesList(List<TokenAttributes> tokenFeaturesList) {
        this.tokenFeaturesList = tokenFeaturesList;
    }
}
