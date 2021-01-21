/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
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
    private String uuid;
    private String trxJson;

    public String getTrxJson() {
        return trxJson;
    }

    public void setTrxJson(String trxJson) {
        this.trxJson = trxJson;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("U:" + uuid + " ").append("THE REQUEST").append("\n");
        if (wallet != null) {
            if (rt != null) {
                sb.append("U:" + uuid + " ").append("ReqType: " + rt.name()).append("\n");
            }
            //if (!TkmTextUtils.isNullOrBlank(wallet.getWalletName())) {
            sb.append("U:" + uuid + " ").append("WalletName: " + wallet.getWalletName()).append("\n");
            //}
            sb.append("U:" + uuid + " ").append("WalletNumber: " + wallet.getAddressNumber()).append("\n");
        }
        //
        //return super.toString(); //To change body of generated methods, choose Tools | Templates.
        sb.append("U:" + uuid + " ").append("THE REQUEST -- end");
        return sb.toString();
    }

}
