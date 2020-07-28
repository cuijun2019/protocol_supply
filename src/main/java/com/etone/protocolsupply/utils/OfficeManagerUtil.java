package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;


public class OfficeManagerUtil {

    private OfficeManagerUtil(){}

    private static volatile OfficeManager officeManager;


    public static OfficeManager getOfficeManager(){
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

        return officeManager;
    }

}
