package com.etone.protocolsupply.model.entity.user;

import com.etone.protocolsupply.model.entity.Attachment;
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

    /**
     * 用户名
     */
    @Column(name = "USERNAME", length = 50, unique = true)
    @NotNull
    @Size(min = 4, max = 50)
    private String username;

    @Column(name = "PASSWORD", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    /**
     * 用户姓名
     */
    @Column(name = "FULLNAME", length = 50)
    @NotNull
    private String fullname;

    /**
     * 性别
     */
    @Column(name = "SEX", length = 10)
    @NotNull
    private String sex;

    /**
     * 所在公司
     */
    @Column(name = "COMPANY", length = 100)
    @NotNull
    private String company;

    /**
     * 联系电话
     */
    @Column(name = "TELEPHONE", length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String telephone;

    /**
     * 邮箱
     */
    @Column(name = "EMAIL", length = 50)
    @NotNull
    private String email;

    /**
     * 状态
     */
    @Column(name = "ENABLED", length = 8)
    @NotNull
    private Boolean enabled;

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
