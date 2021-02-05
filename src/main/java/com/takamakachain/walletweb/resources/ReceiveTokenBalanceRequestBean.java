/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

/**
 *
 * @author isacco
 */
public class ReceiveTokenBalanceRequestBean {

    private String qColor;
    private String qAddr;
    private String qValue;
    private String qMessage;

    public String getqMessage() {
        return qMessage;
    }

    public void setqMessage(String qMessage) {
        this.qMessage = qMessage;
    }

    public String getqColor() {
        return qColor;
    }

    public void setqColor(String qColor) {
        this.qColor = qColor;
    }

    public String getqAddr() {
        return qAddr;
    }

    public void setqAddr(String qAddr) {
        this.qAddr = qAddr;
    }

    public String getqValue() {
        return qValue;
    }

    public void setqValue(String qValue) {
        this.qValue = qValue;
    }

}
