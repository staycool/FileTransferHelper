/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hopa.fthelper.main;

import java.net.URL;

/**
 *
 * @author KID
 */
public interface IFileTransferHelper {
    //从存有下载过文件的url信息列表的文件中检查
    //public boolean checkDownloadFileExist(URL newUrl);

    //存储地址为当前文件路径,启动线程数为1
    //public void download(URL url, String fileName)
    
    //启动线程数为1
    public void download(URL url, String path, String fileName);

    public void download(URL url, String path, String fileName, int threadNum);

    //public void upload(URL url);
}
