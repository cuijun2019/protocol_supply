package com.etone.protocolsupply.model.entity.inquiry;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "INQUIRY_NOTES")
public class InquiryNotes implements Serializable {

    @Id
    @Column(name = "NOTES_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notesId;


    /**
     * 询价主题
     */
    @Column(name = "NOTES_THEME", length = 100)
    private String notesTheme;

    /**
     * 询价记录内容
     */
    @Column(name = "NOTES_CONTENT", length = 2000)
    private String notesContent;


    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 是否删除
     */
    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 创建人
     */
    @Column(name = "CREATOR", length = 32)
    private String creator;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;


    /**
     * 附件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;


    /**
     * 关联询价
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "INQUIRY_ID", referencedColumnName = "INQUIRY_ID")
    private InquiryInfoNew inquiryInfoNew;
}
