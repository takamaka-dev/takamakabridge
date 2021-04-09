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
    private String hash;
    private String notBefore;

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    private WebHelper.RequestType rt;
    private String uuid;
    private String trxJson;
    private FilePropertiesBean frb;
    private String[] tags;
    private String env;
    private String recoveryWords;
    private ReceiveTokenBalanceRequestBean rtbr;

    public ReceiveTokenBalanceRequestBean getRtbr() {
        return rtbr;
    }

    public void setRtbr(ReceiveTokenBalanceRequestBean rtbr) {
        this.rtbr = rtbr;
    }

    public String getRecoveryWords() {
        return recoveryWords;
    }

    public void setRecoveryWords(String recoveryWords) {
        this.recoveryWords = recoveryWords;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public FilePropertiesBean getFrb() {
        return frb;
    }

    public void setFrb(FilePropertiesBean frb) {
        this.frb = frb;
    }

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
