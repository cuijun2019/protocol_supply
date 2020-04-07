package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.model.entity.Attachment;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    public static String getYYYYMMDDDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String convertSerial(String serial, int step) {
        if (Strings.isBlank(serial)) {
            return "0001";
        }
        serial = String.valueOf(Long.parseLong(serial) + step);
        if (serial.length() == 1) {
            return "000" + serial;
        } else if (serial.length() == 2) {
            return "00" + serial;
        } else if (serial.length() == 3) {
            return "0" + serial;
        }
        return serial;
    }

    public static String convertSerialProject(String serial, int step) {
        if (Strings.isBlank(serial)) {
            return "001";
        }
        serial = String.valueOf(Long.parseLong(serial) + step);
        if (serial.length() == 1) {
            return "00" + serial;
        } else if (serial.length() == 2) {
            return "0" + serial;
        }
        return serial;
    }


    public static <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
