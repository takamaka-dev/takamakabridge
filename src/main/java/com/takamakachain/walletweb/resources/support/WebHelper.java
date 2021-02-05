/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class WebHelper {

    public static enum RequestType {

        /**
         *
         */
        GET_ADDRESS,
        RECOVER_WALLET,
        PAY,
        BLOB,
        BLOB_RICH_TEXT,
        RECEIVE_TOKENS,
        SEND_TRX
    }
}
