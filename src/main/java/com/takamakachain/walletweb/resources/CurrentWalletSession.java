/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.globalContext.KeyContexts;
import io.takamaka.code.whitelisting.internal.database.java.math.BigInteger;
import javax.ejb.Stateless;

/**
 *
 * @author isacco.borsani@h2tcoin.com
 */
@Stateless
public class CurrentWalletSession {

    private String walletAddress, walletName, walletPassword;
    private int walletIndex;
    private KeyContexts.WalletCypher walletCypher;
    private BigInteger tkgBalance, tkrBalance, frozenTkg, frozenTkr;

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

    public KeyContexts.WalletCypher getWalletCypher() {
        return walletCypher;
    }

    public void setWalletCypher(KeyContexts.WalletCypher walletCypher) {
        this.walletCypher = walletCypher;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public int getWalletIndex() {
        return walletIndex;
    }

    public void setWalletIndex(int walletIndex) {
        this.walletIndex = walletIndex;
    }

    public BigInteger getTkgBalance() {
        return tkgBalance;
    }

    public void setTkgBalance(BigInteger tkgBalance) {
        this.tkgBalance = tkgBalance;
    }

    public BigInteger getTkrBalance() {
        return tkrBalance;
    }

    public void setTkrBalance(BigInteger tkrBalance) {
        this.tkrBalance = tkrBalance;
    }

    public BigInteger getFrozenTkg() {
        return frozenTkg;
    }

    public void setFrozenTkg(BigInteger frozenTkg) {
        this.frozenTkg = frozenTkg;
    }

    public BigInteger getFrozenTkr() {
        return frozenTkr;
    }

    public void setFrozenTkr(BigInteger frozenTkr) {
        this.frozenTkr = frozenTkr;
    }

}
