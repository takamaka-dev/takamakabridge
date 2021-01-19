/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.saturn.SatUtils;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.utils.FileHelper;
import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class CryptoHelper {

    public static final String getSaltString() {
        return RandomStringUtils.randomAlphanumeric(30, 30);
    }

    public static final IvParameterSpec generateNewIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
    //public static final SecretKey getNewAesSecretKey()

}
