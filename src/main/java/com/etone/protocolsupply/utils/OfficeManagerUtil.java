package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class OfficeManagerUtil {

    private static final Logger logger = LoggerFactory.getLogger(OfficeManagerUtil.class);

    private OfficeManagerUtil(){}

    private static volatile OfficeManager officeManager;


    public static OfficeManager getOfficeManager(){
        try{
            if (officeManager == null){
                synchronized (OfficeManagerUtil.class){
                    if(officeManager == null){
                        officeManager=  new DefaultOfficeManagerConfiguration()
                                .setOfficeHome(new File("C:/Program Files/LibreOffice"))
                                .buildOfficeManager();
                        officeManager.start();
                    }
                }
            }

        }catch (Exception e){
            logger.error("officeManager初始化失敗"+e.getMessage());
        }
        return officeManager;
    }

}
