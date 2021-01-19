/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.main.defaults.DefaultInitParameters;
import com.h2tcoin.takamakachain.utils.FileHelper;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class InternalParameters {

    private static final String internalWebWalletSettingsFolderName = "walletWeb";
    private static final String internalWebWalletSaltFileName = "sessions.salt";
    private static final String internalWebWalletPasswordFileName = "sessions.pass";
    private static final String internalWebWalletIVFileName = "sessions.iv";
    private static final String internalWebWalletSecretKeyFileName = "sessions.secretKey";

    public static String getInternalWebWalletPasswordFileName() {
        return internalWebWalletPasswordFileName;
    }

    public static final Path getInternalWebWalletPasswordFilePath() {
        return Paths.get(getInternalWebWalletSettingsFolderPath().toString(), internalWebWalletPasswordFileName);
    }

    public static final Path getInternalWebWalletSettingsFolderPath() {
        return Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), internalWebWalletSettingsFolderName);
    }

    public static final Path getInternalWebWalletSaltFilePath() {
        return Paths.get(getInternalWebWalletSettingsFolderPath().toString(), internalWebWalletSaltFileName);
    }
    
    public static final Path getInternalWebWalletIvParameterSpecFilePath() {
        return Paths.get(getInternalWebWalletSettingsFolderPath().toString(), internalWebWalletIVFileName);
    }
    
    public static final Path getInternalWebWalletSecretKeyFilePath() {
        return Paths.get(getInternalWebWalletSettingsFolderPath().toString(), internalWebWalletSecretKeyFileName);
    }

    public static String getInternalWebWalletSaltFileName() {
        return internalWebWalletSaltFileName;
    }

    public static String getInternalWebWalletSettingsFolderName() {
        return internalWebWalletSettingsFolderName;
    }

    public static String getInternalWebWalletIVFileName() {
        return internalWebWalletIVFileName;
    }

    public static String getInternalWebWalletSecretKeyFileName() {
        return internalWebWalletSecretKeyFileName;
    }
    
    

}
