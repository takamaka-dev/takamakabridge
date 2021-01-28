/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import java.util.HashMap;

/**
 *
 * @author isacco
 */
public class QueryActionsResponseBean {

    private HashMap<String, String> getResponse;
    private String[] listResponse;
    private boolean success;
    private String error;
    private String requestType;

    public HashMap<String, String> getGetResponse() {
        return getResponse;
    }

    public void setGetResponse(HashMap<String, String> getResponse) {
        this.getResponse = getResponse;
    }

    public String[] getListResponse() {
        return listResponse;
    }

    public void setListResponse(String[] listResponse) {
        this.listResponse = listResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

}
