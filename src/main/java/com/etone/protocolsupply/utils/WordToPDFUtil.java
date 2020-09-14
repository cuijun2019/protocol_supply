package com.etone.protocolsupply.utils;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WordToPDFUtil {

    private static final Logger logger = LoggerFactory.getLogger(WordToPDFUtil.class);

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
            logger.error("word文档转化PDF异常",e);
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
        } catch (Exception e) {
            logger.error("PDF生成失败",e);
        }
    }
}