package com.etone.protocolsupply.model.entity.template;

import com.etone.protocolsupply.model.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author cuijun
 */
@Data
@Entity
@Table(name = "CONTRACT_TEMPLATE")
public class ContractTemplate implements Serializable {

    @Id
    @Column(name = "CONTRACT_TEMPLATE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    contractTemplateId;

    @Column(name = "CONTRACT_TEMPLATE_SUBJECT", length = 200)
    private String  contractTemplateSubject;

    @Column(name = "STATUS", length = 4)
    private Integer status;

    @Column(name = "CREATOR", length = 32)
    private String  creator;

    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date    createDate;

    @Column(name = "MAINTENANCE_MAN", length = 32)
    private String  maintenanceMan;

    @Column(name = "MAINTENANCE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date    maintenanceDate;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value={"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;
}
