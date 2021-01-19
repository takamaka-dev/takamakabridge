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

    public static final Path getInternalWebWalletSettingsFolderPath() {
        return Paths.get(FileHelper.getDefaultApplicationDirectoryPath().toString(), internalWebWalletSettingsFolderName);
    }

    public static final Path getInternalWebWalletSaltFilePath() {
        return Paths.get(getInternalWebWalletSettingsFolderPath().toString(), internalWebWalletSaltFileName);
    }

    public static String getInternalWebWalletSaltFileName() {
        return internalWebWalletSaltFileName;
    }

    public static String getInternalWebWalletSettingsFolderName() {
        return internalWebWalletSettingsFolderName;
    }

}
