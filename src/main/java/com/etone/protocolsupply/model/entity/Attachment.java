package com.etone.protocolsupply.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author cuijun
 * @date 2018/12/28
 */
@Data
@Entity
@Table(name = "ATTACHMENT")
public class Attachment implements Serializable {

    @Id
    @Column(name = "ATTACH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachId;

    /**
     * 文件类型
     */
    @Column(name = "FILE_TYPE", length = 100)
    private String fileType;

    /**
     * 上传人id
     */
    @Column(name = "UPLOADER", length = 32)
    private String uploader;

    /**
     * 附件名
     */
    @Column(name = "ATTACH_NAME", length = 128)
    private String attachName;

    /**
     * 附件大小
     */
    @Column(name = "ATTACH_SIZE", length = 32)
    private Long attachSize;

    /**
     * 上传时间
     */
    @Column(name = "UPLOAD_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTime;

    /**
     * 文件路径
     */
    @Column(name = "PATH", length = 256)
    private String path;

    /**
     * 加密文件密码
     */
    @Column(name = "PASSWORD", length = 255)
    private String password;


    /**
     * 是否发送加密文件的密码邮件  0未发送 ，1已发送
     */
    @Column(name = "IS_SENDEMAIL", length = 4)
    private Integer isSendEmail;

    /**
     * 项目编号
     */
    @Column(name = "PROJECTCODE", length = 255)
    private String projectCode;
}
