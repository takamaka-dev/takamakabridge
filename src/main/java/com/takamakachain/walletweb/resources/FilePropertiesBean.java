/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.takamakachain.walletweb.resources;

import com.h2tcoin.takamakachain.utils.F;
import com.h2tcoin.takamakachain.utils.simpleWallet.panels.support.FilePropertyKeyValueBean;
import com.h2tcoin.takamakachain.utils.threadSafeUtils.TkmTextUtils;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author isacco
 */
public class FilePropertiesBean {
    private ConcurrentSkipListMap<String, FilePropertyKeyValueBean> metas;

    public ConcurrentSkipListMap<String, FilePropertyKeyValueBean> getMetas() {
        return metas;
    }

    public void setMetas(ConcurrentSkipListMap<String, FilePropertyKeyValueBean> metas) {
        this.metas = metas;
    }
    private static final Object LOCK = new Object();
    private String fileSize;
    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    
    public FilePropertiesBean() {
        metas = new ConcurrentSkipListMap<String, FilePropertyKeyValueBean>();
    }

    public void add(String key, String value, boolean mandatory) {
        add(key, value, mandatory, mandatory);
    }

    public void add(String key, String value, boolean mandatory, boolean selected) {
        if (TkmTextUtils.isNullOrBlank(key)) {
            F.r("null value in key");
            return;
        }
        synchronized (LOCK) {
            metas.put(key, new FilePropertyKeyValueBean(selected, mandatory, key, value));
        }
    }

    /**
     * return selected and mandatory
     *
     * @return
     */
    public ConcurrentSkipListMap<String, String> getSelected() {
        synchronized (LOCK) {
            ConcurrentSkipListMap<String, String> res = new ConcurrentSkipListMap<String, String>();
            metas.keySet().parallelStream().forEach((String metaName) -> {
                FilePropertyKeyValueBean meta = metas.get(metaName);
                if (meta.isMandatory() || meta.isInclude()) {
                    res.put(meta.getKey(), meta.getValue());
                }
            });
            return res;
        }
    }

    public ConcurrentSkipListMap<String, String> getAllProperties() {
        synchronized (LOCK) {
            ConcurrentSkipListMap<String, String> res = new ConcurrentSkipListMap<String, String>();
            metas.keySet().parallelStream().forEach((String metaName) -> {
                FilePropertyKeyValueBean meta = metas.get(metaName);
                res.put(meta.getKey(), meta.getValue());
            });
            return res;
        }
    }

    public ConcurrentSkipListSet<FilePropertyKeyValueBean> getAll() {
        synchronized (LOCK) {
            ConcurrentSkipListSet<FilePropertyKeyValueBean> res = new ConcurrentSkipListSet<FilePropertyKeyValueBean>();
            metas.keySet().parallelStream().forEach((String metaName) -> {
                FilePropertyKeyValueBean meta = metas.get(metaName);
                res.add(meta);
            });
            return res;
        }
    }
}
