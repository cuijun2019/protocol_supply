package com.etone.protocolsupply.model.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "SCUT_USER")
public class ScutUser {

    @Id
    @Column(name = "GH",length = 10)
    @NotNull
    private String gh; //工号   主键

    @Column(name = "XM")
    private String xm;//姓名

    @Column(name = "DWMC")
    private String dwmc; //单位

    @Column(name = "DZYX")
    private String dzyx;//电子邮箱

    @Column(name = "LXDH")
    private String lxdh;//联系电话
}
