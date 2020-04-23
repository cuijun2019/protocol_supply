package com.etone.protocolsupply.model.entity.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "PERMISSIONS")
public class Permissions implements Serializable {

    @Id
    @Column(name = "PERM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permId;

    @Column(name = "PARENT_PERM_ID")
    private Long parentPermId;

    @Column(name = "MENU_CODE", length = 100)
    private String menuCode;

    @Column(name = "MENU_NAME", length = 100)
    private String menuName;

    @Column(name = "MENU_URL", length = 500)
    private String menuUrl;

    @Column(name = "STATUS", length = 4)
    private Integer status;

    @Column(name = "CREATOR", length = 32)
    private String creator;

    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    @Column(name = "ICON" ,length = 500)
    private String icon;
}
