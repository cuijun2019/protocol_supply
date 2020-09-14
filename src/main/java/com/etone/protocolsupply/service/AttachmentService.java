package com.etone.protocolsupply.service;

import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class AttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment upload(MultipartFile uploadFile, JwtUser jwtUser) {
        try {
//            保存文件到本地
            return Common.saveUploadedFiles(uploadFile, uploadFilePath + Common.getYYYYMMDate(new Date()), jwtUser.getUsername());
        } catch (Exception e) {
            logger.error("文件下载异常",e.getMessage());
        }
        return null;
    }

    public Attachment save(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public void download(HttpServletResponse response, Long attachId) throws UnsupportedEncodingException {
        Optional<Attachment> optional = attachmentRepository.findById(attachId);
        if (optional.isPresent()) {
            Attachment attachment = optional.get();
            String path = attachment.getPath();
            String fileName = attachment.getAttachName();
            File file = new File(path);
            if (file.exists()) {
                String type = new MimetypesFileTypeMap().getContentType(fileName);
                // 设置contenttype，即告诉客户端所发送的数据属于什么类型
                response.setHeader("Content-type", type);
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=utf-8''"
                        + URLEncoder.encode(fileName, "utf-8"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void downloadByName(HttpServletResponse response, String attachName) throws UnsupportedEncodingException {
        Attachment attachment= attachmentRepository.findAttachmentByName(attachName);
        if (null!=attachment) {
            String path = attachment.getPath();
            String fileName = attachment.getAttachName();
            File file = new File(path);
            if (file.exists()) {
                String type = new MimetypesFileTypeMap().getContentType(fileName);
                // 设置contenttype，即告诉客户端所发送的数据属于什么类型
                response.setHeader("Content-type", type);
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=utf-8''"
                        + URLEncoder.encode(fileName, "utf-8"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public Attachment findByResultTemplate() {
        return attachmentRepository.findByResultTemplate();
    }

    public Attachment findBidTemplate() {
        return attachmentRepository.findBidTemplate();
    }

    public Attachment findContractTemplate() {
        return attachmentRepository.findContractTemplate();
    }
}
