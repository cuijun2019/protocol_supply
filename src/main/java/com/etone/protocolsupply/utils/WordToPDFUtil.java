package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;

public class WordToPDFUtil {


    public static void convert(String wordPath,String pdfPath) {

        // 1) Start LibreOffice in headless mode.
        OfficeManager officeManager = null;
        try {

            officeManager = OfficeManagerUtil.getOfficeManager();

            // 2) Create JODConverter converter
            OfficeDocumentConverter converter = new OfficeDocumentConverter(
                    officeManager);

            // 3) Create PDF
            createPDF(converter,wordPath,pdfPath);

        }catch (Exception e){
            e.printStackTrace();
        }/*finally {
            // 4) Stop LibreOffice in headless mode.
            if (officeManager != null) {
                officeManager.stop();
            }
        }*/
    }

    private static void createPDF(OfficeDocumentConverter converter,String wordPath,String pdfPath) {
        try {
            long start = System.currentTimeMillis();
            converter.convert(new File(wordPath), new File(
                    pdfPath));
            System.err.println("Generate pdf/HelloWorld.pdf with "
                    + (System.currentTimeMillis() - start) + "ms");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}