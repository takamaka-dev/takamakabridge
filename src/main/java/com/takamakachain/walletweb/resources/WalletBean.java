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
public class WalletBean implements Serializable {

    private String walletName;
    private String walletPassword;
    private String walletCypher;
    private int addressNumber;

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public String getWalletCypher() {
        return walletCypher;
    }

    public void setWalletCypher(String walletCypher) {
        this.walletCypher = walletCypher;
    }

}
