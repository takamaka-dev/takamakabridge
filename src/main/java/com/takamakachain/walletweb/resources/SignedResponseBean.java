/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.transactions.fee.FeeBean;
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
    private String passedData;
    private FeeBean feeBean;
    private String trxJson;
    private String endpoint;
    private String words;

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public FeeBean getFeeBean() {
        return feeBean;
    }

    public void setFeeBean(FeeBean feeBean) {
        this.feeBean = feeBean;
    }

    public String getTrxJson() {
        return trxJson;
    }

    public void setTrxJson(String trxJson) {
        this.trxJson = trxJson;
    }

    public String getPassedData() {
        return passedData;
    }

    public void setPassedData(String passedData) {
        this.passedData = passedData;
    }

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
