/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.tkmdata.exceptions.TkmDataException;
import com.h2tcoin.takamakachain.tkmdata.utils.TkmDataUtils;
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
import com.takamakachain.walletweb.resources.SignedRequestBean;
import com.takamakachain.walletweb.resources.SignedResponseBean;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

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

    public static final boolean manageRequests(
            SignedRequestBean srb,
            SignedResponseBean signedResponse,
            InstanceWalletKeystoreInterface iwk) throws TransactionCanNotBeCreatedException, WalletException, ProtocolException, IOException, TkmDataException {
        InternalTransactionBean itb = null;
        switch (srb.getRt()) {
            case GET_ADDRESS:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                break;
            case PAY:
                itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    return false;
                }
                break;
            case BLOB:
                System.out.println(srb.getTags());
                System.out.println(srb.getFrb().getAll().toString());
                itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));
                String messageText = generateMessageText(srb.getTags(), srb.getFrb());

                if (Strings.isBlank(messageText)) {
                    itb.setMessage(null);
                } else {
                    itb.setMessage(messageText);
                }
                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    return false;
                }

                break;

            case SEND_TRX:
                String hexBody = TkmSignUtils.fromStringToHexString(srb.getTrxJson());
                String transactionEndpoint = "https://dev.takamaka.io/api/V2/testapi/transaction/";
                String r = ProjectHelper.doPost(transactionEndpoint, "tx", hexBody);
                System.out.println(r);
                if (!r.contains("true")) {
                    System.out.println("ciaoneeee");
                    return false;
                }
                break;
            default:
        }
        return true;
    }

    public static final String generateMessageText(String tags, FilePropertiesBean fpb) throws IOException, TkmDataException {
        String messageText = null;
        ObjectMapper jacksonMapper = TkmTextUtils.getJacksonMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator gen = jacksonMapper.createGenerator(baos);
        gen.writeStartObject();
        //JsonObject jsMessage = new JsonObject();
        //jsMessage.addProperty(fromAddr, rootPaneCheckingEnabled);
        //String messageText = jTextAreaMessage.getText().trim();
        ConcurrentSkipListMap<Integer, Exception> exceptionMapper = TkmDataUtils.getExceptionMapper();
        fpb.getSelected().entrySet().forEach((Map.Entry<String, String> prop) -> {
            try {
                gen.writeStringField(prop.getKey(), prop.getValue());
            } catch (IOException ex) {
            }
        });
        TkmDataUtils.throwsMapException(exceptionMapper);
        gen.writeStringField("data", fpb.getFileContent());

        if (!TkmTextUtils.isNullOrBlank(tags)) {
            if (!TkmTextUtils.isNullOrBlank(tags)) {
                tags = tags.trim();
                String[] tagArray = tags.split(",");
                if (tagArray.length > 0) {
                    gen.writeFieldName("tags");
                    gen.writeStartArray();
                    //JsonArray jsonArray = new JsonArray(tagArray.length);
                    for (String tag : tagArray) {
                        String trimmedTag = StringUtils.trimToNull(tag);
                        if (!TkmTextUtils.isNullOrBlank(trimmedTag)) {
                            gen.writeObject(trimmedTag);
                        }
                    }
                    gen.writeEndArray();
                    //jsMessage.add("tags", jsonArray);
                }
            }

        }
        gen.writeEndObject();
        gen.flush();
        messageText = baos.toString(FixedParameters.CHARSET);
        gen.close();
        baos.close();
        return messageText;
    }

}
