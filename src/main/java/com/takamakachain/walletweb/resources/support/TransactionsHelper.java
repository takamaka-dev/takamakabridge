/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.h2tcoin.takamakachain.transactions.TransactionBean;
import com.h2tcoin.takamakachain.transactions.fee.FeeBean;
import com.h2tcoin.takamakachain.transactions.fee.TransactionFeeCalculator;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import com.h2tcoin.takamakachain.wallet.TkmWallet;
import com.h2tcoin.takamakachain.wallet.TransactionBox;
import com.takamakachain.walletweb.resources.SignedResponseBean;

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
}
