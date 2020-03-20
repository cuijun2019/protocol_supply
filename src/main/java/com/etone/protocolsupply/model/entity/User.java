package com.etone.protocolsupply.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @Description //TODO
 * @Date 2018/12/2 下午5:12
 * @Author maozhihui
 * @Version V1.0
 **/
@Data
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", length = 50, unique = true)
    @NotNull
    @Size(min = 4, max = 50)
    private String username;

    @Column(name = "PASSWORD", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    @Column(name = "FULLNAME", length = 50)
    @NotNull
    private String fullname;

    @Column(name = "TELEPHONE", length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String telephone;

    @Column(name = "ENABLED", length = 8)
    @NotNull
    private Boolean enabled;

    /**
     * 业主公司
     */
    @Column(name = "OWNER")
    private String owner;

    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")}
    )
    private List<Role> roles;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;
}
