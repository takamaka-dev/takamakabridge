/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.tkmdata.exceptions.TkmDataException;
import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.h2tcoin.takamakachain.transactions.TransactionBean;
import com.h2tcoin.takamakachain.transactions.fee.FeeBean;
import com.h2tcoin.takamakachain.transactions.fee.TransactionFeeCalculator;
import com.takamakachain.walletweb.resources.FilePropertiesBean;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import com.h2tcoin.takamakachain.wallet.TkmWallet;
import com.h2tcoin.takamakachain.wallet.TransactionBox;
import com.h2tcoin.takamakachain.wallet.WalletHelper;
import com.hazelcast.internal.json.JsonObject;
import com.takamakachain.walletweb.resources.SignedRequestBean;
import com.takamakachain.walletweb.resources.SignedResponseBean;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ProtocolException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.util.Date;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 *
 * @author isacco
 */
public class TransactionsHelper {

    public static final boolean makeJsonTrx(
            SignedResponseBean signedResponse,
            InternalTransactionBean itb,
            InstanceWalletKeystoreInterface iwk,
            int addressNumber) throws TransactionCanNotBeCreatedException {
        if (null != itb) {

            if (TkmTextUtils.isNullOrBlank(itb.getFrom())) {
                System.out.println("Overwrite from!");
                try {
                    itb.setFrom(iwk.getPublicKeyAtIndexURL64(addressNumber));
                } catch (WalletException ex) {
                    return false;
                }
            }

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

    public static final String prepareMessageBase64(String oldMessage) {
        JSONObject jsonObjectMessage = ProjectHelper.isJSONValid(oldMessage);
        String oldData = jsonObjectMessage.getString("data");
        String textTrimToNull = StringUtils.trimToNull(oldData);
        String b64Message = TkmSignUtils.fromByteArrayToB64URL(textTrimToNull.getBytes(FixedParameters.CHARSET));
        jsonObjectMessage.remove("data");
        jsonObjectMessage.accumulate("data", b64Message);
        return jsonObjectMessage.toString();
    }
    
    public static final boolean manageRequests(
            SignedRequestBean srb,
            SignedResponseBean signedResponse,
            InstanceWalletKeystoreInterface iwk,
            String plainPassword
            ) throws TransactionCanNotBeCreatedException, WalletException, ProtocolException, IOException, TkmDataException, FileNotFoundException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        InternalTransactionBean itb = null;
        switch (srb.getRt()) {
            case GET_ADDRESS:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                String words = WalletHelper.readKeyFile(ProjectHelper.getCurrentWalletpath(srb.getWallet().getWalletName()), plainPassword).getWords();
                if (!TkmTextUtils.isNullOrBlank(words)) {
                    signedResponse.setWords(words);
                }
                break;
            case PAY:
                itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    return false;
                }
                break;
            case BLOB_RICH_TEXT:
                itb = srb.getItb();
                itb.setMessage(prepareMessageBase64(itb.getMessage()));
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    System.out.println("Failed");
                    return false;
                }

                break;
            case BLOB:
                //System.out.println(srb.getTags()); //null
                //System.out.println(srb.getFrb().getAll().toString());
                itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    System.out.println("Failed");
                    return false;
                }

                break;

            case SEND_TRX:
                String hexBody = TkmSignUtils.fromStringToHexString(srb.getTrxJson());
                String transactionEndpoint = "https://dev.takamaka.io/api/V2/testapi/transaction/";
                if (srb.getEnv().equals("prod")) {
                    transactionEndpoint = "https://dev.takamaka.io/api/V2/nodeapi/transaction/";
                }
                System.out.println(transactionEndpoint);
                String r = ProjectHelper.doPost(transactionEndpoint, "tx", hexBody);
                System.out.println(r);
                if (!r.contains("true")) {
                    return false;
                }
                break;
            default:
        }
        return true;
    }

    public static final String generateMessageText(String[] tags, FilePropertiesBean fpb) throws IOException, TkmDataException {
        String messageText = null;
        return messageText;
    }

}
