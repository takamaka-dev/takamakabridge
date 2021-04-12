/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

/**
 *
 * @author isacco
 */
public class InitParameters {

    public static boolean ENABLE_CAMPAIGN_SUPPORT = false;

    public boolean isENABLE_CAMPAIGN_SUPPORT() {
        return ENABLE_CAMPAIGN_SUPPORT;
    }

    public void setENABLE_CAMPAIGN_SUPPORT(boolean ENABLE_CAMPAIGN_SUPPORT) {
        InitParameters.ENABLE_CAMPAIGN_SUPPORT = ENABLE_CAMPAIGN_SUPPORT;
    }

}
