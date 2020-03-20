package com.etone.protocolsupply.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description //TODO
 * @Date 2018/12/9 下午5:58
 * @Author maozhihui
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class PageStatisticsDto {

    private Integer totalPages;
    private int pageSize;
    private int currentPage;
}
