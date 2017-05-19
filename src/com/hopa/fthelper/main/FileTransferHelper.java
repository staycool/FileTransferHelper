/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hopa.fthelper.main;

import com.hopa.fthelper.bean.FileInfo;
import com.hopa.fthelper.bean.SiteInfo;
import com.hopa.fthelper.core.FileDownload;
import com.hopa.fthelper.core.PartFileDownload;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * 多线程断点续传
 *
 * @author KID
 */
public class FileTransferHelper implements IFileTransferHelper {

    //FileTransferHelper唯一实例
    //private final static FileTransferHelper helper;
    private static FileTransferHelper helper;
    private SiteInfo siteInfo;
    private FileInfo fileInfo;

    //下载线程
    private FileDownload downloadThread;
    //子线程个数
    private int downThreadNum;
    //子线程集合
    private PartFileDownload[] downThreads;

    //构造函数
    private FileTransferHelper() {

    }

    //工厂方式
    public static FileTransferHelper create() {
        if (helper == null) {
            helper = new FileTransferHelper();
        }
        return helper;
    }

    /**
     *
     * @param url
     * @param path
     * @param fileName
     * @param threadNum
     */
    @Override
    public void download(URL url, String path, String fileName, int threadNum) {
        downloadThread = new FileDownload(new SiteInfo(url), new FileInfo(path, fileName), threadNum);
        downloadThread.start();

    }
    /*
    @Override
    public boolean checkDownloadFileExist(URL newUrl) {
        File listFile = new File("[FileName]");
        //read all url list from File [FileName]
        //ArrayList<String> urlList = from FileName
        ArrayList<String> urlList = null;
        if (urlList.stream().anyMatch((url) -> (url == newUrl.getPath()))) {
            return true;
        } //url.equals(newURL.getPath())用哪种方式比较?
        return false;
    }
     */

    @Override
    public void download(URL url, String path, String fileName) {
        download(url, path, fileName, 1);
    }
}
