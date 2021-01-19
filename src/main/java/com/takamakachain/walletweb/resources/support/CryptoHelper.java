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
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class CryptoHelper {

    public static final String getSaltString() {
        return RandomStringUtils.randomAlphanumeric(30, 30);
    }
}
