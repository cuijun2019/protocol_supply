package com.etone.protocolsupply.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description 角色实体
 * @Date 2018/12/12 下午2:43
 * @Author maozhihui
 * @Version V1.0
 **/
@Data
@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Long id;

    /**
     * 将枚举的字符串写入
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "NAME",nullable = false,length = 32)
    private RoleName name;

    @Column(name = "DESCRIPTION",length = 80)
    private String description;
}
