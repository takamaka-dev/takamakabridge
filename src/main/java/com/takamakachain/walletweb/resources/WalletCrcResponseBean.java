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
public class WalletCrcResponseBean {
    private String address;
    private String crcAddress;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCrcAddress() {
        return crcAddress;
    }

    public void setCrcAddress(String crcAddress) {
        this.crcAddress = crcAddress;
    }
    
    
}
