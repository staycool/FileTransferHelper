/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hopa.fthelper.core;

import com.hopa.fthelper.bean.FileInfo;
import com.hopa.fthelper.bean.SiteInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author KID
 */
public class FileDownload extends Thread {

    private URL url;
    private FileInfo fileI;
    //�ļ��洢��ȫ��
    //private String fullName;

    //���̸߳���
    private int downThreadNum;
    //���̼߳���
    private PartFileDownload[] downThreads;

    //Ŀ���ļ�
    private File targetFile;
    //����������Ϣ����ʱ�ļ�
    private File tmpFile;

    long[] startPos;
    long[] endPos;

    //�Ƿ����������,Ĭ��false
    private boolean isCompleted;

    //�����ļ���ʼ�ͽ���λ�õ�ָ�������
    //��ֵ��Map����������,�ĸ��ڴ濪����?
    public FileDownload(SiteInfo siteInfo, FileInfo fileInfo, int downThreadNum) {
        this.url = siteInfo.getUrl();
        this.fileI = fileInfo;
        this.downThreadNum = downThreadNum;
        downThreads = new PartFileDownload[downThreadNum];

        //fullName = fileI.getFilePath() + File.separator + fileI.getFileName();
        int lastDotIndx = fileI.getFileName().lastIndexOf(".");
        String prefix = fileI.getFileName().substring(0, lastDotIndx);
        //String suffix = fileI.getFileName().substring(lastDotIndx + 1);
        tmpFile = new File(fileI.getFilePath() + File.separator + prefix + ".info");

        //���ж��Ƿ��Ѵ���ͬ�����ļ�,�����,����������
        //������1�ļ�����,��ʱ�ļ������ڵĿ���
        adjustFileNameForDuplicate(fileI);

        if (targetFile.exists() && tmpFile.exists()) {
            //��ʱ�ļ�������������ǵ�һ������,�����ػ�û���.
            isCompleted = false;
            readPositions();

        } else if (!targetFile.exists() && !tmpFile.exists()) {
            //��һ������
            startPos = new long[this.downThreadNum];
            endPos = new long[this.downThreadNum];

        } else if (!targetFile.exists() && tmpFile.exists()) {
            //��Ϣ�ļ�����,�����ļ���ʧ,��ɾ����Ϣ�ļ�
            boolean isDeleteCompleted = tmpFile.delete();
        }

    }

    public void adjustFileNameForDuplicate(FileInfo mFileInfo) {

        if (mFileInfo != null && mFileInfo.getFilePath() != null && mFileInfo.getFileName() != null) {

            int lastDotIndx = mFileInfo.getFileName().lastIndexOf(".");
            String prefix = mFileInfo.getFileName().substring(0, lastDotIndx);
            String suffix = mFileInfo.getFileName().substring(lastDotIndx + 1);

            targetFile = new File(mFileInfo.getFilePath() + File.separator + mFileInfo.getFileName());
            int count = 1;
            while (targetFile.exists()) {

                tmpFile = new File(mFileInfo.getFilePath() + File.separator
                        + mFileInfo.getFileName().substring(0, mFileInfo.getFileName().lastIndexOf(".")) + ".info");
                if (tmpFile.exists()) {
                    break;
                }
                String newPrefix = prefix + "(" + count + ")";
                mFileInfo.setFileName(newPrefix + "." + suffix);
                targetFile = new File(mFileInfo.getFilePath() + File.separator + mFileInfo.getFileName());
                count++;
            }

        }
    }

    @Override
    public void run() {
        try {
            
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //�������ӳ�ʱʱ��5s
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
            conn.setRequestProperty("Connection", "Keep-Alive");

            //��ȡ���ļ����ļ���С
            fileI.setFileSize(conn.getContentLengthLong());
            //�ر�����
            conn.disconnect();
            //�����ļ��Ĵ�С
            long partFileSize = fileI.getFileSize() / downThreadNum + 1;

            RandomAccessFile accessFile = new RandomAccessFile(fileI.getFilePath() + File.separator + fileI.getFileName(), "rw");
            accessFile.setLength(fileI.getFileSize());
            accessFile.close();

            for (int i = 0; i < downThreadNum; i++) {
                //����ÿ���߳����صĿ�ʼλ��
                
                //TODO ��һ�βų�ʼ��?
                if (i != downThreadNum - 1) {
                    startPos[i] = i * partFileSize;
                    endPos[i] = (i + 1) * partFileSize - 1;
                } else {
                    startPos[i] = i * partFileSize;
                    endPos[i] = fileI.getFileSize();
                }
                //ÿ���߳�ʹ��һ��RandomAccessFile��������
                RandomAccessFile partAccessFile = new RandomAccessFile(fileI.getFilePath() + File.separator + fileI.getFileName(), "rw");
                //��λ���̵߳�����λ��
                //partAccessFile.seek(startPos);
                //�����������߳�
                String filePathName = fileI.getFilePath() + File.separator + fileI.getFileName();
                downThreads[i] = new PartFileDownload(url.getPath(),
                        filePathName, this.startPos[i], endPos[i], partAccessFile);
                //�������߳�
                downThreads[i].start();
            }
        } catch (IOException ex) {
            //TODO
        }
    }

    //ͳ�����ؽ���
    public double getCompleteRate() {
        //ͳ�ƶ����߳��Ѿ����ص��ܴ�С
        //long sumSize
        int sumSize = 0;
        for (int i = 0; i < downThreadNum; i++) {
            if (downThreads[i] != null) {
                sumSize += downThreads[i].length;
            }
        }
        //�������ļ��ٷֱ�
        double percent = sumSize * 1.0 / fileI.getFileSize();
        return percent;
    }

    public void writePositions() {
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(tmpFile));
            output.writeInt(downThreadNum);

            for (int i = 0; i < downThreadNum; i++) {
                output.writeLong(downThreads[i].startPos);
                output.writeLong(downThreads[i].endPos);
            }
            output.close();
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
    }

    public void readPositions() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(tmpFile));
            int mThreadNum = input.readInt();
            startPos = new long[mThreadNum];
            endPos = new long[mThreadNum];
            for (int i = 0; i < downThreadNum; i++) {
                startPos[i] = input.readLong();
                endPos[i] = input.readLong();
            }
            input.close();
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
    }
}
