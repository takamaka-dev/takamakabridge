/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashAlgorithmNotFoundException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashEncodeException;
import com.h2tcoin.takamakachain.exceptions.threadSafeUtils.HashProviderNotFoundException;
import com.h2tcoin.takamakachain.main.defaults.DefaultInitParameters;
import com.h2tcoin.takamakachain.saturn.SatUtils;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.utils.FileHelper;
import com.h2tcoin.takamakachain.utils.simpleWallet.SWTracker;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class ProjectHelper {

    public static final String ENC_LABEL = "isEncriptedPasswordWithAES256";
    public static final String ENC_SEP = "§";

    public static final void initProject(String rootFolder) throws IOException, SaturnException, ClassNotFoundException, URISyntaxException, HashEncodeException, HashAlgorithmNotFoundException, HashProviderNotFoundException {
//        Package[] definedPackages = Thread.currentThread().getContextClassLoader().getDefinedPackages();
        //DefaultInitParameters.TAKAMAKA_CODE_JAR_RESOURCE
//        URL resource = Thread.currentThread().getContextClassLoader().getResource(DefaultInitParameters.TAKAMAKA_CODE_JAR_RESOURCE);
//        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DefaultInitParameters.TAKAMAKA_CODE_JAR_RESOURCE);
//
//        System.out.println("PKGS: ");
//        Arrays.stream(definedPackages).forEachOrdered(p->{
//            System.out.println("PKG: " + p.getName());
//        });
        FileHelper.initProjectFiles();
        SatUtils.loadConfig(rootFolder);
        //System.out.println("N:" + incompleteClasspathError.toString());
        System.out.println("Current Application Folder Dir: " + FileHelper.getDefaultApplicationDirectoryPath().toString());
        //load salt
        System.out.println("init salt file ");
        getSalt("wallet_name");
        System.out.println("init password file ");
        getPassword("wallet_name");
//        IvParameterSpec ivps = getIVParameterSpec("wallet_name");
//        SecretKey sk = getSecretKey("wallet_name");
    }

    public static final boolean saltFileExists() {
        //boolean file = InternalParameters.getInternalWebWalletSaltFilePath().toFile().isFile();
        //System.out.println("Salt file exists " + file);
        return InternalParameters.getInternalWebWalletSaltFilePath().toFile().isFile();
    }

    public static final boolean passwordFileExists() {
        //boolean file = InternalParameters.getInternalWebWalletSaltFilePath().toFile().isFile();
        //System.out.println("Salt file exists " + file);
        return InternalParameters.getInternalWebWalletPasswordFilePath().toFile().isFile();
    }

    public static final boolean fileExists(Path filePath) {
        return filePath.toFile().isFile();
    }

    public static final boolean fileExists(String filePath) {
        return Paths.get(filePath).toFile().isFile();
    }

    public static final String getSalt(String walletName) throws FileNotFoundException, IOException {
        String salt;
        //create internal settings folder
        if (!FileHelper.directoryExists(InternalParameters.getInternalWebWalletSettingsFolderPath())) {
            FileHelper.createDir(InternalParameters.getInternalWebWalletSettingsFolderPath());
        }
        //create salt file
        if (!saltFileExists()) {

            FileHelper.writeStringToFile(InternalParameters.getInternalWebWalletSettingsFolderPath(), InternalParameters.getInternalWebWalletSaltFileName(), CryptoHelper.getSaltString(), false);
        }
        salt = FileHelper.readStringFromFile(InternalParameters.getInternalWebWalletSaltFilePath());

        return walletName + salt;
        //return "lol";
    }

    public static final String getPassword(String walletName) throws IOException, HashEncodeException, HashAlgorithmNotFoundException, HashProviderNotFoundException {
        String password;
        //create internal settings folder
        if (!FileHelper.directoryExists(InternalParameters.getInternalWebWalletSettingsFolderPath())) {
            FileHelper.createDir(InternalParameters.getInternalWebWalletSettingsFolderPath());
        }
        //create password file
        if (!passwordFileExists()) {

            FileHelper.writeStringToFile(
                    InternalParameters.getInternalWebWalletSettingsFolderPath(),
                    InternalParameters.getInternalWebWalletPasswordFileName(),
                    CryptoHelper.getSaltString(),
                    false);
        }
        password = FileHelper.readStringFromFile(InternalParameters.getInternalWebWalletSaltFilePath());
        String mixPass = TkmSignUtils.Hash512ToHex(walletName + password);

        return mixPass;
    }

    public static final SecretKey getSecretKey(String wallet_name) throws NoSuchAlgorithmException, IOException, KeyStoreException, HashEncodeException, HashAlgorithmNotFoundException, CertificateException, HashProviderNotFoundException, UnrecoverableKeyException {
        SecretKey sk;

        if (!FileHelper.directoryExists(InternalParameters.getInternalWebWalletSettingsFolderPath())) {
            FileHelper.createDir(InternalParameters.getInternalWebWalletSettingsFolderPath());
        }
        //method creates a new file if it doesn't exist and returns the keystore object if it did exist
        KeyStore ks = CryptoHelper.getKeyStoreOrNew(InternalParameters.getInternalWebWalletSecretKeyFilePath());

        sk = CryptoHelper.getWebSessionSecret(ks);

        return sk;
    }

    public static final IvParameterSpec getIVParameterSpec(String wallet_name) throws IOException {
        IvParameterSpec ivParamSpec;
        //create internal settings folder
        if (!FileHelper.directoryExists(InternalParameters.getInternalWebWalletSettingsFolderPath())) {
            FileHelper.createDir(InternalParameters.getInternalWebWalletSettingsFolderPath());
        }
        //create password file
        if (!fileExists(InternalParameters.getInternalWebWalletIvParameterSpecFilePath())) {
            //generate iv parameter spec, transform it to its hex equivalent and save it in the file at the specified path
            ivParamSpec = CryptoHelper.generateNewIv();
            String ivHex = CryptoHelper.ivToHex(ivParamSpec);

            FileHelper.writeStringToFile(
                    InternalParameters.getInternalWebWalletSettingsFolderPath(),
                    InternalParameters.getInternalWebWalletIVFileName(),
                    ivHex,
                    false);
        }
        ivParamSpec = CryptoHelper.hexToIv(FileHelper.readStringFromFile(InternalParameters.getInternalWebWalletIvParameterSpecFilePath()));

        return ivParamSpec;
    }

    public static final KeyStore getInternalKeystore() throws IOException {
        KeyStore keyStore;
        try {
            keyStore = CryptoHelper.getKeyStoreOrNew(InternalParameters.getInternalWebWalletSecretKeyFilePath());
        } catch (KeyStoreException | IOException | HashEncodeException | HashAlgorithmNotFoundException | HashProviderNotFoundException | NoSuchAlgorithmException | CertificateException ex) {
            throw new IOException(ex);
        }
        return keyStore;
    }

    /*
    public static final SecretKey getWebSessionSecret(KeyStore ks) throws IOException {
        try {
            return getWebSessionSecret(ks);
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
     */
    public static final String doPost(String passedUrl, String key, String param) throws MalformedURLException, ProtocolException, IOException {
        String r = null;
        URL url = new URL(passedUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (!TkmTextUtils.isNullOrBlank(key) && !TkmTextUtils.isNullOrBlank(param)) {
            String data = key + "=" + param;

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);
        }

        int status = http.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                r = sb.toString();
                break;
            default:
                return null;
        }

        http.disconnect();

        return r;
    }

    public static String convertToStringRepresentationFileSize(final long value) {
        long K = 1024;
        long M = K * K;
        long G = M * K;
        long T = G * K;
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "Byte"};
        if (value < 1) {
            throw new IllegalArgumentException("Invalid file size: " + value);
        }
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value,
            final long divider,
            final String unit) {
        final double result
                = divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", Double.valueOf(result), unit);
    }

    public static final JSONObject isJSONValid(String test) {
        try {
            return new JSONObject(test);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final JSONArray getJsonArrayObject(String test) {
        try {
            return new JSONArray(test);
        } catch (JSONException ex1) {
            return null;
        }
    }

    public static final String convertToHex(String str) {
        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        return result;
    }

    public static final void createFileCollectionTransactionHash(String targetFolder, HashMap<String, String[]> result) {
        if (FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", targetFolder))) {
            String[] files = FileHelper.getFileNameList(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", targetFolder));
            List<String> convFiles = new ArrayList<String>();
            for (String file : files) {
                String fileName = Paths.get(file).getFileName().toString();
                String convertedFileName = TkmSignUtils.fromHexToString(fileName);
                convFiles.add(convertedFileName);
            }

            result.put(targetFolder, convFiles.toArray(String[]::new));
        }
    }

    public static final HashMap<String, String[]> manageListTransactions(String param) throws IOException {
        HashMap<String, String[]> result = new HashMap<>();
        if (TkmTextUtils.isNullOrBlank(param)) {
            String[] targetFolders = {"transactions", "pending", "succeeded", "failed"};
            for (String targetFolder : targetFolders) {
                createFileCollectionTransactionHash(targetFolder, result);
            }
            return result;
        }
        createFileCollectionTransactionHash(param, result);
        return result;
    }

    public static final HashMap<String, String> manageGetTransactions(String[] targets) throws FileNotFoundException {
        HashMap<String, String> result = new HashMap<>();
        if (targets.length == 0) {
            return null;
        }
        if (!FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"))) {
            return null;
        }

        for (String absolutePathFile : FileHelper.getFileNameList(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"))) {
            String fileName = Paths.get(absolutePathFile).getFileName().toString();
            String hexToStringFileName = TkmSignUtils.fromHexToString(fileName);
            if (Arrays.asList(targets).contains(hexToStringFileName)) {
                result.put(hexToStringFileName, FileHelper.readStringFromFile(Paths.get(absolutePathFile)));
            }

        }

        return result;
    }

    public static final boolean manageDeleteTransactions(String[] targets) throws IOException {
        if (targets.length == 0) {
            return false;
        }

        String[] targetFolders = {"transactions", "pending", "succeeded", "failed"};
        for (String singleHash : targets) {
            for (String singleTargetFolder : targetFolders) {
                if (FileHelper.fileExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", singleTargetFolder, convertToHex(singleHash)))) {
                    FileHelper.delete(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", singleTargetFolder, convertToHex(singleHash)));
                }
            }
        }

        if (FileHelper.directoryExists(Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), "idm", "transactions"))) {

        }

        return true;
    }

    public static Path getCurrentWalletpath(String wname) {
        String currentWalletName = wname + DefaultInitParameters.WALLET_EXTENSION;
        Path currentWalletPath = Paths.get(FileHelper.getDefaultWalletDirectoryPath().toString(), currentWalletName);
        return currentWalletPath;
    }

}
