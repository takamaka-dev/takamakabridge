/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import java.io.Serializable;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class SignedResponseBean implements Serializable {

    private String signedResponse;
    private SignedRequestBean request;

    public String getSignedResponse() {
        return signedResponse;
    }

    public void setSignedResponse(String signedResponse) {
        this.signedResponse = signedResponse;
    }

    public SignedRequestBean getRequest() {
        return request;
    }

    public void setRequest(SignedRequestBean request) {
        this.request = request;
    }

}