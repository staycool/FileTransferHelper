/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hopa.fthelper.factory;

import com.hopa.fthelper.main.FileTransferHelper;

/**
 *
 * @author KID
 */
public class DownloadFactory {
    
    //创建DownloadHelper唯一实例
    public static FileTransferHelper create() {
        return FileTransferHelper.create();
    }
}
