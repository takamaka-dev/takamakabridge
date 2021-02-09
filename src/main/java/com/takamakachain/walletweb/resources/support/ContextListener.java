/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author giovanni.antino@h2tcoin.com
 */
public class ContextListener implements ServletContextListener {

    public static ServletContext context;

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        context = null;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();

    }

}
