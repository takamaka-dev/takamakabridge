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
import io.hotmoka.nodes.Node;
import io.takamaka.code.verification.IncompleteClasspathError;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

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

    public static final String getSalt(String walletName) throws FileNotFoundException, IOException {
        String salt;
        //create internal settings folder
        if (!FileHelper.directoryExists(InternalParameters.getInternalWebWalletSettingsFolderPath())) {
            FileHelper.createDir(InternalParameters.getInternalWebWalletSettingsFolderPath());
        }
        //create salt file
        if (!saltFileExists()) {

            FileHelper.writeStringToFile(InternalParameters.getInternalWebWalletSettingsFolderPath(), InternalParameters.getInternalWebWalletSaltFileName(), CryptoHelper.getSaltString(),false);
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

}