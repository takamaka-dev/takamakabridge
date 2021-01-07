/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.h2tcoin.takamakachain.walletweb.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class JacksonHelper {

    public static final ObjectMapper getBeanMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        /*
        simpleModule.addSerializer(StorageValue.class, new ISStorageValueSerializer());
        simpleModule.addDeserializer(TransactionReference.class, new ISTransactionRefernceDeserializer());
        */
        mapper.registerModule(simpleModule);
        return mapper;
    }

    public static final ObjectWriter getBeanWriterPretty() {
        return getBeanMapper().writerWithDefaultPrettyPrinter();
    }
    
    

}
