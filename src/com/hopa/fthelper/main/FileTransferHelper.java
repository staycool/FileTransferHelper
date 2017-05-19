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
 * ���̶߳ϵ�����
 *
 * @author KID
 */
public class FileTransferHelper implements IFileTransferHelper {

    //FileTransferHelperΨһʵ��
    //private final static FileTransferHelper helper;
    private static FileTransferHelper helper;
    private SiteInfo siteInfo;
    private FileInfo fileInfo;

    //�����߳�
    private FileDownload downloadThread;
    //���̸߳���
    private int downThreadNum;
    //���̼߳���
    private PartFileDownload[] downThreads;

    //���캯��
    private FileTransferHelper() {

    }

    //������ʽ
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
        } //url.equals(newURL.getPath())�����ַ�ʽ�Ƚ�?
        return false;
    }
     */

    @Override
    public void download(URL url, String path, String fileName) {
        download(url, path, fileName, 1);
    }
}
