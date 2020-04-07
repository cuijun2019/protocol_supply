package com.etone.protocolsupply.model.entity.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

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
     * 角色名称
     * 将枚举的字符串写入
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", nullable = false, length = 32)
    private RoleName name;

    /**
     * 角色描述
     */
    @Column(name = "DESCRIPTION", length = 80)
    private String description;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ROLE_PERMIS",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PERM_ID", referencedColumnName = "PERM_ID")}
    )
    private Set<Permissions> permissions;
}
