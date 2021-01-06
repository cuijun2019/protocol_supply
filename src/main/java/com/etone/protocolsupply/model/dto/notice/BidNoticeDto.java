package com.etone.protocolsupply.model.dto.notice;

import com.etone.protocolsupply.model.entity.notice.BidNotice;
import lombok.Data;

@Data
public class BidNoticeDto extends BidNotice {
    private String company;//关联users表拿到的字段
    private String username;//关联users表拿到的字段
}
