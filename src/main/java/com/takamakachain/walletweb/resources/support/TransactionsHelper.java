/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.h2tcoin.takamakachain.exceptions.wallet.TransactionCanNotBeCreatedException;
import com.h2tcoin.takamakachain.exceptions.wallet.WalletException;
import com.h2tcoin.takamakachain.globalContext.FixedParameters;
import com.h2tcoin.takamakachain.main.defaults.DefaultInitParameters;
import com.h2tcoin.takamakachain.tkmdata.exceptions.TkmDataException;
import com.h2tcoin.takamakachain.transactions.InternalTransactionBean;
import com.h2tcoin.takamakachain.transactions.TransactionBean;
import com.h2tcoin.takamakachain.transactions.fee.FeeBean;
import com.h2tcoin.takamakachain.transactions.fee.TransactionFeeCalculator;
import com.h2tcoin.takamakachain.utils.F;
import com.h2tcoin.takamakachain.utils.FileHelper;
import com.h2tcoin.takamakachain.utils.networking.RequestPaymentBean;
import com.takamakachain.walletweb.resources.FilePropertiesBean;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import com.h2tcoin.takamakachain.wallet.InstanceWalletKeystoreInterface;
import com.h2tcoin.takamakachain.wallet.TkmWallet;
import com.h2tcoin.takamakachain.wallet.TransactionBox;
import com.h2tcoin.takamakachain.wallet.WalletHelper;
import com.takamakachain.walletweb.resources.NodeBean;
import com.takamakachain.walletweb.resources.ReceiveTokenBalanceRequestBean;
import com.takamakachain.walletweb.resources.SignedRequestBean;
import com.takamakachain.walletweb.resources.SignedResponseBean;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
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
            int addressNumber) throws TransactionCanNotBeCreatedException, IOException {
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

            if (!logTransactions(tbox)) {
                return false;
            }

            FeeBean feeBean = TransactionFeeCalculator.getFeeBean(tbox);

            signedResponse.setFeeBean(feeBean);
            signedResponse.setTrxJson(txJson);

            return !(feeBean == null || feeBean.getDisk() == null);

        }
        return false;
    }
    
    public static String Post(String uri, Map<String, String> parameters) throws UnsupportedEncodingException, ProtocolException, IOException {
        URL url = new URL(uri);
        URLConnection con = url.openConnection();
        try {
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            http.setConnectTimeout(10 * 1000); //set timeout to 10 seconds
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
                F.b("Key: " + entry.getKey() + " - Value: " + entry.getValue());
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            F.b(sj.toString());

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            } catch(Exception e){
                e.printStackTrace();
                return "";
            }
            Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));

            String ret = "";
            for (int c; (c = in.read()) >= 0;) {
                ret += (char) c;
            }

            return ret;
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            return "";
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
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
        String words;
        String walletAddress;
        ArrayList fileList, bannedListFile, finalOrderedResult;
        HashMap hm;
        Map<String, String> orderedMap;
        Set set2;
        Iterator iterator2;
        String filename;
        switch (srb.getRt()) {
            case GET_ADDRESS:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                words = WalletHelper.readKeyFile(ProjectHelper.getCurrentWalletpath(srb.getWallet().getWalletName()), plainPassword).getWords();
                if (!TkmTextUtils.isNullOrBlank(words)) {
                    signedResponse.setWords(words);
                }
                break;
            case RECOVER_WALLET:
                signedResponse.setWalletAddress(iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber()));
                words = WalletHelper.readKeyFile(ProjectHelper.getCurrentWalletpath(srb.getWallet().getWalletName()), plainPassword).getWords();
                if (!TkmTextUtils.isNullOrBlank(words)) {
                    signedResponse.setWords(words);
                }
                break;
            case MOVE_TO_APPROVED:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                filename = srb.getNotBefore() + "_" + TkmSignUtils.fromStringToHexString(srb.getHash());
                FileHelper.rename(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages", filename), Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "approved", filename), Boolean.TRUE);
                break;
            case MOVE_TO_REJECTED:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                filename = srb.getNotBefore() + "_" + TkmSignUtils.fromStringToHexString(srb.getHash());
                FileHelper.rename(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages", filename), Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "rejected", filename), Boolean.TRUE);
                break;
            case MOVE_TO_BLACKLIST:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                filename = srb.getNotBefore() + "_" + TkmSignUtils.fromStringToHexString(srb.getHash());
                FileHelper.rename(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages", filename), Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "banned", filename), Boolean.TRUE);

                break;
            case GET_NEW_MESSAGES:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                
                fileList = FileHelper.getWalletListInFolder(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages"), null);
                fileList.sort(Comparator.reverseOrder());
                hm = new HashMap<String, String>();
                fileList.forEach(e -> {
                    //System.out.println(e);
                    try {
                        hm.put(e.toString(), new JSONObject(FileHelper.readStringFromFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages", e.toString()))).toString());
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TransactionsHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                orderedMap = new TreeMap<String, String>(Collections.reverseOrder());
                orderedMap.putAll(hm);
                set2 = orderedMap.entrySet();
                iterator2 = set2.iterator();
                finalOrderedResult = new ArrayList<String>();
                while (iterator2.hasNext()) {
                    Map.Entry me2 = (Map.Entry) iterator2.next();
                    finalOrderedResult.add(me2.getValue().toString());
                }

                signedResponse.setPostReturn(new JSONArray(finalOrderedResult).toString());
                break;
            case GET_REJECTED_MESSAGES:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                fileList = FileHelper.getWalletListInFolder(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "rejected"), null);
                hm = new HashMap<String, String>();
                fileList.forEach(e -> {
                    try {
                        hm.put(e.toString(), new JSONObject(FileHelper.readStringFromFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "rejected", e.toString()))).toString());
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TransactionsHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                orderedMap = new TreeMap<String, String>(Collections.reverseOrder());
                orderedMap.putAll(hm);
                set2 = orderedMap.entrySet();
                iterator2 = set2.iterator();
                finalOrderedResult = new ArrayList<String>();
                while (iterator2.hasNext()) {
                    Map.Entry me2 = (Map.Entry) iterator2.next();
                    finalOrderedResult.add(me2.getValue().toString());
                }

                signedResponse.setPostReturn(new JSONArray(finalOrderedResult).toString());
                break;
            case CHECK_NEW_TRANSACTIONS:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());
                String jsonResponseTransactions = ProjectHelper.doPost("https://dev.takamaka.io/api/V2/testapi/listtransactions", "address", walletAddress);

                if (TkmTextUtils.isNullOrBlank(jsonResponseTransactions)) {
                    return false;
                }
                signedResponse.setPostReturn(jsonResponseTransactions);
                JSONArray jsonObjectResponse = ProjectHelper.getJsonArrayObject(jsonResponseTransactions);

                String readStringFromFile = FileHelper.readStringFromFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "includedTransactionHash"));

                List<String> hashesList = new ArrayList<String>(Arrays.asList(readStringFromFile.split("\n")));

                bannedListFile = FileHelper.getWalletListInFolder(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "banned"), null);

                ArrayList<String> bannedFromList = new ArrayList<String>();
                bannedListFile.forEach(e -> {
                    try {
                        JSONObject content = new JSONObject(FileHelper.readStringFromFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "banned", e.toString())));
                        bannedFromList.add(content.get("from").toString());
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TransactionsHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                String newContentHashesList = "";
                for (Object o : jsonObjectResponse) {
                    if (o instanceof JSONObject) {
                        if (((JSONObject) o).get("transactionType").toString().equals("PAY")
                                && ((JSONObject) o).get("to").toString().equals(walletAddress)
                                && ((JSONObject) o).get("validity").toString().equals("true")
                                && !bannedFromList.contains(((JSONObject) o).get("from").toString())
                                && !hashesList.contains(((JSONObject) o).get("transactionHash").toString())) {
                            filename = ((JSONObject) o).get("notBefore").toString() + "_" + TkmSignUtils.fromStringToHexString(((JSONObject) o).get("transactionHash").toString());

                            if (!FileHelper.writeStringToFile(
                                    Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages"),
                                    filename,
                                    o.toString(),
                                    true)) {
                                return false;
                            }
                        }
                        if (((JSONObject) o).get("transactionType").toString().equals("PAY")
                                && ((JSONObject) o).get("to").toString().equals(walletAddress)) {
                            newContentHashesList += ((JSONObject) o).get("transactionHash").toString() + "\n";
                        }
                    }
                }

                if (!FileHelper.writeStringToFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress), "includedTransactionHash", newContentHashesList, true)) {
                    return false;
                }

                break;
            case CREATE_CAMPAIGN:
                walletAddress = iwk.getPublicKeyAtIndexURL64(srb.getWallet().getAddressNumber());

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns"))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns"));
                }

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress));
                }

                if (!FileHelper.fileExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "includedTransactionHash"))) {
                    FileHelper.writeStringToFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress), "includedTransactionHash", "", false);
                }

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages"))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "new_messages"));
                }

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "approved"))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "approved"));
                }

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "rejected"))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "rejected"));
                }

                if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "banned"))) {
                    FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "campaigns", walletAddress, "banned"));
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

            case STAKE:
                itb = srb.getItb();
                itb.setNotBefore(new Date((new Date()).getTime() + 60000L * 5));

                if (!TransactionsHelper.makeJsonTrx(signedResponse, itb, iwk, srb.getWallet().getAddressNumber())) {
                    System.out.println("Failed");
                    return false;
                }

                break;

            case RECEIVE_TOKENS:
                ReceiveTokenBalanceRequestBean rtbr = srb.getRtbr();
                signedResponse.setBase64QrCodeReceiveBalance(getQR(createQRString(rtbr)));
                break;

            case SEND_TRX:
                String hexBody = TkmSignUtils.fromStringToHexString(srb.getTrxJson());
                String transactionEndpoint = srb.getEnv();
                System.out.println(transactionEndpoint);
                String r = ProjectHelper.doPost(transactionEndpoint, "tx", hexBody);
                System.out.println(r);
                JSONObject jsonTxResponse = new JSONObject(r);
                signedResponse.setTrxVerified(jsonTxResponse.getBoolean("TxIsVerified"));
                return jsonTxResponse.getBoolean("TxIsVerified");
            default:
        }
        return true;
    }

    public static final boolean logTransactions(TransactionBox tbox) throws IOException {
        String hexTransactionHash = ProjectHelper.convertToHex(tbox.getItb().getTransactionHash());

        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm"))) {
            FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm"));
        }

        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "pending"))) {
            FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "pending"));
        }

        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "succeeded"))) {
            FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "succeeded"));
        }

        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "failed"))) {
            FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "failed"));
        }

        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"))) {
            FileHelper.createDir(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"));
        }

        if (!FileHelper.writeStringToFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "pending"), hexTransactionHash, "", false)) {
            return false;
        }

        return FileHelper.writeStringToFile(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"), hexTransactionHash, tbox.getTransactionJson(), false);
    }

    public static final String generateMessageText(String[] tags, FilePropertiesBean fpb) throws IOException, TkmDataException {
        String messageText = null;
        return messageText;
    }

    public static final String createQRString(ReceiveTokenBalanceRequestBean rtbr) {
        String qrString;
        String message = rtbr.getqMessage();
        if (TkmTextUtils.isNullOrBlank(message)) {
            System.out.println("NULL message");
            qrString = TkmTextUtils.toJson(new RequestPaymentBean(
                    rtbr.getqColor(),
                    rtbr.getqValue(),
                    rtbr.getqAddr()));
        } else {
            message = message.trim();
            if (message.length() > 200) {
                message = message.substring(0, DefaultInitParameters.REQUEST_PAY_MESSAGE_LIMIT);
            }
            qrString = TkmTextUtils.toJson(new RequestPaymentBean(
                    rtbr.getqColor(),
                    rtbr.getqValue(),
                    rtbr.getqAddr(),
                    message
            ));
            System.out.println("QR STRING: " + qrString);
        }
        return qrString;
    }

    public static final String getQR(String qrString) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 250;
        int height = 250;
        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(qrString, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            System.out.println("Success...");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", out);
            byte[] bytes = out.toByteArray();

            String base64bytes = Base64.getEncoder().encodeToString(bytes);
            return base64bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
