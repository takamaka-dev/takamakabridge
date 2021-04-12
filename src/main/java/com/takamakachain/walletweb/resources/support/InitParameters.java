/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.json.JsonReader;
import static com.takamakachain.walletweb.resources.support.InternalParameters.getWalletWebConfigFileName;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author isacco
 */
public class InitParameters {

    //public static ConcurrentSkipListMap<String, String> STARTUP_PARAMETERS = new ConcurrentSkipListMap<String, String>();

    public static boolean ENABLE_CAMPAIGN_SUPPORT = false;

    public boolean isENABLE_CAMPAIGN_SUPPORT() {
        return ENABLE_CAMPAIGN_SUPPORT;
    }

    public void setENABLE_CAMPAIGN_SUPPORT(boolean ENABLE_CAMPAIGN_SUPPORT) {
        InitParameters.ENABLE_CAMPAIGN_SUPPORT = ENABLE_CAMPAIGN_SUPPORT;
    }

}
