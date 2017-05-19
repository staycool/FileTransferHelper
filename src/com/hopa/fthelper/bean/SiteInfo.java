/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hopa.fthelper.bean;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KID
 * @since 2017-5-17
 */
public class SiteInfo {
    //
    private String urlPath;
    private URL url;
    
    public final static String REQUEST_METHOD_GET = "GET";
    public final static String REQUEST_METHOD_POST = "POST";
    
    public SiteInfo(String path) {
        try {
            this.urlPath = path;
            this.url = new URL(path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SiteInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public SiteInfo(URL url) {
        this.url = url;
        this.urlPath = this.url.getPath();
    }

    public String getUrlPath() {
        return urlPath;
    }

    public URL getUrl() {
        return url;
    }
}
