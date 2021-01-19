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
public class WalletIdenticonResponseBean {
    private String address;
    private String identiconUrlBase64;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdenticonUrlBase64() {
        return identiconUrlBase64;
    }

    public void setIdenticonUrlBase64(String identiconUrlBase64) {
        this.identiconUrlBase64 = identiconUrlBase64;
    }
    
}
