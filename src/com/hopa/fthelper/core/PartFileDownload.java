/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hopa.fthelper.core;

import com.hopa.fthelper.bean.FileInfo;
import com.hopa.fthelper.bean.SiteInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KID
 * @since 2017-5-17
 */
public class PartFileDownload extends Thread {
    public int length;
    public long startPos;
    public long endPos;
    private URL url;
    private FileInfo fileI;
    private RandomAccessFile targetFile;
    
    public PartFileDownload(String urlPath, String filePathName, long start, long end, RandomAccessFile partAccessFile) {
        try {
            this.url = new URL(urlPath);
            //fileI = new FileInfo(urlPath, urlPath)
            this.startPos = start;
            this.endPos = end;
            targetFile = partAccessFile;
        } catch (MalformedURLException ex) {
            Logger.getLogger(PartFileDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

    
    @Override
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 5);
            conn.setRequestMethod(SiteInfo.REQUEST_METHOD_GET);
            conn.setRequestProperty("Accept",
                    "image/gif, image/jpeg, image/pjpeg, "
                    + "application/x-shockwave-flash, application/xaml+xml, "
                    + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                    + "application/x-ms-application, application/vnd.ms-excel, "
                    + "application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("RANGE", "bytes=" + startPos + "-");
            
            InputStream is = conn.getInputStream();
            byte[] buff = new byte[1024];
            int hasRead = 0;
            
            while ((hasRead = is.read(buff))>0 && startPos<endPos /*&&!bstop*/) {                
                targetFile.write(buff, 0, hasRead);
                startPos += hasRead;
            }
            
        } catch (IOException ex) {
            
        }
    }

}
