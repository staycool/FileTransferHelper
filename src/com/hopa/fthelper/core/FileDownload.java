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
    //文件存储的全名
    //private String fullName;

    //子线程个数
    private int downThreadNum;
    //子线程集合
    private PartFileDownload[] downThreads;

    //目标文件
    private File targetFile;
    //保存下载信息的临时文件
    private File tmpFile;

    long[] startPos;
    long[] endPos;

    //是否已完成下载,默认false
    private boolean isCompleted;

    //保存文件开始和结束位置的指针的数组
    //键值对Map与两个数组,哪个内存开销大?
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

        //先判断是否已存在同名的文件,如果有,则先重命名
        //过滤了1文件存在,临时文件不存在的可能
        adjustFileNameForDuplicate(fileI);

        if (targetFile.exists() && tmpFile.exists()) {
            //临时文件存在则表明不是第一次下载,且下载还没完成.
            isCompleted = false;
            readPositions();

        } else if (!targetFile.exists() && !tmpFile.exists()) {
            //第一次下载
            startPos = new long[this.downThreadNum];
            endPos = new long[this.downThreadNum];

        } else if (!targetFile.exists() && tmpFile.exists()) {
            //信息文件存在,下载文件丢失,则删除信息文件
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
            //设置连接超时时间5s
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

            //获取大文件的文件大小
            fileI.setFileSize(conn.getContentLengthLong());
            //关闭连接
            conn.disconnect();
            //部分文件的大小
            long partFileSize = fileI.getFileSize() / downThreadNum + 1;

            RandomAccessFile accessFile = new RandomAccessFile(fileI.getFilePath() + File.separator + fileI.getFileName(), "rw");
            accessFile.setLength(fileI.getFileSize());
            accessFile.close();

            for (int i = 0; i < downThreadNum; i++) {
                //计算每条线程下载的开始位置
                
                //TODO 第一次才初始化?
                if (i != downThreadNum - 1) {
                    startPos[i] = i * partFileSize;
                    endPos[i] = (i + 1) * partFileSize - 1;
                } else {
                    startPos[i] = i * partFileSize;
                    endPos[i] = fileI.getFileSize();
                }
                //每条线程使用一个RandomAccessFile进行下载
                RandomAccessFile partAccessFile = new RandomAccessFile(fileI.getFilePath() + File.separator + fileI.getFileName(), "rw");
                //定位该线程的下载位置
                //partAccessFile.seek(startPos);
                //创建下载子线程
                String filePathName = fileI.getFilePath() + File.separator + fileI.getFileName();
                downThreads[i] = new PartFileDownload(url.getPath(),
                        filePathName, this.startPos[i], endPos[i], partAccessFile);
                //启动子线程
                downThreads[i].start();
            }
        } catch (IOException ex) {
            //TODO
        }
    }

    //统计下载进度
    public double getCompleteRate() {
        //统计多条线程已经下载的总大小
        //long sumSize
        int sumSize = 0;
        for (int i = 0; i < downThreadNum; i++) {
            if (downThreads[i] != null) {
                sumSize += downThreads[i].length;
            }
        }
        //已下载文件百分比
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
