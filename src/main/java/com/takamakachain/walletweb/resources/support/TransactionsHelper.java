/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.h2tcoin.takamakachain.transactions.TransactionBean;
import com.h2tcoin.takamakachain.transactions.fee.FeeBean;
import com.h2tcoin.takamakachain.transactions.fee.TransactionFeeCalculator;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import com.h2tcoin.takamakachain.wallet.TkmWallet;
import com.h2tcoin.takamakachain.wallet.TransactionBox;
import com.takamakachain.walletweb.resources.SignedRequestBean;
import com.takamakachain.walletweb.resources.SignedResponseBean;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Date;
import javax.ws.rs.core.Response;

/**
 *
 * @author isacco
 */
public class TransactionsHelper {

    public static boolean makeJsonTrx(
            SignedResponseBean signedResponse,
            InternalTransactionBean itb,
            InstanceWalletKeystoreInterface iwk,
            int addressNumber) throws TransactionCanNotBeCreatedException {
        if (null != itb) {
            TransactionBean genericTRA;

            genericTRA = TkmWallet.createGenericTransaction(
                    itb,
                    iwk,
                    addressNumber);

            String txJson = TkmTextUtils.toJson(genericTRA);
            TransactionBox tbox = TkmWallet.verifyTransactionIntegrity(txJson);
            FeeBean feeBean = TransactionFeeCalculator.getFeeBean(tbox);

            signedResponse.setFeeBean(feeBean);
            signedResponse.setTrxJson(txJson);

            return !(feeBean == null || feeBean.getDisk() == null);

        }
        return false;
    }

    public static boolean manageRequests(
            SignedRequestBean srb,
            SignedResponseBean signedResponse,
            InstanceWalletKeystoreInterface iwk) throws TransactionCanNotBeCreatedException, WalletException, ProtocolException, IOException {
        switch (srb.getRt()) {
            case GET_ADDRESS:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                break;
            case PAY:
                InternalTransactionBean itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    return false;
                }
                break;
            case SEND_TRX:
                String hexBody = TkmSignUtils.fromStringToHexString(srb.getTrxJson());
                String transactionEndpoint = "https://dev.takamaka.io/api/V2/testapi/transaction/";
                String r = ProjectHelper.doPost(transactionEndpoint, "tx", hexBody);
                if (!r.equals("{\"TxIsVerified\":\"true\"}")) {
                    return false;
                }
                break;
            default:
        }
        return true;
    }

}
