package com.etone.protocolsupply.model.dto.notice;

import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import lombok.Data;

@Data
public class ContractNoticceDto extends ContractNotice {
    private String company;//关联users表拿到的字段
    private String username;//关联users表拿到的字段
}
