package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OfficeManagerUtil {

    private OfficeManagerUtil(){}

    private static OfficeManager officeManager;

    static {
        officeManager=  new DefaultOfficeManagerConfiguration()
                .setOfficeHome(new File("C:/Program Files/LibreOffice"))
                .buildOfficeManager();
        officeManager.start();
        System.out.println("officeManager实例初始化完成");
    }

    public static OfficeManager getOfficeManager(){
        return officeManager;
    }

}
