package com.skroll.document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Token {
    String token;
    List<TokenAttributes> tokenFeaturesList;

    public String getText() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
