package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.model.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author cuijun
 */
public class Common {
    /**
     * 上传文件
     *
     * @param file
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Attachment saveUploadedFiles(MultipartFile file, String filePath, String userName) throws Exception {
        File uploadPath = new File(filePath);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        if (file.isEmpty()) {
            throw new NullPointerException();
        }

        byte[] bytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        String fileFullPath = uploadPath.getAbsolutePath() + "/" + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        System.out.println("UploadPath Url:" + fileFullPath);
        Path path = Paths.get(fileFullPath);
        Files.write(path, bytes);

        Attachment attachment = new Attachment();
        attachment.setAttachName(fileName);
        attachment.setAttachSize(file.getSize());
        attachment.setPath(fileFullPath);
        attachment.setFileType(file.getContentType());
        attachment.setUploader(userName);
        attachment.setUploadTime(new Date());

        return attachment;
    }

    public static String getYYYYMMDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        return format.format(date);
    }
}
