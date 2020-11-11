package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OfficeManagerUtil {

    @Value("${soft.LibreOffice.path}")
    private String libreOfficePath;

    private static final Logger logger = LoggerFactory.getLogger(OfficeManagerUtil.class);

    private OfficeManagerUtil(){}

    private static volatile OfficeManager officeManager;


    public OfficeManager getOfficeManager(){
        try{
            if (officeManager == null){
                synchronized (OfficeManagerUtil.class){
                    if(officeManager == null){
                        officeManager=  new DefaultOfficeManagerConfiguration()
                                .setOfficeHome(new File(libreOfficePath))
                                .buildOfficeManager();
                        officeManager.start();
                    }
                }
            }

        }catch (Exception e){
            logger.error("officeManager初始化失敗"+e);
        }
        return officeManager;
    }

}
