/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import com.h2tcoin.takamakachain.main.defaults.DefaultInitParameters;
import com.h2tcoin.takamakachain.saturn.SatUtils;
import com.h2tcoin.takamakachain.saturn.exceptions.SaturnException;
import com.h2tcoin.takamakachain.utils.FileHelper;
import io.hotmoka.nodes.Node;
import io.takamaka.code.verification.IncompleteClasspathError;
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

    public static final void initProject(String rootFolder) throws IOException, SaturnException, ClassNotFoundException, URISyntaxException {
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
    }

}
