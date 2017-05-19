/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hopa.fthelper.core;

/**
 *
 * @author KID
 * @since 2017-5-17
 */
public class FileUpload extends Thread{

    @Override
    public void run() {
        
    }

    /**
     * 工厂方法
     * @return
     */
    public static FileUpload create() {
        return new FileUpload();
    }

    private FileUpload() {
        
    }
}
