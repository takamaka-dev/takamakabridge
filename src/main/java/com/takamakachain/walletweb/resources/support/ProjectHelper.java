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
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmSignUtils;
import static com.takamakachain.walletweb.resources.support.CryptoHelper.getWebSessionPassword;
import static com.takamakachain.walletweb.resources.support.InternalParameters.getInternalWebWalletSecretKeyFilePath;
import io.hotmoka.nodes.Node;
import io.takamaka.code.verification.IncompleteClasspathError;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class ProjectHelper {

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
        System.out.println("test salt " + getSalt("wallet_name"));
        System.out.println("test password " + getPassword("wallet_name"));
        IvParameterSpec ivps = getIVParameterSpec("wallet_name");
        SecretKey sk = getSecretKey("wallet_name");
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

    public static final SecretKey getSecretKey(String wallet_name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public static final SecretKey getWebSessionSecret(KeyStore ks) throws IOException {
        try {
            return getWebSessionPassword(ks);
        } catch (IOException | HashEncodeException | HashAlgorithmNotFoundException | HashProviderNotFoundException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            throw new IOException(ex);
        }
    }

}
