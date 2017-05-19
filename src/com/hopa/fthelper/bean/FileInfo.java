/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hopa.fthelper.bean;

/**
 *
 * @author KID
 * @since 2017-5-17
 */
public class FileInfo {

    private String filePath;
    private String fileName;
    private long fileSize;

    public FileInfo() {

    }

    public FileInfo(String path, String name) {
        this.filePath = path;
        this.fileName = name;

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
