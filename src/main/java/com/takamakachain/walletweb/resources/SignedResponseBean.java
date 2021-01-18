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
    private String walletAddress;
    private int walletKey;

    public int getWalletKey() {
        return walletKey;
    }

    public void setWalletKey(int walletKey) {
        this.walletKey = walletKey;
    }
    
    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    
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
