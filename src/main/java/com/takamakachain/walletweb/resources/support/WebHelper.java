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
        CREATE_CAMPAIGN,
        CHECK_NEW_TRANSACTIONS,
        GET_NEW_MESSAGES,
        GET_REJECTED_MESSAGES,
        MOVE_TO_APPROVED,
        MOVE_TO_REJECTED,
        MOVE_TO_BLACKLIST,
        PAY,
        BLOB,
        STAKE,
        BLOB_RICH_TEXT,
        RECEIVE_TOKENS,
        SEND_TRX
    }
}
