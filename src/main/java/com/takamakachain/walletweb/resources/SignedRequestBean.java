/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.takamakachain.walletweb.resources.support.WebHelper;
import java.io.Serializable;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class SignedRequestBean implements Serializable {

    private WalletBean wallet;
    private InternalTransactionBean itb;
    private WebHelper.RequestType rt;

    public WalletBean getWallet() {
        return wallet;
    }

    public void setWallet(WalletBean wallet) {
        this.wallet = wallet;
    }

    public InternalTransactionBean getItb() {
        return itb;
    }

    public void setItb(InternalTransactionBean itb) {
        this.itb = itb;
    }

    public WebHelper.RequestType getRt() {
        return rt;
    }

    public void setRt(WebHelper.RequestType rt) {
        this.rt = rt;
    }

}
