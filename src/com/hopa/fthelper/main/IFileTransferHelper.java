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
    //�Ӵ������ع��ļ���url��Ϣ�б���ļ��м��
    //public boolean checkDownloadFileExist(URL newUrl);

    //�洢��ַΪ��ǰ�ļ�·��,�����߳���Ϊ1
    //public void download(URL url, String fileName)
    
    //�����߳���Ϊ1
    public void download(URL url, String path, String fileName);

    public void download(URL url, String path, String fileName, int threadNum);

    //public void upload(URL url);
}
