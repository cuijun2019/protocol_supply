package com.etone.protocolsupply.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class EncryptZipUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncryptZipUtil.class);

    public static String zipFile(String zipPath,String filePath)  {

        String password="";

        try {
            // 生成的压缩文件
            ZipFile zipFile = new ZipFile(zipPath);
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            //随机6位密码
            password=String.format("%06d",(int)(Math.random()* (999999)+1));
            parameters.setPassword(password);

            zipFile.addFile(new File(filePath),parameters);

            return password;
        }catch (Exception e){
            logger.error("文件压缩加密出错-",e);
            return "添加压缩文件出错";
        }
    }
}
